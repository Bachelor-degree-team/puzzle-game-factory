package com.puzzlemaker.controller;

import com.puzzlemaker.service.GameService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/game")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GameController {

    @NotNull
    private final GameService gameService;

    @GetMapping("/{id}/play")
    public ResponseEntity<List<String>> play(@PathVariable("id") String id) {
        Optional<List<String>> activeGameId = gameService.playGameById(id);
        return ResponseEntity.of(activeGameId);
    }

}
