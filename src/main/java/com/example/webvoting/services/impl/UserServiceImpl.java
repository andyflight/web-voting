package com.example.webvoting.services.impl;

import com.example.webvoting.exceptions.UserNotFoundException;
import com.example.webvoting.models.User;
import com.example.webvoting.repositories.UserRepository;
import com.example.webvoting.services.UserService;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;

import java.util.Optional;
import java.util.UUID;

@Stateless
public class UserServiceImpl implements UserService {

    @EJB
    private UserRepository userRepository;

    @Override
    public User getUserById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found with id " + id));
    }

    @Override
    public Optional<User> getUserByName(String name) {
        if (name == null) {
            return Optional.empty();
        }
        return userRepository.findByName(name);
    }

    @Override
    public User createUser(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("User name cannot be null or empty");
        }
        User user = new User();
        user.setName(name);
        return userRepository.save(user);
    }

}
