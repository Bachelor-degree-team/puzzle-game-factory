package com.puzzlemaker.controller;

import com.puzzlemaker.model.User;
import com.puzzlemaker.security.SecurityUtils;
import com.puzzlemaker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserController {

    @NotNull
    private final UserService userService;

    @GetMapping("/{login}/games")
    public ResponseEntity<List<String>> getGameIds(@PathVariable("login") String login, @AuthenticationPrincipal UserDetails userDetails) {
        if (!SecurityUtils.hasAccess(userDetails, login)) {
            return ResponseEntity.notFound().build();
        }

        Optional<List<String>> result = userService.getUserByLogin(login).map(User::getGamesIds);
        return ResponseEntity.of(result);
    }

}
