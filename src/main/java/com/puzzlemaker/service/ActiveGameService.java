package com.puzzlemaker.service;

import com.puzzlemaker.comparison.ComparableRecord;
import com.puzzlemaker.model.ActiveGame;
import com.puzzlemaker.model.Game;
import com.puzzlemaker.model.factory.ActiveGameFactory;
import com.puzzlemaker.repository.ActiveGameRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ActiveGameService {

    private static final String NO_GUESS_FOUND = "invalid_guess_name";

    @NotNull
    private final ActiveGameRepository activeGameRepository;

    public String createActiveGame(Game game) {
        ActiveGame gameToBePlayed = ActiveGameFactory.fromGame(game);
        activeGameRepository.insert(gameToBePlayed);
        return gameToBePlayed.getId();
    }

    public void finishActiveGame(String gameId) {
        activeGameRepository.deleteById(gameId);
    }

    public Optional<Map<String, String>> guess(String activeGameId, String guessName) {
        Optional<ActiveGame> supposedGame = activeGameRepository.findById(activeGameId);

        if (supposedGame.isEmpty()) {
            log.warn("No currently played games under the id of {}", activeGameId);
            return Optional.empty();
        }

        ActiveGame currentGame = supposedGame.orElseThrow(() -> new IllegalStateException("No game present despite being found"));

        Optional<ComparableRecord> supposedGuess = currentGame.getGameData()
                .stream()
                .filter(record -> guessName.equals(record.getName()))
                .findFirst();

        if (supposedGuess.isEmpty()) {
            log.info("No guesses under the name of {} exist, try again.", guessName);
            return Optional.of(Map.of(NO_GUESS_FOUND, "true"));
        }

        ComparableRecord currentGuess = supposedGuess.orElseThrow(() -> new IllegalStateException("No guess present despite being found"));

        Pair<Map<String, String>, Boolean> result = currentGuess.compareTo(currentGame.getCorrectGuess());

        if (result.getSecond()) {
            log.info("Game with id {} has been won! Ending the game.", activeGameId);
            finishActiveGame(activeGameId);
        }

        return Optional.of(result.getFirst());
    }

}
