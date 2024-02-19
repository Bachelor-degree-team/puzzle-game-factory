package com.puzzlemaker.unit.services;

import com.puzzlemaker.model.Game;
import com.puzzlemaker.model.User;
import com.puzzlemaker.model.UserRole;
import com.puzzlemaker.model.dto.GameHistoryDTO;
import com.puzzlemaker.repository.GameRepository;
import com.puzzlemaker.repository.UserRepository;
import com.puzzlemaker.service.UserService;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.server.PathContainer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.event.annotation.BeforeTestClass;

import javax.lang.model.type.ArrayType;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
public class UserServiceTests {

    private UserRepository userRepository;
    private GameRepository gameRepository;
    private BCryptPasswordEncoder passwordEncoder;
    private UserService userService;

    private SecurityContext securityContext;
    private Authentication authentication;


    User user1 = new User("testUser", "password1",  new ArrayList<>(), UserRole.USER,false, true);
    User user2 = new User("testAdmin", "password1",  new ArrayList<>(), UserRole.ADMIN,false, true);
    Game game1 = new Game(true,"0", "Test title", "Test description", new ArrayList<>());
    @BeforeEach
    public void setUp(){
        userRepository = Mockito.mock(UserRepository.class);
        gameRepository = Mockito.mock(GameRepository.class);
        authentication = Mockito.mock(Authentication.class);
        securityContext = Mockito.mock(SecurityContext.class);
        passwordEncoder = new BCryptPasswordEncoder();
        userService = new UserService(userRepository, gameRepository,passwordEncoder);
        SecurityContextHolder.setContext(securityContext);
    }


    @Test
    public void loadUserByUsernameTest(){

        //mock behviors
        when(userRepository.findUserByLogin("testUser")).thenReturn(Optional.of(user1));
        when(userRepository.findUserByLogin("testAdmin")).thenReturn(Optional.of(user2));

        //asserts
        UserDetails user3 = userService.loadUserByUsername("testUser");
        UserDetails user4 = userService.loadUserByUsername("testAdmin");
        Assertions.assertEquals(user1.getUsername(), user3.getUsername());
        Assertions.assertEquals(user2.getUsername(), user4.getUsername());
    }
    @Test
    public void addScoreToUserTest(){
        user1.setScores(new ArrayList<>());
        user2.setScores(new ArrayList<>());
        User user3 = new User("testUser", "password1",  new ArrayList<>(), UserRole.USER,false, true);
        User user4 = new User("testAdmin", "password1",  new ArrayList<>(), UserRole.ADMIN,false, true);
        user3.setScores(List.of(Pair.of("0",1)));
        user4.setScores(List.of(Pair.of("0",1)));
        //mock behaviors
        when(userRepository.findUserByLogin("testUser")).thenReturn(Optional.of(user1));
        when(userRepository.findUserByLogin("testAdmin")).thenReturn(Optional.of(user2));
        when(userRepository.findUserByLogin("test")).thenReturn(Optional.empty());
        when(userRepository.save(user1)).thenReturn(user3);
        when(userRepository.save(user2)).thenReturn(user4);
        //test
        boolean test = userService.addScoreToUser("testUser","0",1);
        boolean  test2 = userService.addScoreToUser("testAdmin","0",1);
        boolean test3 = userService.addScoreToUser("test", "0", 2);
        boolean test4 = userService.addScoreToUser("testUser", "0", -1);
        Assertions.assertTrue(test);
        Assertions.assertTrue(test2);
        Assertions.assertTrue(test3);
        Assertions.assertFalse(test4);
    }
    @Test
    public void getScores(){
        user1.setScores(List.of(Pair.of("0", 1)));
        user2.setScores(List.of(Pair.of("0", 1)));

        //mock
        when(userRepository.findUserByLogin("testUser")).thenReturn(Optional.of(user1));
        when(userRepository.findUserByLogin("testAdmin")).thenReturn(Optional.of(user2));
        when(userRepository.findUserByLogin("test")).thenReturn(Optional.empty());
        when(gameRepository.findById("0")).thenReturn(Optional.of(game1));
        //test
        Optional<List<GameHistoryDTO>> test1 = userService.getScores("testUser");
        Optional<List<GameHistoryDTO>> test2 = userService.getScores("testAdmin");
        Optional<List<GameHistoryDTO>> test3 = userService.getScores("test");

        //checks
        Assertions.assertTrue(test1.isPresent());
        Assertions.assertTrue(test2.isPresent());
        Assertions.assertTrue(test3.isEmpty());
        Assertions.assertEquals(1,test1.get().size());
        Assertions.assertEquals(1, test1.get().size());
        Assertions.assertEquals("0", test1.get().get(0).id());
        Assertions.assertEquals("0", test2.get().get(0).id());
    }
    @Test
    public void getUserByIdTest(){
        //mocks
        when(userRepository.findById("0")).thenReturn(Optional.of(user1));
        when(userRepository.findById("1")).thenReturn(Optional.of(user2));
        when(userRepository.findById("2")).thenReturn(Optional.empty());
        //tests
        Optional<User> test1 = userService.getUserById("0");
        Optional<User> test2 = userService.getUserById("1");
        Optional<User> test3 = userService.getUserById("2");
        //asserts
        Assertions.assertTrue(test1.isPresent());
        Assertions.assertTrue(test2.isPresent());
        Assertions.assertTrue(test3.isEmpty());
        Assertions.assertEquals("testUser", test1.get().getUsername());
        Assertions.assertEquals("testAdmin", test2.get().getUsername());
    }
    @Test
    public void getLoggedInUserLoginTest(){
        //mock
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testUser");
        //test
        Optional<String> test1 = userService.getLoggedInUserLogin();
        Assertions.assertTrue(test1.isPresent());
        Assertions.assertEquals("testUser", test1.get());
    }
    @Test
    public void setUserLockedTest(){

        User user3 = new User("testUser", "password1",  new ArrayList<>(), UserRole.USER,true, true);
        user1.setId("0");
        user3.setId("1");
        //mocks
        when(userRepository.findById("0")).thenReturn(Optional.of(user1));
        when(userRepository.findById("1")).thenReturn(Optional.of(user3));
        when(userRepository.findById("2")).thenReturn(Optional.empty());
        when(userRepository.save(user1)).thenReturn(user1);
        when(userRepository.save(user3)).thenReturn(user3);
        //tests
        Optional<String> test1 = userService.setUserLocked("0",true);
        Optional<String> test2 = userService.setUserLocked("1", false);
        Optional<String> test3= userService.setUserLocked("2", true);
        Assertions.assertTrue(test1.isPresent());
        Assertions.assertTrue(test2.isPresent());
        Assertions.assertTrue(test3.isEmpty());
        Assertions.assertEquals("0", test1.get());
        Assertions.assertEquals("1", test2.get());
        Assertions.assertEquals(true, user1.getLocked());
        Assertions.assertEquals(false, user3.getLocked());
    }
}
