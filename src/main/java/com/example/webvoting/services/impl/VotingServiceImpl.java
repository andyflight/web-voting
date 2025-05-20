package com.example.webvoting.services.impl;

import com.example.webvoting.exceptions.*;
import com.example.webvoting.models.Candidate;
import com.example.webvoting.models.User;
import com.example.webvoting.models.Vote;
import com.example.webvoting.models.Voting;
import com.example.webvoting.repositories.VotingRepository;
import com.example.webvoting.services.UserService;
import com.example.webvoting.services.VotingService;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.persistence.PersistenceException;
import jakarta.servlet.http.HttpServletRequest;
import org.hibernate.event.spi.PersistContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Stateless
public class VotingServiceImpl implements VotingService {

    @EJB
    private VotingRepository votingRepository;

    @EJB
    private UserService userService;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @Override
    public Voting createVoting(String title, List<String> candidateNames, UUID creatorId) {

        if (title == null || title.isBlank() || candidateNames == null || candidateNames.isEmpty() || creatorId == null) {
            throw new IllegalArgumentException("Invalid input");
        }

        try {
            Voting voting = new Voting();
            voting.setTitle(title);
            voting.setCreator(userService.getUserById(creatorId));
            voting.setVotes(new ArrayList<>());
            voting.setIsActive(true);
            voting.setCandidates(candidateNames.stream().map(name -> {
                Candidate candidate = new Candidate();
                candidate.setName(name);
                return candidate;
            }).collect(Collectors.toList()));


            return votingRepository.save(voting);
        } catch (PersistenceException e) {
            throw new VotingDataException(e.getMessage());
        }
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    @Override
    public List<Voting> getVotingsByCreatorId(UUID creatorId) {
        if (creatorId == null) {
            throw new IllegalArgumentException("Creator ID cannot be null");
        }
        return votingRepository.findByCreatorId(creatorId);
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    @Override
    public List<Voting> getAllVotings() {
        return votingRepository.findAll();
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    @Override
    public Voting getVotingById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Voting ID cannot be null");
        }
        return votingRepository.findById(id).orElseThrow(() -> new VotingNotFoundException("Voting not found with id " + id));
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @Override
    public void deleteVoting(UUID id) {
        if (id == null) {
            return;
        }
        votingRepository.delete(id);
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    @Override
    public boolean hasUserVoted(UUID votingId, UUID userId) {
        if (votingId == null || userId == null) {
            throw new IllegalArgumentException("Voting ID and User ID cannot be null");
        }
        try {
            return votingRepository.hasUserVoted(votingId, userId);
        } catch (PersistenceException e) {
            throw new VotingDataException(e.getMessage());
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @Override
    public void vote(UUID votingId, UUID userId, UUID candidateId) {

        Voting voting = getVotingById(votingId);
        if (!voting.getIsActive()) {
            throw new VotingNotActiveException("Voting is not active");
        }

        if (hasUserVoted(votingId, userId)) {
            throw new UserHasAlreadyVotedException("User has already voted");
        }

        try {
            User user = userService.getUserById(userId);

            Candidate candidate = voting.getCandidates().stream()
                    .filter(c -> c.getId().equals(candidateId))
                    .findFirst()
                    .orElseThrow(() -> new CandidateNotFoundException("Candidate not found"));

            Vote vote = new Vote();
            vote.setCandidate(candidate);
            vote.setUser(user);
            if (voting.getVotes() == null) {
                voting.setVotes(new ArrayList<>());
            }

            voting.getVotes().add(vote);
        } catch (PersistenceException e) {
            throw new VotingDataException(e.getMessage());
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @Override
    public void startVoting(UUID votingId) {
        Voting voting = getVotingById(votingId);
        voting.setIsActive(true);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @Override
    public void stopVoting(UUID votingId) {
        Voting voting = getVotingById(votingId);
        voting.setIsActive(false);
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    @Override
    public Map<String, Integer> getVotes(UUID votingId) {
        Voting voting = getVotingById(votingId);

        return voting.getVotes().stream()
                .collect(Collectors.groupingBy(vote -> vote.getCandidate().getName(), Collectors.summingInt(vote -> 1)));
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    @Override
    public String generateVotingLink(HttpServletRequest request, UUID votingId) {
        if (votingId == null) {
            throw new IllegalArgumentException("Voting ID cannot be null");
        }
        return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/votings/" + votingId;
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    @Override
    public String generateResultsLink(HttpServletRequest request, UUID votingId) {
        if (votingId == null) {
            throw new IllegalArgumentException("Voting ID cannot be null");
        }
        return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/results/" + votingId;

    }

}
