package com.puzzlemaker.unit.services;

import com.puzzlemaker.comparison.ComparableField;
import com.puzzlemaker.comparison.ComparableRecord;
import com.puzzlemaker.comparison.fields.ComparableDouble;
import com.puzzlemaker.comparison.fields.ComparableInteger;
import com.puzzlemaker.comparison.fields.ComparableString;
import com.puzzlemaker.model.ActiveGame;
import com.puzzlemaker.model.Game;
import com.puzzlemaker.model.dto.ActiveGameDTO;
import com.puzzlemaker.repository.ActiveGameRepository;
import com.puzzlemaker.service.ActiveGameService;
import io.restassured.internal.common.assertion.Assertion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.junit.jupiter.api.Assertions;
import org.mockito.stubbing.Stubber;
import org.springframework.util.Assert;

import javax.swing.text.html.Option;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doAnswer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ActiveGameServiceTests {
    private ActiveGameRepository activeGameRepository;

    private ActiveGameService activeGameService;
    private List<ActiveGame> activeGames = new ArrayList<>();
    private int activeGameIndex= 0;
    Game game1;
    ActiveGame activeGame1;

    @BeforeEach
    public void setUp() {
        this.activeGameRepository = Mockito.mock(ActiveGameRepository.class);
        this.activeGameService = new ActiveGameService(activeGameRepository);
        ComparableField<String> c1 = new ComparableString("test");
        ComparableField<String> c2 = new ComparableString("test2");
        ComparableField<Double> c3 = new ComparableDouble(2.0);
        ComparableField<Double> c4 = new ComparableDouble(1.0);
        ComparableField<Integer> c5 = new ComparableInteger(1);
        ComparableField<Integer> c6 = new ComparableInteger(2);
        ComparableRecord guess1 = new ComparableRecord("guess1", List.of(c1, c3, c5));
        ComparableRecord guess2 = new ComparableRecord("guess2", List.of(c2, c4, c6));
        activeGame1 = new ActiveGame("Test game", guess1, List.of(guess1, guess2));
        activeGame1.setId("0");
        game1 = new Game(true, "0", "Test game", "Test decsription", List.of(guess1, guess2));
        game1.setId("0");
    }

    @Test
    public void getByIdTestPositive() {
        //mocks
        when(activeGameRepository.findById("0")).thenReturn(Optional.of(activeGame1));
        //test
        Optional<ActiveGameDTO> test1 = activeGameService.getById("0");
        Assertions.assertTrue(test1.isPresent());
        Assertions.assertEquals("Test game", test1.get().title());
        Assertions.assertEquals("0", test1.get().id());
    }

    @Test
    public void getByIdTestNegative() {
        //mocks
        when(activeGameRepository.findById("0")).thenReturn(Optional.empty());
        //test
        Optional<ActiveGameDTO> test1 = activeGameService.getById("0");
        Assertions.assertTrue(test1.isEmpty());
    }

    @Test
    public void createActiveGamePositive() {
        //mocks
        doAnswer(arg -> {
            ActiveGame gameCast = (ActiveGame) (arg.getArgument(0));
            if(gameCast.getId() == null){
                gameCast.setId(Integer.toString(activeGameIndex));
                activeGameIndex++;
            }
            for (ActiveGame game : activeGames) {
                if (game.getId().compareTo(gameCast.getId()) == 0) {
                    return null;
                }
            }
            this.activeGames.add(gameCast);
            return null;

        }).when(activeGameRepository).insert(any(ActiveGame.class));
        String id1 = activeGameService.createActiveGame(game1);
        Assertions.assertFalse(activeGames.isEmpty());
        Assertions.assertEquals(1,activeGames.size());
        Assertions.assertEquals("0", id1);
        String id2 = activeGameService.createActiveGame(game1);
        Assertions.assertEquals(2,activeGames.size());
        Assertions.assertEquals("1", id2);
    }

    @Test
    public void deleteActiveGamePositive() {
        //prep
        activeGames.add(activeGame1);
        //mocks
        //when
        doAnswer(arg -> {
            this.activeGames.removeIf(game -> game.getId().compareTo(arg.getArgument(0)) == 0);
            return null;
        }).when(activeGameRepository).deleteById(any());
        //assertions
        activeGameService.finishActiveGame("0");
        Assertions.assertTrue(this.activeGames.isEmpty());
    }

    @Test
    public void deleteActiveGameNegative() {
        //prep
        activeGames.add(activeGame1);
        //mocks
        //when
        doAnswer(arg -> {
            this.activeGames.removeIf(game -> game.getId().compareTo(arg.getArgument(0)) == 0);
            return null;
        }).when(activeGameRepository).deleteById(any());
        //assertions
        activeGameService.finishActiveGame("1");
        Assertions.assertFalse(this.activeGames.isEmpty());
        Assertions.assertEquals(1, this.activeGames.size());
        this.activeGames.clear();
    }

    public void guessTest() {
        //mocks
        when(activeGameRepository.findById("0")).thenReturn(Optional.of(activeGame1));
        when(activeGameRepository.findById("1")).thenReturn(Optional.empty());
        //tests
        Optional<Map<String, List<String>>> test1 = activeGameService.guess("0", "guess1");
        Optional<Map<String, List<String>>> test2 = activeGameService.guess("0", "guess2");
        Optional<Map<String, List<String>>> test3 = activeGameService.guess("1", "guess2");
        Assertions.assertTrue(test1.isPresent());
        Assertions.assertTrue(test2.isPresent());
        Assertions.assertTrue(test3.isEmpty());
        Assertions.assertEquals("true", test1.get().get("game_won").get(0));
        Assertions.assertEquals("false", test2.get().get("game_won").get(0));
        Assertions.assertEquals("MATCH", test1.get().get("test").get(1));
        Assertions.assertEquals("MATCH", test1.get().get("2.0").get(1));
        Assertions.assertEquals("MATCH", test1.get().get("1").get(1));
        Assertions.assertEquals("NO_MATCH", test2.get().get("test").get(1));
        Assertions.assertEquals("HIGHER", test2.get().get("2.0").get(1));
        Assertions.assertEquals("LOWER", test2.get().get("1").get(1));
    }
}
