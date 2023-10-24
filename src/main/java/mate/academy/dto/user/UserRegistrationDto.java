package mate.academy.dto.user;

import jakarta.validation.constraints.NotBlank;

public record UserRegistrationDto(
        @NotBlank
        String email,
        @NotBlank
        String password,
        @NotBlank
        String repeatedPassword
) {}
