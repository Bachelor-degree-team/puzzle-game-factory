package com.puzzlemaker.model.dto;

import com.puzzlemaker.model.Game;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.OptionalDouble;

public record GameDTO (
        String id,
        String title,
        String description,
        double rating
) {
    public static GameDTO fromGame(Game game) {
        return new GameDTO(
                game.getId(),
                game.getTitle(),
                game.getDescription(),
                averageRating(game.getRatings())
        );
    }

    private static double averageRating(List<Pair<String, Integer>> ratings) {
        OptionalDouble average = ratings.stream().map(Pair::getValue).mapToInt(Integer::intValue).average();
        if (average.isEmpty()) {
            return 0.0;
        }
        return average.getAsDouble();
    }
}
