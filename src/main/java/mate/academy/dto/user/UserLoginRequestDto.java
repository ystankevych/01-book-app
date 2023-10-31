package mate.academy.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import mate.academy.validation.email.Email;

public record UserLoginRequestDto(
        @Email
        @Size(max = 255, message = "Maximum allowed size 255 characters")
        String email,
        @NotBlank(message = "Password must not be null or empty")
        @Size(max = 255, message = "Maximum allowed size 255 characters")
        String password
) {}
