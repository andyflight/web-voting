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
import com.example.webvoting.repositories.impl.StubVotingRepository;
import com.example.webvoting.services.VotingService;
import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class VotingServiceImpl implements VotingService {

    private VotingRepository votingRepository = StubVotingRepository.getInstance();
    private UserServiceImpl userService = new UserServiceImpl();


    @Override
    public Voting createVoting(String title, List<String> candidateNames, UUID creatorId) {
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
        return votingRepository.findByCreatorId(creatorId);
    }

    @Override
    public List<Voting> getAllVotings() {
        return votingRepository.findAll();
    }

    @Override
    public Voting getVotingById(UUID id) {
        return votingRepository.findById(id).orElseThrow(() -> new VotingNotFoundException("Voting not found with id " + id.toString()));
    }

    @Override
    public void deleteVoting(UUID id) {
        votingRepository.delete(id);
    }

    @Override
    public boolean hasUserVoted(UUID votingId, UUID userId) {
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


        voting.getVotes().add(new Vote(UUID.randomUUID(), user, candidate));
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
        return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/votings/" + votingId.toString();
    }

    @Override
    public String generateResultsLink(HttpServletRequest request, UUID votingId) {
        return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/results/" + votingId.toString();

    }

}
