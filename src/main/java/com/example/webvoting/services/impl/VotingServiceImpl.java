package com.example.webvoting.services.impl;

import com.example.webvoting.exceptions.CandidateNotFoundException;
import com.example.webvoting.exceptions.UserHasAlreadyVotedException;
import com.example.webvoting.exceptions.VotingNotActiveException;
import com.example.webvoting.exceptions.VotingNotFoundException;
import com.example.webvoting.models.Candidate;
import com.example.webvoting.models.User;
import com.example.webvoting.models.Vote;
import com.example.webvoting.models.Voting;
import com.example.webvoting.repositories.VotingRepository;
import com.example.webvoting.services.UserService;
import com.example.webvoting.services.VotingService;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.servlet.http.HttpServletRequest;

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


    @Override
    public Voting createVoting(String title, List<String> candidateNames, UUID creatorId) {

        if (title == null || title.isBlank() || candidateNames == null || candidateNames.isEmpty() || creatorId == null) {
            throw new IllegalArgumentException("Invalid input");
        }

        Voting voting = new Voting();
        voting.setTitle(title);
        voting.setCreator(userService.getUserById(creatorId));
        voting.setVotes(new ArrayList<>());
        voting.setIsActive(true);
        voting.setCandidates(candidateNames.stream().map(name -> {
            Candidate candidate = new Candidate();
            candidate.setId(UUID.randomUUID());
            candidate.setName(name);
            return candidate;
        }).collect(Collectors.toList()));

        return votingRepository.save(voting);
    }

    @Override
    public List<Voting> getVotingsByCreatorId(UUID creatorId) {
        if (creatorId == null) {
            throw new IllegalArgumentException("Creator ID cannot be null");
        }
        return votingRepository.findByCreatorId(creatorId);
    }

    @Override
    public List<Voting> getAllVotings() {
        return votingRepository.findAll();
    }

    @Override
    public Voting getVotingById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Voting ID cannot be null");
        }
        return votingRepository.findById(id).orElseThrow(() -> new VotingNotFoundException("Voting not found with id " + id));
    }

    @Override
    public void deleteVoting(UUID id) {
        if (id == null) {
            return;
        }
        votingRepository.delete(id);
    }

    @Override
    public boolean hasUserVoted(UUID votingId, UUID userId) {
        if (votingId == null || userId == null) {
            throw new IllegalArgumentException("Voting ID and User ID cannot be null");
        }
        return votingRepository.hasUserVoted(votingId, userId);
    }

    @Override
    public void vote(UUID votingId, UUID userId, UUID candidateId) {

        Voting voting = getVotingById(votingId);
        if (!voting.getIsActive()) {
            throw new VotingNotActiveException("Voting is not active");
        }

        if (hasUserVoted(votingId, userId)) {
            throw new UserHasAlreadyVotedException("User has already voted");
        }

        User user = userService.getUserById(userId);

        Candidate candidate = voting.getCandidates().stream()
                .filter(c -> c.getId().equals(candidateId))
                .findFirst()
                .orElseThrow(() -> new CandidateNotFoundException("Candidate not found"));
        
        Vote vote = new Vote(UUID.randomUUID(), user, candidate);
        if (voting.getVotes() == null) {
            voting.setVotes(new ArrayList<>());
        }

        voting.getVotes().add(vote);
    }

    @Override
    public void startVoting(UUID votingId) {
        Voting voting = getVotingById(votingId);
        voting.setIsActive(true);
        votingRepository.save(voting);
    }

    @Override
    public void stopVoting(UUID votingId) {
        Voting voting = getVotingById(votingId);
        voting.setIsActive(false);
        votingRepository.save(voting);
    }

    @Override
    public Map<String, Integer> getVotes(UUID votingId) {
        Voting voting = getVotingById(votingId);

        return voting.getVotes().stream()
                .collect(Collectors.groupingBy(vote -> vote.getCandidate().getName(), Collectors.summingInt(vote -> 1)));
    }

    @Override
    public String generateVotingLink(HttpServletRequest request, UUID votingId) {
        if (votingId == null) {
            throw new IllegalArgumentException("Voting ID cannot be null");
        }
        return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/votings/" + votingId;
    }

    @Override
    public String generateResultsLink(HttpServletRequest request, UUID votingId) {
        if (votingId == null) {
            throw new IllegalArgumentException("Voting ID cannot be null");
        }
        return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/results/" + votingId;

    }

}
