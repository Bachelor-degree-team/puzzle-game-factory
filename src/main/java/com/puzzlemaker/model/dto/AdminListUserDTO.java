package com.puzzlemaker.model.dto;

import com.puzzlemaker.model.User;

public record AdminListUserDTO(
        String id,
        String login,
        int numberOfGames,
        boolean isBlocked
) {

    public static AdminListUserDTO fromUser(User user) {
        return new AdminListUserDTO(
                user.getId(),
                user.getLogin(),
                user.getGamesIds().size(),
                !user.isAccountNonLocked()
        );
    }

}
