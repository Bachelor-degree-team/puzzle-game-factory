package com.puzzlemaker.controller;

import com.puzzlemaker.model.User;
import com.puzzlemaker.security.SecurityUtils;
import com.puzzlemaker.service.SessionService;
import com.puzzlemaker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserController {

    @NotNull
    private final UserService userService;

    @NotNull
    private final SessionService sessionService;

    @NotNull
    private final AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResponseEntity<List<String>> loginUser(@RequestBody LoginRequest loginRequest) {
        if (userService.getUserByLogin(loginRequest.login()).isEmpty()) {
            return ResponseEntity.of(Optional.of(List.of("false")));
        }

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.login(), loginRequest.password()));
        if (authentication.isAuthenticated()) {
            return ResponseEntity.of(Optional.of(List.of("true", sessionService.addSessionAfterLogin(loginRequest.login()))));
        }
        return ResponseEntity.of(Optional.of(List.of("false")));
    }

    @GetMapping("/logged")
    public ResponseEntity<String> getLoggedInUser(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.of(Optional.ofNullable(userDetails.getUsername()));
    }

    @GetMapping("/{login}/games")
    public ResponseEntity<List<String>> getGameIds(@PathVariable("login") String login, @AuthenticationPrincipal UserDetails userDetails) {
        if (!SecurityUtils.hasAccess(userDetails, login)) {
            return ResponseEntity.notFound().build();
        }

        Optional<List<String>> result = userService.getUserByLogin(login).map(User::getGamesIds);
        return ResponseEntity.of(result);
    }

    @PostMapping("/{login}/block/{locked}")
    public ResponseEntity<String> getGameIds(@PathVariable("login") String login,
                                             @PathVariable("locked") Boolean locked,
                                             @AuthenticationPrincipal UserDetails userDetails) {
        if (!SecurityUtils.isAdmin(userDetails)) {
            return ResponseEntity.notFound().build();
        }

        Optional<String> result = userService.setUserLocked(login, locked);
        return ResponseEntity.of(result);
    }

    @PostMapping("/{login}/scores/{gameId}/add/{score}")
    public ResponseEntity<String> addScore(@PathVariable("login") String login,
                                           @PathVariable("gameId") String gameId,
                                           @PathVariable("score") Integer score,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        if (!SecurityUtils.hasAccess(userDetails, login)) {
            return ResponseEntity.notFound().build();
        }

        boolean result = userService.addScoreToUser(login, gameId, score);
        return result ? ResponseEntity.ok().build() : ResponseEntity.badRequest().body("Wrong score (must be integer and more than 0)");
    }

}
