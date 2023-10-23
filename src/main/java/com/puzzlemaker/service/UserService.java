package com.puzzlemaker.service;

import com.puzzlemaker.model.User;
import com.puzzlemaker.model.UserRole;
import com.puzzlemaker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserService implements UserDetailsService {

    private static final String TEST_USER_LOGIN = "test";
    private static final String ADMIN_USER_LOGIN = "admin";

    @NotNull
    private final UserRepository userRepository;

    @NotNull
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        return userRepository.findUserByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("There exist no users with login " + login));
    }

    public void populate() {
        populateAdminUser();

        if (getTestUser().isPresent()) {
            return;
        }
        log.info("Test user not detected, inserting test user.");

        User testUser = new User(
                TEST_USER_LOGIN,
                passwordEncoder.encode("password"),
                List.of(),
                UserRole.USER,
                false,
                true
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

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User addUser(User user) {
        return userRepository.save(user);
    }

    public Optional<User> getTestUser() {
        return userRepository.findUserByLogin(TEST_USER_LOGIN);
    }

    private Optional<User> getAdminUser() {
        return userRepository.findUserByLogin(ADMIN_USER_LOGIN);
    }

    private void populateAdminUser() {
        if (getAdminUser().isPresent()) {
            return;
        }
        log.info("Admin user not detected, inserting admin user.");

        User admin = new User(
                ADMIN_USER_LOGIN,
                passwordEncoder.encode("admin"),
                List.of(),
                UserRole.ADMIN,
                false,
                true
        );

        userRepository.insert(admin);
    }
}
