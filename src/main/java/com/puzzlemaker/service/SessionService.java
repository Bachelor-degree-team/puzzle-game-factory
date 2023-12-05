package com.puzzlemaker.service;

import com.puzzlemaker.model.Session;
import com.puzzlemaker.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class SessionService {

    @NotNull
    private final SessionRepository sessionRepository;

    public String addSessionAfterLogin(String login) {
        return sessionRepository.insert(new Session(login)).getId();
    }

    public Optional<Session> getSessionById(String id) {
        return sessionRepository.findById(id);
    }

}
