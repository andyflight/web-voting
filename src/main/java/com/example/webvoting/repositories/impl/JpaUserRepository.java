package com.example.webvoting.repositories.impl;

import com.example.webvoting.models.User;
import com.example.webvoting.repositories.UserRepository;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;

import java.util.Optional;
import java.util.UUID;

@Stateless
public class JpaUserRepository implements UserRepository {

    @PersistenceContext(unitName = "votingPU")
    private EntityManager em;

    @Override
    public Optional<User> findById(UUID id) {
        try {
            return Optional.ofNullable(em.find(User.class, id));
        } catch (PersistenceException e) {
            return Optional.empty();
        }

    }

    @Override
    public Optional<User> findByName(String name) {
        try {
            User user = em.createQuery("SELECT u FROM User u WHERE u.name = :name", User.class)
                    .setParameter("name", name)
                    .getSingleResult();
            System.out.println("User found: " + user.getId() + user.getName());
            return Optional.of(user);
        } catch (PersistenceException e) {
            return Optional.empty();
        }
    }

    @Override
    public User save(User user) {
        if (user.getId() == null) {
            em.persist(user);
            return user;
        } else {
            return em.merge(user);
        }
    }
}
