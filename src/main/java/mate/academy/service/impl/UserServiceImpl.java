package mate.academy.service.impl;

import jakarta.annotation.PostConstruct;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.user.UserRegistrationRequestDto;
import mate.academy.dto.user.UserResponseDto;
import mate.academy.exception.RegistrationException;
import mate.academy.mapper.UserMapper;
import mate.academy.model.Role;
import mate.academy.model.User;
import mate.academy.repository.RoleRepository;
import mate.academy.repository.UserRepository;
import mate.academy.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final PasswordEncoder encoder;
    private final UserMapper mapper;
    private Role userRole;

    @PostConstruct
    public void init() {
        userRole = roleRepo.findByName(Role.RoleName.ROLE_USER);
    }

    @Override
    public UserResponseDto register(UserRegistrationRequestDto request)
            throws RegistrationException {
        if (userRepo.findByEmail(request.email()).isPresent()) {
            throw new RegistrationException(errorMessage(request.email()));
        }
        User user = mapper.toUser(request);
        user.setRoles(Set.of(userRole));
        user.setPassword(encoder.encode(request.password()));
        return mapper.toDto(userRepo.save(user));
    }

    private String errorMessage(String email) {
        return String.format("user with this email: %s already exists", email);
    }
}
