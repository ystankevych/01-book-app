package mate.academy.dto.user;

import jakarta.validation.constraints.NotBlank;
import mate.academy.validation.email.Email;

public record UserLoginRequestDto(
        @Email
        String email,
        @NotBlank(message = "Password must not be null or empty")
        String password
) {}
