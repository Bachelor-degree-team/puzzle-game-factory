package com.puzzlemaker.service;

import com.puzzlemaker.comparison.ComparableRecord;
import com.puzzlemaker.comparison.fields.ComparableDouble;
import com.puzzlemaker.comparison.fields.ComparableInteger;
import com.puzzlemaker.comparison.fields.ComparableString;
import com.puzzlemaker.model.Game;
import com.puzzlemaker.model.Session;
import com.puzzlemaker.model.User;
import com.puzzlemaker.model.dto.GameDTO;
import com.puzzlemaker.model.dto.GameListDTO;
import com.puzzlemaker.model.dto.UserDTO;
import com.puzzlemaker.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GameService {

    private static final String EXAMPLE_GAME_TITLE = "Example Game";

    @NotNull
    private final GameRepository gameRepository;

    @NotNull
    private final UserService userService;

    @NotNull
    private final SessionService sessionService;

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

        populateExampleAdminGame();
    }

    public void populateExampleAdminGame() {

        String adminUserId = userService.getAdminUser()
                .map(User::getId)
                .orElseThrow(() -> new RuntimeException("No admin user present while populating the database"));

        ComparableRecord record0 = new ComparableRecord(
                "name",
                List.of(
                        new ComparableString("Type"),
                        new ComparableString("Subtype"),
                        new ComparableString("Dominant Color"),
                        new ComparableString("Mass (KG)"),
                        new ComparableString("Lifespan (Yrs)")
                )
        );

        ComparableRecord record1 = new ComparableRecord(
                "Human",
                List.of(
                        new ComparableString("Animal"),
                        new ComparableString("Mammal"),
                        new ComparableString("Milk coffee"),
                        new ComparableDouble(70.0),
                        new ComparableInteger(80)
                )
        );

        ComparableRecord record2 = new ComparableRecord(
                "Woolly Mammoth",
                List.of(
                        new ComparableString("Animal"),
                        new ComparableString("Mammal"),
                        new ComparableString("Brown"),
                        new ComparableDouble(5000.0),
                        new ComparableInteger(60)
                )
        );

        ComparableRecord record3 = new ComparableRecord(
                "Rat",
                List.of(
                        new ComparableString("Animal"),
                        new ComparableString("Mammal"),
                        new ComparableString("Gray"),
                        new ComparableDouble(0.2),
                        new ComparableInteger(3)
                )
        );

        ComparableRecord record4 = new ComparableRecord(
                "Crocodile",
                List.of(
                        new ComparableString("Animal"),
                        new ComparableString("Reptile"),
                        new ComparableString("Green"),
                        new ComparableDouble(1250.0),
                        new ComparableInteger(60)
                )
        );

        ComparableRecord record5 = new ComparableRecord(
                "Chameleon",
                List.of(
                        new ComparableString("Animal"),
                        new ComparableString("Reptile"),
                        new ComparableString("Changes color"),
                        new ComparableDouble(0.2),
                        new ComparableInteger(5)
                )
        );

        ComparableRecord record6 = new ComparableRecord(
                "Oak",
                List.of(
                        new ComparableString("Plant"),
                        new ComparableString("Tree"),
                        new ComparableString("Brown"),
                        new ComparableDouble(10000.0),
                        new ComparableInteger(600)
                )
        );

        ComparableRecord record7 = new ComparableRecord(
                "Birch",
                List.of(
                        new ComparableString("Plant"),
                        new ComparableString("Tree"),
                        new ComparableString("White"),
                        new ComparableDouble(1000.0),
                        new ComparableInteger(100)
                )
        );

        ComparableRecord record8 = new ComparableRecord(
                "Tulip",
                List.of(
                        new ComparableString("Plant"),
                        new ComparableString("Flower"),
                        new ComparableString("Many colors"),
                        new ComparableDouble(0.035),
                        new ComparableInteger(2)
                )
        );

        ComparableRecord record9 = new ComparableRecord(
                "Daisy",
                List.of(
                        new ComparableString("Plant"),
                        new ComparableString("Flower"),
                        new ComparableString("White"),
                        new ComparableDouble(0.025),
                        new ComparableInteger(0)
                )
        );

        ComparableRecord record10 = new ComparableRecord(
                "Rose",
                List.of(
                        new ComparableString("Plant"),
                        new ComparableString("Flower"),
                        new ComparableString("Red"),
                        new ComparableDouble(0.05),
                        new ComparableInteger(7)
                )
        );

        List<ComparableRecord> gameData = List.of(
                record0,
                record1,
                record2,
                record3,
                record4,
                record5,
                record6,
                record7,
                record8,
                record9,
                record10
        );

        Game game = new Game(
                false,
                adminUserId,
                EXAMPLE_GAME_TITLE,
                "Guess out of 10 well known living beings. Observe how the game progresses and make educated guesses based on previous answers!",
                gameData
        );

        if (gameRepository.findGamesByUserId(adminUserId).isEmpty()) {
            log.info("The admin user does not have any games, adding the example game.");
            gameRepository.insert(game);
            userService.addGameToUsersCollection(adminUserId, game.getId());
        }
    }

    public Optional<String> getExampleGameId() {
        String adminUserId = userService.getAdminUser()
                .map(User::getId)
                .orElseThrow(() -> new RuntimeException("No admin user present while fetching example game"));

        return gameRepository.findGamesByUserId(adminUserId)
                .stream()
                .filter(game -> EXAMPLE_GAME_TITLE.equals(game.getTitle()))
                .map(Game::getId)
                .findFirst();
    }

    public Optional<String> getRandomGameId() {
        List<String> allIds = gameRepository.findAll().stream().filter(Game::isPublic).map(Game::getId).toList();

        if (CollectionUtils.isEmpty(allIds)) {
            return Optional.empty();
        }

        int randomIndex = new Random().nextInt(allIds.size());
        return Optional.of(allIds.get(randomIndex));
    }

    public Optional<GameDTO> getGameById(String gameId) {
        return gameRepository.findById(gameId).map(GameDTO::fromGame);
    }

    public Optional<Game> getGameObjectById(String gameId) {
        return gameRepository.findById(gameId);
    }

    public List<GameDTO> getAllPublicGameDtos() {
        return gameRepository.findAll()
                .stream()
                .filter(Game::isPublic)
                .map(GameDTO::fromGame)
                .sorted(Comparator.comparing(GameDTO::rating).reversed())
                .toList();
    }

    public Optional<List<GameListDTO>> getGameList(String login) {
        List<String> ids = userService.getUserByLogin(login).map(User::getGamesIds).orElseThrow();

        return Optional.of(ids.stream().map(this::getGameObjectById).map(Optional::get).map(GameListDTO::fromGame).toList());
    }

    public Optional<String> playGameById(String gameId) {
        Optional<Game> game = gameRepository.findById(gameId);

        if (game.isEmpty()) {
            log.warn("There is no game by id {}, cannot initiate play", gameId);
            return Optional.empty();
        }

        return Optional.of(activeGameService.createActiveGame(game.orElseThrow()));
    }

    public Optional<String> rateGameById(String gameId, String session, Integer rating) {
        if (rating < 1 || rating > 5) {
            log.warn("The rating must be a value between 1 and 5, the current value is {}", rating);
            return Optional.empty();
        }

        Optional<Game> gameOptional = gameRepository.findById(gameId);

        if (gameOptional.isEmpty()) {
            log.warn("There is no game by id {}, cannot rate", gameId);
            return Optional.empty();
        }
        String login = sessionService.getSessionById(session).orElseThrow().getUserLogin();
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

    public Optional<String> createGame(List<ComparableRecord> gameData, boolean isPublic, String title, String desc, String session) {
        Optional<String> userId = sessionService.getSessionById(session)
                .map(Session::getUserLogin)
                .flatMap(userService::getUserByLogin)
                .map(User::getId);

        if (userId.isEmpty()) {
            return Optional.empty();
        }

        Game newGame = new Game(
                isPublic,
                userId.orElseThrow(() -> new IllegalStateException("No username is present despite being found.")),
                title,
                desc,
                gameData
        );

        String result = gameRepository.insert(newGame).getId();
        userService.addGameToUsersCollection(newGame.getUserId(), result);
        return Optional.of(result);
    }
}
