package com.puzzlemaker.controller;

import org.jetbrains.annotations.NotNull;

public record LoginRequest (
        @NotNull String login,
        @NotNull String password
) {

}
