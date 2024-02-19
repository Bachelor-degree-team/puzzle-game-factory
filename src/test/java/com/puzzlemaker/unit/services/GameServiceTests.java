package com.puzzlemaker.unit.services;

import com.puzzlemaker.comparison.ComparableField;
import com.puzzlemaker.comparison.ComparableRecord;
import com.puzzlemaker.comparison.fields.ComparableDouble;
import com.puzzlemaker.comparison.fields.ComparableInteger;
import com.puzzlemaker.comparison.fields.ComparableString;
import com.puzzlemaker.model.*;
import com.puzzlemaker.model.dto.GameDTO;
import com.puzzlemaker.repository.ActiveGameRepository;
import com.puzzlemaker.repository.GameRepository;
import com.puzzlemaker.repository.SessionRepository;
import com.puzzlemaker.repository.UserRepository;
import com.puzzlemaker.service.ActiveGameService;
import com.puzzlemaker.service.GameService;
import com.puzzlemaker.service.SessionService;
import com.puzzlemaker.service.UserService;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.Assert;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GameServiceTests {
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
    User user1 = new User("testUser", "password1",  new ArrayList<>(), UserRole.USER,false, true);
    User user2 = new User("testAdmin", "password1",  new ArrayList<>(), UserRole.ADMIN,false, true);

    ActiveGame activeGame1;
    Game game1;
    Game game2;
    Session session1;
    Session session2;
    @BeforeEach
    public void setUp(){
        this.activeGameRepository = Mockito.mock(ActiveGameRepository.class);
        this.userRepository = Mockito.mock(UserRepository.class);
        this.gameRepository = Mockito.mock(GameRepository.class);
        this.sessionRepository = Mockito.mock(SessionRepository.class);
        this.encoder1 = new BCryptPasswordEncoder();
        this.userService = new UserService(this.userRepository, this.gameRepository,encoder1);
        this.activeGameService = new ActiveGameService(this.activeGameRepository);
        this.sessionService = new SessionService(this.sessionRepository);
        this.gameService = new GameService(this.gameRepository, this.userService, this.sessionService, this.activeGameService);
        ComparableField<String> c1 = new ComparableString("test");
        ComparableField<String> c2 = new ComparableString("test2");
        ComparableField<Double> c3 = new ComparableDouble(2.0);
        ComparableField<Double> c4 = new ComparableDouble(1.0);
        ComparableField<Integer> c5 = new ComparableInteger(1);
        ComparableField<Integer> c6 = new ComparableInteger(2);
        ComparableRecord guess1 = new ComparableRecord("guess1", List.of(c1,c3,c5));
        ComparableRecord guess2 = new ComparableRecord("guess2", List.of(c2,c4,c6));
        activeExampleGame = new ActiveGame("Example Game", guess1,List.of(guess1,guess2));
        activeExampleGame.setId("0");
        exampleGame = new Game(true, "0", "Example Game", "Test decsription", List.of(guess1, guess2));
        exampleGame.setId("0");
        game1 = new Game(true, "0", "Test game", "Test decsription", List.of(guess1, guess2));
        game1.setId("1");
        game2 = new Game(true, "0", "Test game", "Test decsription", List.of(guess1, guess2));
        game2.setId("2");
        List<String> gameIds = new ArrayList<>();
        gameIds.add("1");
        user1.setGamesIds(gameIds);
        user1.setScores(List.of(Pair.of("1",3)));
        game1.setRatings(new ArrayList<>());
        game2.setRatings(List.of(Pair.of("1", 4), Pair.of("2",6)));
        activeGame1 = new ActiveGame("Test game", guess1,List.of(guess1,guess2));
        activeGame1.setId("0");
        session1 = new Session(user1.getLogin());
        session2 = new Session(user2.getLogin());
        session1.setId("0");
        session2.setId("1");
        user1.setId("0");
        user2.setId("1");
    }

    @Test
    public void getExampleGameIdTest(){
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
    public void getRandomGameIdPositive(){
        //mocks
        when(gameRepository.findAll()).thenReturn(List.of(exampleGame));
        //test
        Optional<String> test1 = gameService.getRandomGameId();
        Assertions.assertTrue(test1.isPresent());
        Assertions.assertEquals("0", test1.get());
    }
    @Test
    public void getRandomGameIdNegative(){
        //mocks
        when(gameRepository.findAll()).thenReturn(List.of());
        //test
        Optional<String> test1 = gameService.getRandomGameId();
        Assertions.assertTrue(test1.isEmpty());

    }
    @Test
    public void getGameByIdTest(){
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
    public void removeGameByIdTest(){
        when(userRepository.findUserByGamesIdsContaining("1")).thenReturn(Optional.of(user1));
        when(userRepository.save(user1)).thenReturn(user1);
        //tests
        Optional<Boolean> test1 = gameService.removeGameById("1");
        Assertions.assertTrue(test1.isPresent());
        Assertions.assertEquals(true, test1.get());
        Assertions.assertEquals(0, user1.getGamesIds().size());
    }

    @Test
    public void changeGameVisibilityTest(){
        //mocks
        when(gameRepository.findById("1")).thenReturn(Optional.of(game1));
        when(gameRepository.save(game1)).thenReturn(game1);

        //Assertions
        Optional<Boolean> test1 = gameService.changeVisibility("1");
        Assertions.assertTrue(test1.isPresent());
        Assertions.assertEquals(true, test1.get());
        Assertions.assertEquals(false, game1.getIsPublic());
        Optional<Boolean> test2= gameService.changeVisibility("1");
        Assertions.assertTrue(test2.isPresent());
        Assertions.assertEquals(true, test2.get());
        Assertions.assertEquals(true, game1.getIsPublic());
    }
    @Test
    public void getGameObjectByIdTest(){
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
    public void getAllPublicDTOTest(){
        //mocks
        when(gameRepository.findAll()).thenReturn(List.of(game1));
        //tests
        List<GameDTO> test1= gameService.getAllGameDtos();
        Assertions.assertEquals(1,test1.size());
        Assertions.assertEquals("Test game", test1.get(0).title());
    }
    @Test
    public void rateGameById(){
        //mocks
        when(sessionRepository.findById("0")).thenReturn(Optional.of(session1));
        when(sessionRepository.findById("1")).thenReturn(Optional.of(session2));
        when(userRepository.findUserByLogin("testUser")).thenReturn(Optional.of(user1));
        when(gameRepository.findById("1")).thenReturn(Optional.of(game1));
        when(userRepository.findUserByLogin("testAdmin")).thenReturn(Optional.empty());
        when(gameRepository.save(game1)).thenReturn(game1);
        //tests
        Optional<String> test1 = gameService.rateGameById("1", "0", -1);
        Optional<String> test2 = gameService.rateGameById("1", "0", 7);
        Optional<String> test3 = gameService.rateGameById("1", "1", 3);
        Optional<String> test4 = gameService.rateGameById("1","0",3);
        Assertions.assertTrue(test1.isEmpty());
        Assertions.assertTrue(test2.isEmpty());
        Assertions.assertTrue(test3.isEmpty());
        Assertions.assertTrue(test4.isPresent());
        Assertions.assertEquals("1", test4.get());
    }
    @Test
    public void getGameRatingsTest(){

        when(gameRepository.findById("1")).thenReturn(Optional.of(game1));
        when(gameRepository.findById("2")).thenReturn(Optional.of(game2));
        when(gameRepository.findById("3")).thenReturn(Optional.empty());
        //tests
        Optional<Double> test1 = gameService.getGameRatings("1");
        Optional<Double> test2 = gameService.getGameRatings("2");
        Optional<Double> test3 = gameService.getGameRatings("3");
        Assertions.assertTrue(test1.isEmpty());
        Assertions.assertTrue(test2.isPresent());
        Assertions.assertTrue(test3.isEmpty());
        Assertions.assertEquals(5.0, test2.get());
    }
}
