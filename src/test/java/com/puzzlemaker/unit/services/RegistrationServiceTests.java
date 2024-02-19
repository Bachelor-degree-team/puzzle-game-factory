package com.puzzlemaker.unit.services;

import com.puzzlemaker.controller.RegistrationRequest;
import com.puzzlemaker.model.User;
import com.puzzlemaker.model.UserRole;
import com.puzzlemaker.repository.GameRepository;
import com.puzzlemaker.repository.UserRepository;
import com.puzzlemaker.service.RegistrationService;
import com.puzzlemaker.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.Optional;

import static org.mockito.Mockito.when;

public class RegistrationServiceTests {

    UserRepository userRepository;
    GameRepository gameRepository;
    UserService userService;
    BCryptPasswordEncoder encoder1;
    BCryptPasswordEncoder encoder2;
    RegistrationService registrationService;
    RegistrationRequest request1;
    RegistrationRequest request2;
    User user1;
    User user2;
    @BeforeEach
    public void setUp(){
        this.userRepository = Mockito.mock(UserRepository.class);
        this.gameRepository = Mockito.mock(GameRepository.class);
        this.encoder1 = new BCryptPasswordEncoder();
        this.encoder2 = new BCryptPasswordEncoder();
        this.userService = new UserService(userRepository,gameRepository, encoder1);
        this.registrationService = new RegistrationService(this.userService, encoder2);
        this.request1 = new RegistrationRequest("test_exist", "password", "test@test.pl");
        this.request2 = new RegistrationRequest("test_new", "password", "test@test.pl");
        this.user1 = new User("test_exist", "password",new ArrayList<>(), UserRole.USER, false, true);
        this.user2 = new User("test_exist", "password",new ArrayList<>(), UserRole.USER, false, true);
    }
    @Test
    public void registerTest(){
        this.user1.setId("0");
        this.user2.setId("1");
        //mock
        when(userRepository.findUserByLogin("test_exist")).thenReturn(Optional.of(user1));
        when(userRepository.findUserByLogin("test_new")).thenReturn(Optional.empty());
        when(userRepository.save(ArgumentMatchers.any(User.class))).thenReturn(user2);
        //test
        boolean test1= registrationService.register(request1);
        boolean test2= registrationService.register(request2);
        Assertions.assertFalse(test1);
        Assertions.assertTrue(test2);
    }
}
