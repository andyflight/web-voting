package com.example.webvoting.repositories;

import com.example.webvoting.models.Voting;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VotingRepository {
    List<Voting> findAll();
    List<Voting> findAll(Integer offset, Integer size, Boolean isActive);
    List<Voting> findByCreatorId(UUID creatorId);
    Optional<Voting> findById(UUID id);
    Voting save(Voting voting);
    void delete(UUID id);
    boolean hasUserVoted(UUID votingId, UUID userId);
    void updateVotingStatus(UUID votingId, boolean isActive);
}
