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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.junit.jupiter.api.Assertions;
import org.springframework.util.Assert;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ActiveGameServiceTests {
    private ActiveGameRepository activeGameRepository;

    private ActiveGameService activeGameService;


    Game game1;
    ActiveGame activeGame1;
    @BeforeEach
    public void setUp(){
        this.activeGameRepository = Mockito.mock(ActiveGameRepository.class);
        this.activeGameService = new ActiveGameService(activeGameRepository);
        ComparableField<String> c1 = new ComparableString("test");
        ComparableField<String> c2 = new ComparableString("test2");
        ComparableField<Double> c3 = new ComparableDouble(2.0);
        ComparableField<Double> c4 = new ComparableDouble(1.0);
        ComparableField<Integer> c5 = new ComparableInteger(1);
        ComparableField<Integer> c6 = new ComparableInteger(2);
        ComparableRecord guess1 = new ComparableRecord("guess1", List.of(c1,c3,c5));
        ComparableRecord guess2 = new ComparableRecord("guess2", List.of(c2,c4,c6));
        activeGame1 = new ActiveGame("Test game", guess1,List.of(guess1,guess2));
        activeGame1.setId("0");
        game1 = new Game(true, "0", "Test game", "Test decsription", List.of(guess1, guess2));
        game1.setId("0");
    }
    @Test
    public void getByIdTest(){
        //mocks
        when(activeGameRepository.findById("0")).thenReturn(Optional.of(activeGame1));
        when(activeGameRepository.findById("1")).thenReturn(Optional.empty());
        //test
        Optional<ActiveGameDTO> test1 = activeGameService.getById("0");
        Optional<ActiveGameDTO> test2 = activeGameService.getById("1");
        Assertions.assertTrue(test1.isPresent());
        Assertions.assertTrue(test2.isEmpty());
        Assertions.assertEquals("Test game", test1.get().title());
        Assertions.assertEquals("0", test1.get().id());
    }
    @Test
    public void guessTest(){
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
