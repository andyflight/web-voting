package com.example.webvoting.util;

import com.example.webvoting.models.User;
import com.example.webvoting.models.Voting;
import com.example.webvoting.services.UserService;
import com.example.webvoting.services.VotingService;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.Arrays;
import java.util.UUID;

@Singleton
@Startup
public class DBInitializer {

    @EJB
    private UserService userService;

    @EJB
    private VotingService votingService;

    @PersistenceContext(unitName = "votingPU")
    private EntityManager em;

    @PostConstruct
    public void init() {
        if (isDatabaseEmpty()) {
            initializeTestData();
        }
    }

    private boolean isDatabaseEmpty() {
        Long userCount = em.createQuery("SELECT COUNT(u) FROM User u", Long.class).getSingleResult();
        return userCount == 0;
    }

    @Transactional
    private void initializeTestData() {
        try {
            User admin = userService.createUser("admin");
            User user1 = userService.createUser("user1");
            User user2 = userService.createUser("user2");
            User user3 = userService.createUser("user3");
            
            Voting presidentialVoting = votingService.createVoting(
                    "Presidential Election 2024",
                    Arrays.asList("John Smith", "Sarah Johnson", "Michael Brown"),
                    admin.getId()
            );
            
            Voting bestMovieVoting = votingService.createVoting(
                    "Best Movie 2023",
                    Arrays.asList("Oppenheimer", "Barbie", "Dune: Part Two", "The Batman"),
                    user1.getId()
            );
            
            Voting favoriteLanguageVoting = votingService.createVoting(
                    "Favorite Programming Language",
                    Arrays.asList("Java", "Python", "JavaScript", "C#", "Go", "Rust"),
                    admin.getId()
            );
            
            vote(presidentialVoting.getId(), user1.getId(), presidentialVoting.getCandidates().get(0).getId()); // User1 голосує за John Smith
            vote(presidentialVoting.getId(), user2.getId(), presidentialVoting.getCandidates().get(1).getId()); // User2 голосує за Sarah Johnson
            vote(presidentialVoting.getId(), user3.getId(), presidentialVoting.getCandidates().get(1).getId()); // User3 голосує за Sarah Johnson
            
            vote(bestMovieVoting.getId(), admin.getId(), bestMovieVoting.getCandidates().get(0).getId()); // Admin голосує за Oppenheimer
            vote(bestMovieVoting.getId(), user2.getId(), bestMovieVoting.getCandidates().get(1).getId()); // User2 голосує за Barbie
            vote(bestMovieVoting.getId(), user3.getId(), bestMovieVoting.getCandidates().get(0).getId()); // User3 голосує за Oppenheimer
            
            votingService.stopVoting(bestMovieVoting.getId());
            
            System.out.println("Test data has been successfully initialized!");
        } catch (Exception e) {
            System.err.println("Error initializing test data: " + e.getMessage());
        }
    }
    
    private void vote(UUID votingId, UUID userId, UUID candidateId) {
        try {
            votingService.vote(votingId, userId, candidateId);
        } catch (Exception e) {
            System.err.println("Error adding vote: " + e.getMessage());
        }
    }
}
