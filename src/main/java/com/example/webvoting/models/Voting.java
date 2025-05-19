package com.example.webvoting.models;

import java.util.List;
import java.util.UUID;

public class Voting {
    private UUID id;
    private User creator;
    private List<Vote> votes;
    private List<Candidate> candidates;
    private String title;
    private Boolean isActive;

    public Voting(){
        // Default constructor
    }

    public Voting(UUID id, User creator, List<Vote> votes, List<Candidate> candidates, String title, Boolean isActive) {
        this.id = id;
        this.creator = creator;
        this.votes = votes;
        this.candidates = candidates;
        this.title = title;
        this.isActive = isActive;
    }

    public UUID getId() {
        return id;
    }

    public User getCreator() {
        return creator;
    }

    public List<Vote> getVotes() {
        return votes;
    }

    public List<Candidate> getCandidates() {
        return candidates;
    }

    public String getTitle() {
        return title;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public void setVotes(List<Vote> votes) {
        this.votes = votes;
    }

    public void setCandidates(List<Candidate> candidates) {
        this.candidates = candidates;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Voting)) return false;

        Voting voting = (Voting) o;

        if (!id.equals(voting.id)) return false;

        return (
                creator.equals(voting.creator) &&
                votes.equals(voting.votes) &&
                candidates.equals(voting.candidates) &&
                title.equals(voting.title) &&
                isActive.equals(voting.isActive)
        );
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + creator.hashCode();
        result = 31 * result + votes.hashCode();
        result = 31 * result + candidates.hashCode();
        result = 31 * result + title.hashCode();
        result = 31 * result + isActive.hashCode();
        return result;
    }

}
