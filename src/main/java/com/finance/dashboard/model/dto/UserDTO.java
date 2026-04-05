package com.finance.dashboard.model.dto;

import com.finance.dashboard.model.enums.RoleType;
import com.finance.dashboard.model.enums.UserStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private Long id;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    private UserStatus status;

    @Builder.Default
    @Valid
    private Set<RoleType> roles = new HashSet<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
