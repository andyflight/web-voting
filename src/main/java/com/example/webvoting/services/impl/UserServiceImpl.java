package com.example.webvoting.services.impl;

import com.example.webvoting.exceptions.UserAlreadyExistsException;
import com.example.webvoting.exceptions.UserNotFoundException;
import com.example.webvoting.models.User;
import com.example.webvoting.repositories.UserRepository;
import com.example.webvoting.services.UserService;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.persistence.PersistenceException;

import java.util.Optional;
import java.util.UUID;

@Stateless
public class UserServiceImpl implements UserService {

    @EJB
    private UserRepository userRepository;

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    @Override
    public User getUserById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found with id " + id));
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    @Override
    public Optional<User> getUserByName(String name) {
        if (name == null) {
            return Optional.empty();
        }
        return userRepository.findByName(name);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @Override
    public User createUser(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("User name cannot be null or empty");
        }
        User user = new User();
        user.setName(name);
        try {
            return userRepository.save(user);
        } catch (PersistenceException e) {
            throw new UserAlreadyExistsException("User with name " + name + " already exists");
        }
    }

}
