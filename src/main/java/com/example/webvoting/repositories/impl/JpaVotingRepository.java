package com.example.webvoting.repositories.impl;

import com.example.webvoting.models.Voting;
import com.example.webvoting.repositories.VotingRepository;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.NonUniqueResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException; // Загальний виняток JPA
import jakarta.persistence.TypedQuery;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Stateless
public class JpaVotingRepository implements VotingRepository {


    @PersistenceContext(unitName = "votingPU")
    private EntityManager em;

    @Override
    public List<Voting> findAll() {
        try {
            return em.createQuery("SELECT v FROM Voting v", Voting.class).getResultList();
        } catch (PersistenceException e) {
            return Collections.emptyList();
        }
    }

    @Override
    public List<Voting> findAll(Integer offset, Integer size, Boolean isActive){
        try{
            StringBuilder jpql = new StringBuilder("SELECT v FROM Voting v");

            if (isActive != null) {
                jpql.append(" WHERE v.isActive = :isActive");
            }

            jpql.append(" ORDER BY v.id");

            TypedQuery<Voting> query = em.createQuery(jpql.toString(), Voting.class);

            if (isActive != null) {
                query.setParameter("isActive", isActive);
            }

            query.setFirstResult(offset);
            query.setMaxResults(size);

            return query.getResultList();
        } catch (PersistenceException e) {
            return Collections.emptyList();
        }
    }

    @Override
    public List<Voting> findByCreatorId(UUID creatorId) {
        if (creatorId == null) {
            return Collections.emptyList();
        }
        try {
            TypedQuery<Voting> query = em.createQuery("SELECT v FROM Voting v WHERE v.creator.id = :creatorId", Voting.class);
            query.setParameter("creatorId", creatorId);
            return query.getResultList();
        } catch (PersistenceException e) {
            return Collections.emptyList();
        }
    }

    @Override
    public Optional<Voting> findById(UUID id) {
        if (id == null) {
            return Optional.empty();
        }
        try {
            return Optional.ofNullable(em.find(Voting.class, id));
        } catch (PersistenceException e) {
            return Optional.empty();
        }
    }

    @Override
    public Voting save(Voting voting) {
        if (voting == null) {
            throw new IllegalArgumentException("Voting entity cannot be null.");
        }
        if (voting.getId() == null) {
            em.persist(voting);
            return voting;
        } else {
            return em.merge(voting);
        }

    }

    @Override
    public void delete(UUID id) {
        if (id == null) {
            return;
        }
        try {
            Voting voting = em.find(Voting.class, id);
            if (voting != null) {
                em.remove(voting);
            }
        } catch (PersistenceException e) {
            return;
        }
    }

    @Override
    public boolean hasUserVoted(UUID votingId, UUID userId) {
        if (votingId == null || userId == null) {
            return false;
        }
        try {
            TypedQuery<Boolean> query = em.createQuery(
                    "SELECT CASE WHEN COUNT(v) > 0 THEN true ELSE false END " +
                            "FROM Voting vt JOIN vt.votes v " +
                            "WHERE vt.id = :votingId AND v.user.id = :userId",
                    Boolean.class);
            query.setParameter("votingId", votingId);
            query.setParameter("userId", userId);
            Boolean result = query.getSingleResult();
            return result != null && result;
        } catch (NoResultException e) {
            return false;
        } catch (NonUniqueResultException e) {
            return true;
        } catch (PersistenceException e) {
            throw e;
        }
    }

    @Override
    public void updateVotingStatus(UUID votingId, boolean isActive) {
        if (votingId == null) {
            throw new IllegalArgumentException("Voting ID cannot be null for status update.");
        }
        try {
            em.createQuery("UPDATE Voting v SET v.isActive = :isActive WHERE v.id = :votingId")
                    .setParameter("isActive", isActive)
                    .setParameter("votingId", votingId)
                    .executeUpdate();
        } catch (PersistenceException e) {
            throw e;
        }
    }
}