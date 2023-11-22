package com.puzzlemaker.service;

import com.puzzlemaker.comparison.ComparableRecord;
import com.puzzlemaker.comparison.fields.ComparableDouble;
import com.puzzlemaker.comparison.fields.ComparableInteger;
import com.puzzlemaker.comparison.fields.ComparableString;
import com.puzzlemaker.model.Game;
import com.puzzlemaker.model.User;
import com.puzzlemaker.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GameService {

    @NotNull
    private final GameRepository gameRepository;

    @NotNull
    private final UserService userService;

    @NotNull
    private final ActiveGameService activeGameService;

    public void populate() {

        String testUserId = userService.getTestUser()
                .map(User::getId)
                .orElseThrow(() -> new RuntimeException("No test user present while populating the database"));

        ComparableRecord record1 = new ComparableRecord(
                "Opcja1",
                List.of(
                        new ComparableInteger(1),
                        new ComparableDouble(0.04),
                        new ComparableString("Siema")
                )
        );

        ComparableRecord record2 = new ComparableRecord(
                "Opcja2",
                List.of(
                        new ComparableInteger(1),
                        new ComparableDouble(0.09),
                        new ComparableString("Hej")
                )
        );

        ComparableRecord record3 = new ComparableRecord(
                "Opcja3",
                List.of(
                        new ComparableInteger(2),
                        new ComparableDouble(0.04),
                        new ComparableString("Elo")
                )
        );

        List<ComparableRecord> gameData = List.of(record1, record2, record3);

        Game game = new Game(
                false,
                testUserId,
                "A test game",
                "It hopefully very fun",
                gameData
        );

        if (gameRepository.findGamesByUserId(testUserId).isEmpty()) {
            log.info("The test user does not have any games, adding a test game.");
            gameRepository.insert(game);
            userService.addGameToUsersCollection(testUserId, game.getId());
        }
    }

    public Optional<List<String>> playGameById(String gameId) {
        Optional<Game> game = gameRepository.findById(gameId);

        if (game.isEmpty()) {
            log.warn("There is no game by id {}, cannot initiate play", gameId);
            return Optional.empty();
        }

        return Optional.of(List.of(activeGameService.createActiveGame(game.orElseThrow())));
    }

    public Optional<String> rateGameById(String gameId, String login, Integer rating) {
        if (rating < 1 || rating > 5) {
            log.warn("The rating must be a value between 1 and 5, the current value is {}", rating);
            return Optional.empty();
        }

        Optional<Game> gameOptional = gameRepository.findById(gameId);

        if (gameOptional.isEmpty()) {
            log.warn("There is no game by id {}, cannot rate", gameId);
            return Optional.empty();
        }

        Optional<User> userOptional = userService.getUserByLogin(login);

        if (userOptional.isEmpty()) {
            log.warn("There is no user by login {}, cannot rate", login);
            return Optional.empty();
        }

        Game game = gameOptional.orElseThrow(() -> new IllegalStateException("Game not present despite being found."));
        User user = userOptional.orElseThrow(() -> new IllegalStateException("User not present despite being found."));

        if (user.getScores().stream().map(Pair::getLeft).noneMatch(gameScoreId -> game.getId().equals(gameScoreId))) {
            log.warn("The provided user {} has not played this game yet, cannot rate", login);
            return Optional.empty();
        }

        if (game.getRatings().stream().map(Pair::getLeft).anyMatch(userId -> user.getId().equals(userId))) {
            log.warn("The provided user {} has already rated the game, cannot rate", login);
            return Optional.empty();
        }

        game.getRatings().add(Pair.of(user.getId(), rating));
        return Optional.of(gameRepository.save(game).getId());
    }

    public Optional<Double> getGameRatings(String gameId) {
        Optional<Game> gameOptional = gameRepository.findById(gameId);

        if (gameOptional.isEmpty()) {
            log.warn("There is no game by id {}, cannot rate", gameId);
            return Optional.empty();
        }

        Game game = gameOptional.orElseThrow(() -> new IllegalStateException("Game not present despite being found."));

        OptionalDouble average = game.getRatings().stream().map(Pair::getValue).mapToInt(Integer::intValue).average();
        if (average.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(average.getAsDouble());
    }

    public Optional<String> createGame(List<ComparableRecord> gameData, boolean isPublic, String title, String desc) {
        Optional<String> userLogin = userService.getLoggedInUserId();

        if (userLogin.isEmpty()) {
            return Optional.empty();
        }

        Game newGame = new Game(
                isPublic,
                userLogin.orElseThrow(() -> new IllegalStateException("No username is present despite being found.")),
                title,
                desc,
                gameData
        );

        String result = gameRepository.insert(newGame).getId();
        userService.addGameToUsersCollection(newGame.getUserId(), result);
        return Optional.of(result);
    }
}
