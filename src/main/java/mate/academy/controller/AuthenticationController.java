package mate.academy.controller;

import jakarta.validation.Valid;
import mate.academy.dto.user.UserRegistrationDto;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/auth")
@RestController
public class AuthenticationController {
    @PostMapping("/registration")
    public void register(@RequestBody @Valid UserRegistrationDto user) {

    }
}
