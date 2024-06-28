package com.puzzlemaker.unit.services;

import com.puzzlemaker.controller.RegistrationRequest;
import com.puzzlemaker.model.Session;
import com.puzzlemaker.model.User;
import com.puzzlemaker.model.UserRole;
import com.puzzlemaker.repository.GameRepository;
import com.puzzlemaker.repository.SessionRepository;
import com.puzzlemaker.repository.UserRepository;
import com.puzzlemaker.service.RegistrationService;
import com.puzzlemaker.service.SessionService;
import com.puzzlemaker.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doAnswer;

public class SessionServiceTests {

    SessionRepository sessionRepository;
    SessionService sessionService;
    static int INDEX_ID = 0;
    Session session1 = new Session("testLogin");

    @BeforeEach
    public void setUp() {
        sessionRepository = Mockito.mock(SessionRepository.class);
        sessionService = new SessionService(this.sessionRepository);
    }

    @Test
    public void addSessionAfterLoginTest() {
        List<Session> sessions = new ArrayList<>();
        //mock
        doAnswer(args -> {
            Session session = (Session) args.getArgument(0);
            session.setId(Integer.toString(SessionServiceTests.INDEX_ID));
            SessionServiceTests.INDEX_ID++;
            sessions.add(session);
            return session;
        }).when(sessionRepository).insert(any(Session.class));
        String test = sessionService.addSessionAfterLogin("login");
        Assertions.assertFalse(sessions.isEmpty());
        Assertions.assertEquals(1, sessions.size());
        Assertions.assertEquals("0", sessions.get(0).getId());
        Assertions.assertEquals("0", test);
        Assertions.assertEquals("login", sessions.get(0).getUserLogin());
    }

    @Test
    public void getSessionByIdTest() {
        //mock
        when(sessionRepository.findById("0")).thenReturn(Optional.of(session1));
        when(sessionRepository.findById("1")).thenReturn(Optional.empty());
        Optional<Session> test = sessionService.getSessionById("0");
        Assertions.assertTrue(test.isPresent());
        Assertions.assertEquals("testLogin", test.get().getUserLogin());
        test = sessionService.getSessionById("1");
        Assertions.assertTrue(test.isEmpty());
    }

    @Test
    public void removeSessionByIdTest() {
        //data
        session1.setId("0");
        List<Session> sessions = new ArrayList<>(List.of(session1));
        //mock
        doAnswer(args -> sessions.removeIf(session -> session.getId().compareTo(args.getArgument(0)) == 0)).when(sessionRepository).deleteById(any(String.class));
        sessionService.removeSessionById("1");
        Assertions.assertFalse(sessions.isEmpty());
        Assertions.assertEquals(1,sessions.size());
        sessionService.removeSessionById("0");
        Assertions.assertTrue(sessions.isEmpty());
        sessionService.removeSessionById("0");
        Assertions.assertTrue(sessions.isEmpty());
    }
}
