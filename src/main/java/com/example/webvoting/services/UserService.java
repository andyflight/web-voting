package com.example.webvoting.services;

import com.example.webvoting.models.User;

import java.util.Optional;
import java.util.UUID;

public interface UserService {
    User getUserById(UUID id);
    Optional<User> getUserByName(String name);
    User createUser(String name);
}
