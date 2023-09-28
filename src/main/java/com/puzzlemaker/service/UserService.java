package com.puzzlemaker.service;

import com.puzzlemaker.model.User;
import com.puzzlemaker.repository.UserRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserService {

    private static final String TEST_USER_LOGIN = "test";

    @NonNull
    private final UserRepository userRepository;

    public void populate() {
        if (getTestUser().isPresent()) {
            return;
        }
        log.info("Test user not detected, inserting test user.");

        User testUser = new User(
                TEST_USER_LOGIN,
                "A user for testing purposes",
                List.of()
        );

        userRepository.insert(testUser);
    }

    public void addGameToUsersCollection(String userId, String gameId) {
        getUserById(userId).ifPresentOrElse(
                user -> {
                    user.getGamesIds().add(gameId);
                    userRepository.save(user);
                },
                () -> log.warn("The user by the id {} does not exist, cannot add a game to his collection", userId)
        );
    }

    public Optional<User> getUserById(String id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByLogin(String login) {
        return userRepository.findUserByLogin(login);
    }

    public Optional<User> getTestUser() {
        return userRepository.findUserByLogin(TEST_USER_LOGIN);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
