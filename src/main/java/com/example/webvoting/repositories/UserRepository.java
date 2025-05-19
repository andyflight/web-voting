package com.example.webvoting.repositories;

import com.example.webvoting.models.User;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    Optional<User> findById(UUID id);
    Optional<User> findByName(String name);
    User save(User user);
}
