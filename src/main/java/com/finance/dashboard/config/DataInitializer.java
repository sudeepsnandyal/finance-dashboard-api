package com.finance.dashboard.config;

import com.finance.dashboard.model.enums.RoleType;
import com.finance.dashboard.model.enums.UserStatus;
import com.finance.dashboard.model.User;
import com.finance.dashboard.repository.UserRepository;
import com.finance.dashboard.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    public void run(String... args) throws Exception {
        // Check if admin user exists
        if (!userRepository.existsByUsername("admin")) {
            User admin = User.builder()
                    .username("admin")
                    .email("admin@financedashboard.com")
                    .password(passwordEncoder.encode("admin123"))
                    .firstName("Admin")
                    .lastName("User")
                    .status(UserStatus.ACTIVE)
                    .roles(Set.of(RoleType.ADMIN))
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            userRepository.save(admin);
            System.out.println(">>> Created default admin user: admin / admin123");
        }

        // Check if analyst user exists
        if (!userRepository.existsByUsername("analyst")) {
            User analyst = User.builder()
                    .username("analyst")
                    .email("analyst@financedashboard.com")
                    .password(passwordEncoder.encode("analyst123"))
                    .firstName("Analyst")
                    .lastName("User")
                    .status(UserStatus.ACTIVE)
                    .roles(Set.of(RoleType.ANALYST))
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            userRepository.save(analyst);
            System.out.println(">>> Created default analyst user: analyst / analyst123");
        }

        // Check if viewer user exists
        if (!userRepository.existsByUsername("viewer")) {
            User viewer = User.builder()
                    .username("viewer")
                    .email("viewer@financedashboard.com")
                    .password(passwordEncoder.encode("viewer123"))
                    .firstName("Viewer")
                    .lastName("User")
                    .status(UserStatus.ACTIVE)
                    .roles(Set.of(RoleType.VIEWER))
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            userRepository.save(viewer);
            System.out.println(">>> Created default viewer user: viewer / viewer123");
        }
    }
}
