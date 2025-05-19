package com.example.webvoting.repositories.impl;

import com.example.webvoting.models.Candidate;
import com.example.webvoting.models.User;
import com.example.webvoting.models.Voting;
import com.example.webvoting.repositories.VotingRepository;

import java.util.*;
import java.util.stream.Collectors;

public class StubVotingRepository implements VotingRepository {
    
    private static final StubVotingRepository instance = new StubVotingRepository();
    private final Map<UUID, Voting> votings = new HashMap<>();
    
    private StubVotingRepository() {
        User user = new User();
        user.setId(UUID.fromString("d9d42288-e6eb-4d11-8dcd-f919bbeba434"));
        user.setName("John Doe");
        
        Voting voting = new Voting();
        voting.setId(UUID.fromString("d9d42288-e6eb-4d11-8dcd-f919bbeba435"));
        voting.setTitle("Best Programming Language");
        voting.setCreator(user);
        voting.setIsActive(true);
        voting.setVotes(new ArrayList<>());
        voting.setCandidates(new ArrayList<>(List.of(
                new Candidate(UUID.randomUUID(), "Java"),
                new Candidate(UUID.randomUUID(), "C#"),
                new Candidate(UUID.randomUUID(), "JavaScript")
        )));
        save(voting);
    }
    
    public static VotingRepository getInstance() {
        return instance;
    }
    
    @Override
    public List<Voting> findAll() {
        return new ArrayList<>(votings.values());
    }
    
    @Override
    public List<Voting> findByCreatorId(UUID id) {
        return votings.values().stream()
                .filter(voting -> voting.getCreator().getId().equals(id))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Voting> findById(UUID id) {
        return Optional.ofNullable(votings.get(id));
    }

    @Override
    public Voting save(Voting voting) {
        if (voting.getId() == null) {
            voting.setId(UUID.randomUUID());
        }
        votings.put(voting.getId(), voting);
        return voting;
    }

    @Override
    public void delete(UUID id) {
        votings.remove(id);
    }

    @Override
    public boolean hasUserVoted(UUID votingId, UUID userId) {
        Voting voting = votings.get(votingId);
        if (voting == null) {
            return false;
        }
        return voting.getVotes().stream()
                .anyMatch(vote -> vote.getUser().getId().equals(userId));
    }

    @Override
    public void updateVotingStatus(UUID votingId, boolean isActive) {
        Voting voting = votings.get(votingId);
        if (voting != null) {
            voting.setIsActive(isActive);
        }
    }
    
}
