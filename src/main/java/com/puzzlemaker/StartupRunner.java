package com.puzzlemaker;


import com.puzzlemaker.service.GameService;
import com.puzzlemaker.service.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class StartupRunner implements CommandLineRunner {

    @NonNull
    private final UserService userService;

    @NonNull
    private final GameService gameService;

    @Override
    public void run(String...args) {
        log.info("Executing startup logic.");
        userService.populate();
        gameService.populate();
    }
}
