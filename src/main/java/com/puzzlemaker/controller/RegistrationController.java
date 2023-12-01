package com.puzzlemaker.controller;

import com.puzzlemaker.service.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/register")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RegistrationController {

    @NotNull
    private final RegistrationService registrationService;

    @PostMapping
    public String register(@RequestBody RegistrationRequest request) {
        return registrationService.register(request);
    }
}
