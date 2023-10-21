package com.puzzlemaker.controller;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record RegistrationRequest (
        @NotNull String login,
        @NotNull String password,
        @Nullable String email
) {

}
