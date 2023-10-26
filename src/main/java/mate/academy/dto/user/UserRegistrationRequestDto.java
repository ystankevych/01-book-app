package mate.academy.dto.user;

import jakarta.validation.constraints.NotBlank;
import mate.academy.validation.email.Email;
import mate.academy.validation.password.FieldMatch;

@FieldMatch(
        field = "password",
        fieldMatch = "repeatedPassword",
        message = "Password and repeated password must be equal"
)
public record UserRegistrationRequestDto(
        @Email
        String email,
        String password,
        String repeatedPassword,
        @NotBlank(message = "First name must not be null")
        String firstName,
        @NotBlank(message = "Last name must not be null")
        String lastName,
        String shippingAddress
) {}
