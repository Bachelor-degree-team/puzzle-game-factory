package com.puzzlemaker.service;

import com.puzzlemaker.controller.RegistrationRequest;
import com.puzzlemaker.model.User;
import com.puzzlemaker.model.factory.UserFactory;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RegistrationService {

    @NotNull
    private final UserService userService;

    @NotNull
    private final BCryptPasswordEncoder passwordEncoder;

    public Boolean register(@NonNull RegistrationRequest request) {
        if (userService.getUserByLogin(request.login()).isPresent()) {
            return false;
        }

        User userToBeAdded = UserFactory.fromRequest(request);
        userToBeAdded.setPassword(passwordEncoder.encode(userToBeAdded.getPassword()));
        return userService.addUser(userToBeAdded).getId() != null;
    }

}
