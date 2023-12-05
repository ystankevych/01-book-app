package mate.academy.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import mate.academy.validation.email.Email;
import mate.academy.validation.password.FieldMatch;

@FieldMatch(
        fields = {"password", "repeatedPassword"},
        message = "Password and repeated password must be equal"
)
public record UserRegistrationRequestDto(
        @Email
        @Size(max = 255, message = "Maximum allowed size 255 characters")
        String email,
        @NotBlank
        @Size(max = 255, message = "Maximum allowed size 255 characters")
        String password,
        @NotBlank
        @Size(max = 255, message = "Maximum allowed size 255 characters")
        String repeatedPassword,
        @NotBlank(message = "First name must not be null or empty")
        @Size(max = 255, message = "Maximum allowed size 255 characters")
        String firstName,
        @NotBlank(message = "Last name must not be null or empty")
        @Size(max = 255, message = "Maximum allowed size 255 characters")
        String lastName,
        @Size(max = 255, message = "Maximum allowed size 255 characters")
        String shippingAddress
) {
}
