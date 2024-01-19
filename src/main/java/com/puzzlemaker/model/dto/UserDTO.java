package com.puzzlemaker.model.dto;

import com.puzzlemaker.model.User;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public record UserDTO(
        String id,
        String login,
        List<List<String>> scores
) {

    public static UserDTO fromUser(User user) {
        return new UserDTO(
                user.getId(),
                user.getLogin(),
                transformScores(user.getScores())
        );
    }

    private static List<List<String>> transformScores(List<Pair<String, Integer>> scores) {
        List<List<String>> result = new ArrayList<>();
        for (Pair<String, Integer> pair : scores) {
            result.add(List.of(pair.getKey(), pair.getValue().toString()));
        }
        return result;
    }
}
