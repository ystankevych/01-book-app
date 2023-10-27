package mate.academy.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.user.UserLoginRequestDto;
import mate.academy.dto.user.UserLoginResponseDto;
import mate.academy.dto.user.UserRegistrationRequestDto;
import mate.academy.dto.user.UserResponseDto;
import mate.academy.exception.RegistrationException;
import mate.academy.security.AuthenticationService;
import mate.academy.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Account management",
        description = "Endpoints for JWT base authentication and authorization")
@RequiredArgsConstructor
@RequestMapping("/auth")
@RestController
public class AuthenticationController {
    private final UserService service;
    private final AuthenticationService authService;

    @PostMapping("/registration")
    @Operation(
            summary = "Register new user",
            description = "Allows the user to register new account"
    )
    public UserResponseDto register(@RequestBody @Valid UserRegistrationRequestDto user)
            throws RegistrationException {
        return service.register(user);
    }

    @PostMapping("/login")
    @Operation(
            summary = "Login into account",
            description = "Allows the user to log into the account"
    )
    public UserLoginResponseDto login(@RequestBody @Valid UserLoginRequestDto request) {
        return authService.authenticate(request);
    }
}
