package com.example.springmvc.config;

import com.example.springmvc.domain.RoleName;
import com.example.springmvc.domain.User;
import com.example.springmvc.repository.RoleRepository;
import com.example.springmvc.repository.UserRepository;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (userRepository.existsByEmailIgnoreCase("admin@example.com")) {
            return;
        }
        User admin = new User();
        admin.setEmail("admin@example.com");
        admin.setFirstName("System");
        admin.setLastName("Admin");
        admin.setPasswordHash(passwordEncoder.encode("Admin123!"));
        admin.setEnabled(true);
        admin.setRoles(Set.of(
                roleRepository.findByName(RoleName.ADMIN).orElseThrow(),
                roleRepository.findByName(RoleName.USER).orElseThrow()
        ));
        userRepository.save(admin);
        log.info("Seeded demo admin user admin@example.com");
    }
}
