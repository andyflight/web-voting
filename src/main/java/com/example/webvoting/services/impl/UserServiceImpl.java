package com.example.webvoting.services.impl;

import com.example.webvoting.exceptions.UserNotFoundException;
import com.example.webvoting.models.User;
import com.example.webvoting.repositories.UserRepository;
import com.example.webvoting.repositories.impl.StubUserRepository;
import com.example.webvoting.services.UserService;

import java.util.Optional;
import java.util.UUID;

public class UserServiceImpl implements UserService {

    private UserRepository userRepository = StubUserRepository.getInstance();

    @Override
    public User getUserById(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found with id " + id.toString()));
    }

    @Override
    public Optional<User> getUserByName(String name) {
        return userRepository.findByName(name);
    }

    @Override
    public User createUser(String name) {
        User user = new User();
        user.setName(name);
        return userRepository.save(user);
    }

}
