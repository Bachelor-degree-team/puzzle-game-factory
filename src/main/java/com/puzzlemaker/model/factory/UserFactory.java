package com.puzzlemaker.model.factory;

import com.puzzlemaker.controller.RegistrationRequest;
import com.puzzlemaker.model.User;
import com.puzzlemaker.model.UserRole;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.Optional;

@UtilityClass
public class UserFactory {

    public static User fromRequest(RegistrationRequest request) {
        User resultUser = new User(
                request.login(),
                request.password(),
                List.of(),
                UserRole.USER,
                false,
                true
        );

        Optional.ofNullable(request.email()).ifPresent(resultUser::setEmail);

        return resultUser;
    }
}
