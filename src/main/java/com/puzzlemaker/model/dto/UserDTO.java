package com.puzzlemaker.model.dto;

import java.util.Map;

public record UserDTO (
        String login,
        Map<String, Integer> scores
) {
}
