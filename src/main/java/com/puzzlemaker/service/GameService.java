package com.puzzlemaker.service;

import com.puzzlemaker.comparison.ComparableRecord;
import com.puzzlemaker.comparison.fields.ComparableDouble;
import com.puzzlemaker.comparison.fields.ComparableInteger;
import com.puzzlemaker.comparison.fields.ComparableString;
import com.puzzlemaker.model.Game;
import com.puzzlemaker.model.User;
import com.puzzlemaker.repository.GameRepository;
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
public class GameService {

    @NonNull
    private final GameRepository gameRepository;

    @NonNull
    private final UserService userService;

    @NonNull
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

        if (gameRepository.findGamesByUserId(testUserId).size() == 0) {
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
}