package com.example.webvoting.repositories.impl;

import com.example.webvoting.models.User;
import com.example.webvoting.repositories.UserRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class StubUserRepository implements UserRepository {

    private static final UserRepository instance = new StubUserRepository();
    private final Map<UUID, User> users = new HashMap<>();

    private StubUserRepository() {
        User user = new User();
        user.setId(UUID.fromString("d9d42288-e6eb-4d11-8dcd-f919bbeba434"));
        user.setName("John Doe");
        save(user);
    }

    public static UserRepository getInstance() {
        return instance;
    }

    @Override
    public Optional<User> findById(UUID id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public Optional<User> findByName(String name) {
        return users.values().stream()
                .filter(user -> user.getName().equals(name))
                .findFirst();
    }

    @Override
    public User save(User user) {
        if (user.getId() == null) {
            user.setId(UUID.randomUUID());
        }
        users.put(user.getId(), user);
        return user;
    }
}
