package com.example.springmvc.service;

import com.example.springmvc.domain.Role;
import com.example.springmvc.domain.RoleName;
import com.example.springmvc.domain.User;
import com.example.springmvc.repository.RoleRepository;
import com.example.springmvc.repository.UserRepository;
import com.example.springmvc.security.JwtService;
import com.example.springmvc.security.UserPrincipal;
import com.example.springmvc.web.dto.AuthResponse;
import com.example.springmvc.web.dto.CreateUserRequest;
import com.example.springmvc.web.dto.LoginRequest;
import com.example.springmvc.web.dto.RegisterRequest;
import com.example.springmvc.web.dto.UpdateUserRequest;
import com.example.springmvc.web.dto.UserResponse;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public UserService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        User user = createUserEntity(
                request.email(),
                request.password(),
                request.firstName(),
                request.lastName(),
                Set.of(RoleName.USER),
                true);
        UserPrincipal principal = new UserPrincipal(user);
        return new AuthResponse(jwtService.generateToken(principal), UserResponse.from(user));
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        return new AuthResponse(jwtService.generateToken(principal), UserResponse.from(principal.getUser()));
    }

    @Transactional(readOnly = true)
    public UserResponse me(UserPrincipal principal) {
        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return UserResponse.from(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream().map(UserResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public UserResponse findById(Long id) {
        return UserResponse.from(getUser(id));
    }

    @Transactional
    public UserResponse create(CreateUserRequest request) {
        User user = createUserEntity(
                request.email(),
                request.password(),
                request.firstName(),
                request.lastName(),
                request.roles(),
                request.enabled() == null || request.enabled());
        return UserResponse.from(user);
    }

    @Transactional
    public UserResponse update(Long id, UpdateUserRequest request) {
        User user = getUser(id);
        String email = request.email().trim().toLowerCase();
        if (!user.getEmail().equalsIgnoreCase(email) && userRepository.existsByEmailIgnoreCase(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        }
        user.setEmail(email);
        user.setFirstName(request.firstName().trim());
        user.setLastName(request.lastName().trim());
        if (request.password() != null && !request.password().isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(request.password()));
        }
        if (request.enabled() != null) {
            user.setEnabled(request.enabled());
        }
        user.setRoles(resolveRoles(request.roles()));
        return UserResponse.from(userRepository.save(user));
    }

    @Transactional
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        userRepository.deleteById(id);
    }

    private User createUserEntity(
            String email,
            String password,
            String firstName,
            String lastName,
            Set<RoleName> roleNames,
            boolean enabled) {
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        }
        User user = new User();
        user.setEmail(email.trim().toLowerCase());
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setFirstName(firstName.trim());
        user.setLastName(lastName.trim());
        user.setEnabled(enabled);
        user.setRoles(resolveRoles(roleNames));
        return userRepository.save(user);
    }

    private User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    private Set<Role> resolveRoles(Set<RoleName> roleNames) {
        return roleNames.stream()
                .map(this::requireRole)
                .collect(Collectors.toCollection(HashSet::new));
    }

    private Role requireRole(RoleName name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Role missing: " + name));
    }
}
