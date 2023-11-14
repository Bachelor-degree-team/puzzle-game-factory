package com.puzzlemaker.controller;

import com.puzzlemaker.comparison.ComparableRecord;
import com.puzzlemaker.parsing.CsvFileParser;
import com.puzzlemaker.service.GameService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @GetMapping("/create")
    public ResponseEntity<String> create(@RequestParam MultipartFile csv,
                                         @RequestParam char separator,
                                         @RequestParam boolean isPublic,
                                         @RequestParam String title,
                                         @RequestParam String desc) {
        List<ComparableRecord> gameData = CsvFileParser.readCsvFromRequest(csv, separator);
        Optional<String> createdGameId = gameService.createGame(gameData, isPublic, title, desc);
        return ResponseEntity.of(createdGameId);
    }

}
