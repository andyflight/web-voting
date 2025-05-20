package com.example.webvoting.models;


import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "votes")
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "candidate_id", nullable = false)
    private Candidate candidate;

    public Vote() {
        // Default constructor
    }

    public Vote(UUID id, User user, Candidate candidate) {
        this.id = id;
        this.user = user;
        this.candidate = candidate;
    }

    public UUID getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Candidate getCandidate() {
        return candidate;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setCandidate(Candidate candidate) {
        this.candidate = candidate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vote)) return false;

        Vote vote = (Vote) o;

        if (!id.equals(vote.id)) return false;
        return (user.equals(vote.user) && candidate.equals(vote.candidate));
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + user.hashCode();
        result = 31 * result + candidate.hashCode();
        return result;
    }

}
