package com.puzzlemaker.model.dto;

import com.puzzlemaker.comparison.ComparableField;
import com.puzzlemaker.comparison.ComparableRecord;
import com.puzzlemaker.model.ActiveGame;

import java.util.ArrayList;
import java.util.List;

public record ActiveGameDTO (
        String id,
        String title,
        String correctGuess,
        List<String> columns,
        List<String> guesses
) {
    public static final int FIRST_ROW_INDEX = 0;

    public static ActiveGameDTO fromActiveGame(ActiveGame activeGame) {
        return new ActiveGameDTO(
                activeGame.getId(),
                activeGame.getTitle(),
                activeGame.getCorrectGuess().getName(),
                columnNames(activeGame.getGameData()),
                allGuesses(activeGame.getGameData())
        );
    }

    public static List<String> columnNames(List<ComparableRecord> gameData) {
        return gameData.get(FIRST_ROW_INDEX)
                .getFields()
                .stream()
                .map(ComparableField::stringValue)
                .toList();
    }

    private static List<String> allGuesses(List<ComparableRecord> gameData) {
        List<String> nameColumnValues = new ArrayList<>(gameData.stream().map(ComparableRecord::getName).toList());
        nameColumnValues.remove(nameColumnValues.get(0));
        return nameColumnValues;
    }
}
