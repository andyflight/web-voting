package com.example.webvoting.services;

import com.example.webvoting.models.Voting;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface VotingService {
    Voting createVoting(String title, List<String> candidateNames, UUID creatorId);
    List<Voting> getVotingsByCreatorId(UUID creatorId);
    List<Voting> getAllVotings();
    Voting getVotingById(UUID id);
    void deleteVoting(UUID id);
    boolean hasUserVoted(UUID votingId, UUID userId);
    void vote(UUID votingId, UUID userId, UUID candidateId);
    void startVoting(UUID votingId);
    void stopVoting(UUID votingId);
    Map<String, Integer> getVotes(UUID votingId); // Candidate name and number of votes
    String generateVotingLink(HttpServletRequest req, UUID votingId);
    String generateResultsLink(HttpServletRequest req, UUID votingId);

}
