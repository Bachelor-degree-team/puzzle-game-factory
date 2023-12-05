package com.puzzlemaker.controller;

import com.puzzlemaker.service.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("/register")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RegistrationController {

    @NotNull
    private final RegistrationService registrationService;

    @PostMapping
    public ResponseEntity<Boolean> register(@RequestBody RegistrationRequest request) {
        return ResponseEntity.of(Optional.of(registrationService.register(request)));
    }
}
