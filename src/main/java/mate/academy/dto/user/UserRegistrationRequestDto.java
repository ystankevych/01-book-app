package mate.academy.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import mate.academy.validation.email.Email;
import mate.academy.validation.password.FieldMatch;

@FieldMatch(
        fields = {"password", "repeatPassword"},
        message = "Password and repeated password must be equal"
)
public record UserRegistrationRequestDto(
        @Email
        String email,
        @NotBlank
        String password,
        @NotBlank
        String repeatedPassword,
        @NotBlank(message = "First name must not be null or empty")
        @Size(max = 32,
                message = "First name must contain maximum 32 characters")
        String firstName,
        @NotBlank(message = "Last name must not be null or empty")
        @Size(max = 32,
                message = "Last name must contain maximum 32 characters")
        String lastName,
        String shippingAddress
) {
}
