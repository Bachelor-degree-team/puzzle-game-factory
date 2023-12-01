package com.puzzlemaker.controller;

import com.puzzlemaker.comparison.ComparableRecord;
import com.puzzlemaker.model.dto.GameDTO;
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

@CrossOrigin
@RestController
@RequestMapping("/game")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GameController {

    @NotNull
    private final GameService gameService;

    @GetMapping("/get/{id}")
    public ResponseEntity<GameDTO> get(@PathVariable("id") String id) {
        Optional<GameDTO> activeGameId = gameService.getGameById(id);
        return ResponseEntity.of(activeGameId);
    }

    @GetMapping("/public/getAll")
    public ResponseEntity<List<GameDTO>> getAll() {
        Optional<List<GameDTO>> activeGameId = Optional.ofNullable(gameService.getAllPublicGameDtos());
        return ResponseEntity.of(activeGameId);
    }

    @GetMapping("/{id}/play")
    public ResponseEntity<String> play(@PathVariable("id") String id) {
        Optional<String> activeGameId = gameService.playGameById(id);
        return ResponseEntity.of(activeGameId);
    }

    @GetMapping("/{id}/ratings/get")
    public ResponseEntity<Double> getRating(@PathVariable("id") String gameId) {
        Optional<Double> averageRating = gameService.getGameRatings(gameId);
        return ResponseEntity.of(averageRating);
    }

    @PostMapping("/{id}/ratings/{login}/rate/{rating}")
    public ResponseEntity<String> rate(@PathVariable("id") String gameId,
                                       @PathVariable("login") String login,
                                       @PathVariable("rating") Integer rating) {
        Optional<String> upsertedGameId = gameService.rateGameById(gameId, login, rating);
        return ResponseEntity.of(upsertedGameId);
    }

    @PostMapping("/create")
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
