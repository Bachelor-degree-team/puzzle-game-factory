package com.puzzlemaker.model.factory;

import com.puzzlemaker.comparison.ComparableRecord;
import com.puzzlemaker.model.ActiveGame;
import com.puzzlemaker.model.Game;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@UtilityClass
public class ActiveGameFactory {

    public static ActiveGame fromGame(Game game) {
        List<ComparableRecord> listCopy = new ArrayList<>(game.getGameData());
        listCopy.remove(0);
        ComparableRecord correctGuess = getRandomElement(listCopy);

        return new ActiveGame(
                game.getTitle(),
                correctGuess,
                game.getGameData()
        );
    }

    private static ComparableRecord getRandomElement(List<ComparableRecord> gameData) {
        Random rand = new Random();
        return gameData.get(rand.nextInt(gameData.size()));
    }
}
