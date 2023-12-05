package com.puzzlemaker.controller;

import com.puzzlemaker.model.Session;
import com.puzzlemaker.model.dto.GameDTO;
import com.puzzlemaker.service.GameService;
import com.puzzlemaker.service.SessionService;
import com.puzzlemaker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("/session")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class SessionController {

    @NotNull
    private final SessionService sessionService;

    @GetMapping("/get/{id}")
    public ResponseEntity<Session> get(@PathVariable("id") String id) {
        Optional<Session> session = sessionService.getSessionById(id);
        return ResponseEntity.of(session);
    }

}
