package com.puzzlemaker.unit.services;

import com.puzzlemaker.comparison.ComparableField;
import com.puzzlemaker.comparison.ComparableRecord;
import com.puzzlemaker.comparison.fields.ComparableDouble;
import com.puzzlemaker.comparison.fields.ComparableInteger;
import com.puzzlemaker.comparison.fields.ComparableString;
import com.puzzlemaker.model.*;
import com.puzzlemaker.model.dto.GameDTO;
import com.puzzlemaker.model.dto.GameListDTO;
import com.puzzlemaker.repository.ActiveGameRepository;
import com.puzzlemaker.repository.GameRepository;
import com.puzzlemaker.repository.SessionRepository;
import com.puzzlemaker.repository.UserRepository;
import com.puzzlemaker.service.ActiveGameService;
import com.puzzlemaker.service.GameService;
import com.puzzlemaker.service.SessionService;
import com.puzzlemaker.service.UserService;
import org.apache.commons.lang3.tuple.Pair;
import org.assertj.core.api.OptionalAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.Assert;

import javax.swing.text.html.Option;

import static org.mockito.ArgumentMatchers.longThat;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.ArgumentMatchers.any;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GameServiceTests {
    static int LIST_ID = 0;
    ActiveGameRepository activeGameRepository;
    UserRepository userRepository;
    GameRepository gameRepository;
    BCryptPasswordEncoder encoder1;
    UserService userService;
    ActiveGameService activeGameService;
    SessionRepository sessionRepository;
    SessionService sessionService;
    GameService gameService;
    Game exampleGame;
    ActiveGame activeExampleGame;
    User user1 = new User("testUser", "password1", new ArrayList<>(), UserRole.USER, false, true);
    User user3 = new User("testUser2", "password1", new ArrayList<>(), UserRole.USER, false, true);
    User user2 = new User("testAdmin", "password1", new ArrayList<>(), UserRole.ADMIN, false, true);

    ActiveGame activeGame1;
    Game game1;
    Game game2;
    Game game3;
    Session session1;
    Session session2;
    Session session3;
    ComparableField<String> c1 = new ComparableString("test");
    ComparableField<String> c2 = new ComparableString("test2");
    ComparableField<Double> c3 = new ComparableDouble(2.0);
    ComparableField<Double> c4 = new ComparableDouble(1.0);
    ComparableField<Integer> c5 = new ComparableInteger(1);
    ComparableField<Integer> c6 = new ComparableInteger(2);
    ComparableRecord guess1 = new ComparableRecord("guess1", List.of(c1, c3, c5));
    ComparableRecord guess2 = new ComparableRecord("guess2", List.of(c2, c4, c6));

    @BeforeEach
    public void setUp() {
        this.activeGameRepository = Mockito.mock(ActiveGameRepository.class);
        this.userRepository = Mockito.mock(UserRepository.class);
        this.gameRepository = Mockito.mock(GameRepository.class);
        this.sessionRepository = Mockito.mock(SessionRepository.class);
        this.encoder1 = new BCryptPasswordEncoder();
        this.userService = new UserService(this.userRepository, this.gameRepository, encoder1);
        this.activeGameService = new ActiveGameService(this.activeGameRepository);
        this.sessionService = new SessionService(this.sessionRepository);
        this.gameService = new GameService(this.gameRepository, this.userService, this.sessionService, this.activeGameService);

        activeExampleGame = new ActiveGame("Example Game", guess1, List.of(guess1, guess2));
        activeExampleGame.setId("0");
        exampleGame = new Game(true, "0", "Example Game", "Test description", List.of(guess1, guess2));
        exampleGame.setId("0");
        game1 = new Game(true, "0", "Test game", "Test decsription", List.of(guess1, guess2));
        game1.setId("1");
        game2 = new Game(false, "0", "Test game", "Test decsription", List.of(guess1, guess2));
        game3 = new Game(false, "0", "Test game", "Test decsription", List.of(guess1, guess2));
        game2.setId("2");
        game3.setId("3");
        List<String> gameIds = new ArrayList<>();
        gameIds.add("1");
        activeGame1 = new ActiveGame("Test game", guess1, List.of(guess1, guess2));
        activeGame1.setId("0");
        session1 = new Session(user1.getLogin());
        session2 = new Session(user2.getLogin());
        session3 = new Session(user3.getLogin());
        session1.setId("0");
        session2.setId("1");
        session3.setId("2");
        user1.setId("0");
        user2.setId("1");
        user2.setId("2");
    }

    @Test
    public void getExampleGameIdTestPositive() {
        user2.setId("0");
        //mocks
        when(userRepository.findUserByLogin("admin")).thenReturn(Optional.of(user2));
        when(gameRepository.findGamesByUserId("0")).thenReturn(List.of(exampleGame));
        //test
        Optional<String> test1 = gameService.getExampleGameId();

        Assertions.assertTrue(test1.isPresent());
        Assertions.assertEquals("0", test1.get());
    }

    @Test
    public void getExampledGameIdTestNegativeNoAdmin() {
        when(userRepository.findUserByLogin("admin")).thenReturn(Optional.empty());
        try {
            gameService.getExampleGameId();
            throw new AssertionError("The test should fail without admin added");
        } catch (RuntimeException e) {

        }
    }

    @Test
    public void getExampleGameIdTestNegativeNoExampleGame() {
        user2.setId("0");
        when(userRepository.findUserByLogin("admin")).thenReturn(Optional.of(user2));
        when(gameRepository.findGamesByUserId("0")).thenReturn(List.of());
        Optional<String> test = gameService.getExampleGameId();
        Assertions.assertTrue(test.isEmpty());
    }

    @Test
    public void getRandomGameIdPositive() {
        //mocks
        when(gameRepository.findAll()).thenReturn(List.of(exampleGame));
        //test
        Optional<String> test1 = gameService.getRandomGameId();
        Assertions.assertTrue(test1.isPresent());
        Assertions.assertEquals("0", test1.get());
    }

    @Test
    public void getRandomGameIdNegative() {
        //mocks
        when(gameRepository.findAll()).thenReturn(List.of());
        //test
        Optional<String> test1 = gameService.getRandomGameId();
        Assertions.assertTrue(test1.isEmpty());

    }

    @Test
    public void getGameByIdTest() {
        //mocks
        when(gameRepository.findById("0")).thenReturn(Optional.of(exampleGame));
        when(gameRepository.findById("1")).thenReturn(Optional.empty());
        //tests
        Optional<GameDTO> test1 = gameService.getGameById("0");
        Optional<GameDTO> test2 = gameService.getGameById("1");
        Assertions.assertTrue(test1.isPresent());
        Assertions.assertTrue(test2.isEmpty());
        Assertions.assertEquals("Example Game", test1.get().title());
    }

    @Test
    public void removeGameByIdTestPositive() {
        List<Game> games = new ArrayList<>();
        games.add(game1);
        games.add(game2);
        //mocks
        doAnswer(arg -> {
            games.removeIf(game -> game.getId().compareTo(arg.getArgument(0)) == 0);
            return null;
        }).when(gameRepository).deleteById(any(String.class));
        when(userRepository.findUserByGamesIdsContaining("0")).thenReturn(Optional.of(user1));
        Optional<Boolean> test = gameService.removeGameById("0");
        Assertions.assertTrue(test.isPresent());
        Assertions.assertTrue(test.get());
    }

    @Test
    public void removeGameByIdTestNegativeNoUser() {
        when(userRepository.findUserByGamesIdsContaining("1")).thenReturn(Optional.empty());
        Optional<Boolean> test1 = gameService.removeGameById("1");
        Assertions.assertTrue(test1.isPresent());
        Assertions.assertFalse(test1.get());
    }

    @Test
    public void changeGameVisibilityTestPositive() {
        //mocks
        when(gameRepository.findById("1")).thenReturn(Optional.of(game1));
        when(gameRepository.save(game1)).thenReturn(game1);

        //Assertions
        Optional<Boolean> test1 = gameService.changeVisibility("1");
        Assertions.assertTrue(test1.isPresent());
        Assertions.assertEquals(true, test1.get());
        Assertions.assertEquals(false, game1.getIsPublic());
        Optional<Boolean> test2 = gameService.changeVisibility("1");
        Assertions.assertTrue(test2.isPresent());
        Assertions.assertEquals(true, test2.get());
        Assertions.assertEquals(true, game1.getIsPublic());
    }

    @Test
    public void changeGameVisibilityTestNoGameWithGivenId() {
        when(gameRepository.findById(any(String.class))).thenReturn(Optional.empty());
        try {
            gameService.changeVisibility("0");
            throw new AssertionError("This test should fail");
        } catch (RuntimeException e) {
        }
    }

    @Test
    public void getGameObjectById() {
        when(gameRepository.findById("1")).thenReturn(Optional.of(game1));
        when(gameRepository.findById("2")).thenReturn(Optional.empty());
        Optional<Game> test = gameService.getGameObjectById("1");
        Assertions.assertTrue(test.isPresent());
        Assertions.assertEquals("Test game", test.get().getTitle());
        Assertions.assertEquals("Test description", test.get().getDescription());
        test = gameService.getGameObjectById("2");
        Assertions.assertTrue(test.isEmpty());
    }

    @Test
    public void getGameObjectByIdTest() {
        //mocks
        when(gameRepository.findById("1")).thenReturn(Optional.of(game1));
        when(gameRepository.findById("2")).thenReturn(Optional.empty());
        //tests
        Optional<Game> test1 = gameService.getGameObjectById("1");
        Optional<Game> test2 = gameService.getGameObjectById("2");
        Assertions.assertTrue(test1.isPresent());
        Assertions.assertTrue(test2.isEmpty());
        Assertions.assertEquals("Test game", test1.get().getTitle());
    }

    @Test
    public void getAllPublicDTOTestPositives() {
        //mocks
        when(gameRepository.findAll()).thenReturn(List.of(game1, game2));
        //tests
        List<GameDTO> test = gameService.getAllGameDtos();
        Assertions.assertEquals(1, test.size());
        Assertions.assertEquals("Test game", test.get(0).title());
    }

    @Test
    public void getAllPublicDTOTestNoGames() {
        when(gameRepository.findAll()).thenReturn(List.of());
        List<GameDTO> test = gameService.getAllGameDtos();
        Assertions.assertTrue(test.isEmpty());
    }

    @Test
    public void getAllDTOTestPositive() {
        when(gameRepository.findAll()).thenReturn(List.of(game1, game2));
        List<GameDTO> test = gameService.getAllGameDtos();
        Assertions.assertEquals(2, test.size());
        Assertions.assertEquals("1", test.get(0).id());
        Assertions.assertEquals("2", test.get(1).id());
    }

    @Test
    public void getAllDTOTestNoGames() {
        when(gameRepository.findAll()).thenReturn(List.of());
        List<GameDTO> test = gameService.getAllGameDtos();
        Assertions.assertTrue(test.isEmpty());
    }

    @Test
    public void getGameListTestPositive() {
        //setup
        user1.setGamesIds(List.of("0", "1"));
        //mocks
        when(userRepository.findUserByLogin("testUser")).thenReturn(Optional.of(user1));
        when(gameRepository.findById("0")).thenReturn(Optional.of(game1));
        when(gameRepository.findById("1")).thenReturn(Optional.of(game2));
        when(userRepository.findUserByLogin("no user")).thenReturn(Optional.empty());
        Optional<List<GameListDTO>> test = gameService.getGameList("testUser");
        Assertions.assertTrue(test.isPresent());
        Assertions.assertEquals(2, test.get().size());
        Assertions.assertEquals("Test game", test.get().get(0).name());
        Assertions.assertEquals("Test game", test.get().get(1).name());

        try {
            gameService.getGameList("no user");
        } catch (RuntimeException e) {
            Assertions.assertEquals("No such player", e.getMessage());
        }
        //setup no games
        user1.setGamesIds(List.of());
        test = gameService.getGameList("testUser");
        Assertions.assertTrue(test.isPresent());
        Assertions.assertTrue(test.get().isEmpty());
    }

    @Test
    public void playGameByIdTestPositive() {
        List<ActiveGame> activeGames = new ArrayList<>();
        int index = 0;
        //mocks
        when(gameRepository.findById("0")).thenReturn(Optional.of(game1));
        when(gameRepository.findById("1")).thenReturn(Optional.empty());
        doAnswer(
                arg -> {
                    ActiveGame game = (ActiveGame) (arg.getArgument(0));
                    game.setId(Integer.toString(index));
                    activeGames.add(game);
                    return null;
                }
        ).when(activeGameRepository).insert(any(ActiveGame.class));
        Optional<String> test = gameService.playGameById("0");
        Assertions.assertTrue(test.isPresent());
        Assertions.assertEquals(1, activeGames.size());
        Assertions.assertEquals("0", activeGames.get(0).getId());
        test = gameService.playGameById("1");
        Assertions.assertTrue(test.isEmpty());
    }


    @Test
    public void rateGameById() {
        game1.setId("0");
        user1.setScores(List.of());
        user2.setId("1");
        user3.setId("2");
        user2.setScores(List.of(Pair.of("0",4)));
        game1.setRatings(new ArrayList<>(List.of(Pair.of("1",1))));
        user3.setScores(List.of(Pair.of("0",4)));
        Session session4 = new Session("no user");
        //mocks
        when(sessionRepository.findById("0")).thenReturn(Optional.of(session1));
        when(sessionRepository.findById("1")).thenReturn(Optional.of(session2));
        when(sessionRepository.findById("2")).thenReturn(Optional.of(session3));
        when(sessionRepository.findById("3")).thenReturn(Optional.empty());
        when(sessionRepository.findById("4")).thenReturn(Optional.of(session4));
        when(userRepository.findUserByLogin("testUser")).thenReturn(Optional.of(user1));
        when(userRepository.findUserByLogin("testAdmin")).thenReturn(Optional.of(user2));
        when(userRepository.findUserByLogin("testUser2")).thenReturn(Optional.of(user3));
        when(userRepository.findUserByLogin("no user")).thenReturn(Optional.empty());
        when(gameRepository.findById("0")).thenReturn(Optional.of(game1));
        when(gameRepository.findById("1")).thenReturn(Optional.empty());
        doAnswer(
                args-> (Game)args.getArgument(0)
        ).when(gameRepository).save(any(Game.class));
        Optional<String> test = gameService.rateGameById("0", "0", 0);
        Assertions.assertTrue(test.isEmpty());
        //wrong ratings
        test = gameService.rateGameById("0", "0",6);
        Assertions.assertTrue(test.isEmpty());
        test = gameService.rateGameById("0", "0",0);
        Assertions.assertTrue(test.isEmpty());
        // no game with given id
        test = gameService.rateGameById("1", "0",3);
        Assertions.assertTrue(test.isEmpty());
        //no session with given id
        test = gameService.rateGameById("0","3",3);
        Assertions.assertTrue(test.isEmpty());
        //no user from session
        test = gameService.rateGameById("0","4",3);
        Assertions.assertTrue(test.isEmpty());
        //user did not play the game
        test = gameService.rateGameById("0","0", 3);
        Assertions.assertTrue(test.isEmpty());
        //user that already played
        test = gameService.rateGameById("0","1", 3);
        Assertions.assertTrue(test.isPresent());
        Assertions.assertEquals("0",test.get());
        Assertions.assertEquals(1,game1.getRatings().size());
        Assertions.assertEquals("1",game1.getRatings().get(0).getLeft());
        Assertions.assertEquals(3,game1.getRatings().get(0).getRight());
        //user with played game and no score
        test = gameService.rateGameById("0","2",4);
        Assertions.assertTrue(test.isPresent());
        Assertions.assertEquals("0",test.get());
        Assertions.assertEquals(2,game1.getRatings().size());
        Assertions.assertEquals("2",game1.getRatings().get(1).getLeft());
        Assertions.assertEquals(4,game1.getRatings().get(1).getRight());
    }

    @Test
    public void getGameRatingsTest() {
        //data
        game1.setRatings(List.of(Pair.of("0", 2), Pair.of("1", 4), Pair.of("2", 5), Pair.of("3", 1)));
        game2.setRatings(List.of(Pair.of("0", 2), Pair.of("1", 3)));
        game3.setGameData(List.of());
        //mocks
        when(gameRepository.findById("1")).thenReturn(Optional.of(game1));
        when(gameRepository.findById("2")).thenReturn(Optional.of(game2));
        when(gameRepository.findById("3")).thenReturn(Optional.of(game3));
        when(gameRepository.findById("4")).thenReturn(Optional.empty());
        //tests
        Optional<Double> test = gameService.getGameRatings("1");
        Assertions.assertTrue(test.isPresent());
        Assertions.assertEquals(3.0, test.get());
        test = gameService.getGameRatings("2");
        Assertions.assertTrue(test.isPresent());
        Assertions.assertEquals(2.5, test.get());
        test = gameService.getGameRatings("3");
        Assertions.assertTrue(test.isEmpty());
        test = gameService.getGameRatings("4");
        Assertions.assertTrue(test.isEmpty());
    }

    @Test
        public void createGameTest() {
        //data
        List<Game> games = new ArrayList<>();
        GameServiceTests.LIST_ID = 0;
        user1.setGamesIds(new ArrayList<>());
        user2.setGamesIds(new ArrayList<>());

        //mocks
        doAnswer(
                args -> {
                    Game game = (Game) args.getArgument(0);
                    game.setId(Integer.toString(GameServiceTests.LIST_ID));
                    GameServiceTests.LIST_ID++;
                    games.add(game);
                    return game;
                }
        ).when(gameRepository).insert(any(Game.class));
        when(sessionRepository.findById("0")).thenReturn(Optional.of(session1));
        when(sessionRepository.findById("1")).thenReturn(Optional.of(session2));
        when(sessionRepository.findById("2")).thenReturn(Optional.empty());
        when(userRepository.findUserByLogin("testUser")).thenReturn(Optional.of(user1));
        when(userRepository.findUserByLogin("testAdmin")).thenReturn(Optional.of(user2));
        when(userRepository.findById("0")).thenReturn(Optional.of(user1));
        when(userRepository.findById("1")).thenReturn(Optional.of(user2));
        Optional<String> test = gameService.createGame(List.of(guess1, guess2), true, "Title 1", "Description 1", "0");
        Assertions.assertTrue(test.isPresent());
        Assertions.assertFalse(games.isEmpty());
        Assertions.assertEquals(1, games.size());
        Assertions.assertEquals(1, GameServiceTests.LIST_ID);
        Assertions.assertEquals("Title 1", games.get(0).getTitle());
        Assertions.assertEquals("Description 1", games.get(0).getDescription());
        Assertions.assertEquals(1, user1.getGamesIds().size());
        Assertions.assertEquals("0", user1.getGamesIds().get(0));
        Assertions.assertEquals(true, games.get(0).getIsPublic());
        test = gameService.createGame(List.of(guess1, guess2), false, "Title 2", "Description 2", "1");
        Assertions.assertTrue(test.isPresent());
        Assertions.assertFalse(games.isEmpty());
        Assertions.assertEquals(2, games.size());
        Assertions.assertEquals(2, GameServiceTests.LIST_ID);
        Assertions.assertEquals("Title 2", games.get(1).getTitle());
        Assertions.assertEquals("Description 2", games.get(1).getDescription());
        Assertions.assertEquals(1, user2.getGamesIds().size());
        Assertions.assertEquals("1", user2.getGamesIds().get(0));
        Assertions.assertEquals(false, games.get(1).getIsPublic());
        test = gameService.createGame(List.of(guess1, guess2), false, "Title 2", "Description 2", "2");
        Assertions.assertTrue(test.isEmpty());
    }
}
