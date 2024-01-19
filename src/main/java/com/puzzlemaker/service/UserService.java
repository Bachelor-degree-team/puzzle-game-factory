package com.puzzlemaker.service;

import com.puzzlemaker.model.User;
import com.puzzlemaker.model.UserRole;
import com.puzzlemaker.model.dto.GameHistoryDTO;
import com.puzzlemaker.repository.GameRepository;
import com.puzzlemaker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final GameRepository gameRepository;

    @NotNull
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        return userRepository.findUserByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("There exist no users with login " + login));
    }

    public boolean addScoreToUser(String login, String gameId, Integer score) {
        if (score < 1) {
            log.warn("Trying to add a score that is less than 1! Aborting.");
            return false;
        }

        getUserByLogin(login).ifPresentOrElse(
                user -> {
                    user.getScores().add(Pair.of(gameId, score));
                    userRepository.save(user);
                },
                () -> log.warn("The user by the login {} does not exist, cannot add a score", login)
        );
        return true;
    }

    public Optional<List<GameHistoryDTO>> getScores(String login) {
        Optional<User> userOptional = getUserByLogin(login);

        if (userOptional.isEmpty()) {
            return Optional.empty();
        }

        User user = userOptional.orElseThrow(() -> new IllegalStateException("No user present despite being found."));

        return Optional.of(user.getScores().stream().map(pair -> new GameHistoryDTO(
                gameRepository.findById(pair.getLeft()).orElseThrow().getTitle(),
                pair.getRight(),
                pair.getLeft()
        )).toList());
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

    public void removeGameFromUsersCollection(String gameId) {
        userRepository.findUserByGamesIdsContaining(gameId).ifPresentOrElse(
                user -> {
                    user.getGamesIds().remove(gameId);
                    userRepository.save(user);
                },
                () -> log.warn("The user with a game id {} does not exist, cannot remove a game from his collection", gameId)
        );
    }

    public void removeUserById(String id) {
        getUserById(id).ifPresent(user -> {
            gameRepository.deleteGamesByUserId(id);
            userRepository.delete(user);
        });
    }

    public Optional<User> getUserById(String id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByLogin(String login) {
        return userRepository.findUserByLogin(login);
    }

    public Optional<String> getLoggedInUserLogin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            return Optional.of(authentication.getName());
        }
        return Optional.empty();
    }

    public Optional<String> getLoggedInUserId() {
        Optional<String> userLogin = getLoggedInUserLogin();
        if (userLogin.isEmpty()) {
            return Optional.empty();
        }

        return userRepository
                .findUserByLogin(userLogin.orElseThrow(() -> new IllegalStateException("No login despite being found!")))
                .map(User::getId);
    }

    public Optional<String> setUserLocked(String id, Boolean locked) {
        Optional<User> userOptional = getUserById(id);

        if (userOptional.isEmpty()) {
            log.warn("There is no user by id {}, cannot block", id);
            return Optional.empty();
        }

        User user = userOptional.orElseThrow(() -> new IllegalStateException("User not present despite being found."));

        user.setLocked(locked);
        return Optional.of(userRepository.save(user).getId());
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

    public Optional<User> getAdminUser() {
        return userRepository.findUserByLogin(ADMIN_USER_LOGIN);
    }

    private void populateAdminUser() {
        if (getAdminUser().isPresent()) {
            return;
        }
        log.info("Admin user not detected, inserting admin user.");

        User admin = new User(
                ADMIN_USER_LOGIN,
                passwordEncoder.encode(ADMIN_USER_LOGIN),
                List.of(),
                UserRole.ADMIN,
                false,
                true
        );

        userRepository.insert(admin);
    }
}
