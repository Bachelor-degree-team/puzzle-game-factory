package com.puzzlemaker.controller;

import com.puzzlemaker.comparison.ComparableRecord;
import com.puzzlemaker.model.dto.GameDTO;
import com.puzzlemaker.model.dto.GameListDTO;
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

    @GetMapping("/remove/{id}")
    public ResponseEntity<Boolean> remove(@PathVariable("id") String id) {
        Optional<Boolean> gameRemoved = gameService.removeGameById(id);
        return ResponseEntity.of(gameRemoved);
    }

    @GetMapping("/visibility/{id}")
    public ResponseEntity<Boolean> changeVisibility(@PathVariable("id") String id) {
        Optional<Boolean> gameRemoved = gameService.changeVisibility(id);
        return ResponseEntity.of(gameRemoved);
    }

    @GetMapping("/get/example")
    public ResponseEntity<String> getExample() {
        Optional<String> exampleGameId = gameService.getExampleGameId();
        return ResponseEntity.of(exampleGameId);
    }

    @GetMapping("/get/random")
    public ResponseEntity<String> getRandom() {
        Optional<String> randomGameId = gameService.getRandomGameId();
        return ResponseEntity.of(randomGameId);
    }

    @GetMapping("/public/getAll")
    public ResponseEntity<List<GameDTO>> getAll() {
        Optional<List<GameDTO>> activeGameId = Optional.ofNullable(gameService.getAllPublicGameDtos());
        return ResponseEntity.of(activeGameId);
    }

    @GetMapping("/admin/getAll")
    public ResponseEntity<List<GameDTO>> getAllAdmin() {
        Optional<List<GameDTO>> activeGameId = Optional.ofNullable(gameService.getAllGameDtos());
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

    @GetMapping("/{id}/ratings/{session}/rate/{rating}")
    public ResponseEntity<String> rate(@PathVariable("id") String gameId,
                                       @PathVariable("session") String session,
                                       @PathVariable("rating") Integer rating) {
        Optional<String> upsertedGameId = gameService.rateGameById(gameId, session, rating);
        return ResponseEntity.of(upsertedGameId);
    }

    @GetMapping("/{login}/list")
    public ResponseEntity<List<GameListDTO>> getGameList(@PathVariable("login") String login) {
        return ResponseEntity.of(gameService.getGameList(login));
    }

    @PostMapping("/create")
    public ResponseEntity<String> create(@RequestParam MultipartFile csv,
                                         @RequestParam char separator,
                                         @RequestParam boolean isPublic,
                                         @RequestParam String title,
                                         @RequestParam String desc,
                                         @RequestParam String session) {
        List<ComparableRecord> gameData = CsvFileParser.readCsvToGameData(csv, separator);
        Optional<String> createdGameId = gameService.createGame(gameData, isPublic, title, desc, session);
        return ResponseEntity.of(createdGameId);
    }

}
