package com.puzzlemaker.model.dto;

import com.puzzlemaker.model.Game;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.OptionalDouble;

public record GameListDTO (
        String id,
        String name,
        boolean isPublic,
        double rating
) {

    public static GameListDTO fromGame(Game game) {
        return new GameListDTO(
                game.getId(),
                game.getTitle(),
                game.getIsPublic(),
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
