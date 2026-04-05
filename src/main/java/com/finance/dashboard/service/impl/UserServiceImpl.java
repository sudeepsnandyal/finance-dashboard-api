package com.finance.dashboard.service.impl;

import com.finance.dashboard.exception.AccessDeniedException;
import com.finance.dashboard.exception.ResourceAlreadyExistsException;
import com.finance.dashboard.exception.ResourceNotFoundException;
import com.finance.dashboard.model.User;
import com.finance.dashboard.model.enums.RoleType;
import com.finance.dashboard.model.enums.UserStatus;
import com.finance.dashboard.model.dto.UserDTO;
import com.finance.dashboard.repository.UserRepository;
import com.finance.dashboard.service.CustomUserDetailsService;
import com.finance.dashboard.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    @Transactional
    public UserDTO createUser(UserDTO userDTO) {
        // Check for existing username/email
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new ResourceAlreadyExistsException("Username already exists: " + userDTO.getUsername());
        }
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new ResourceAlreadyExistsException("Email already exists: " + userDTO.getEmail());
        }

        // Build user entity
        User user = User.builder()
                .username(userDTO.getUsername().toLowerCase())
                .email(userDTO.getEmail().toLowerCase())
                .password(passwordEncoder.encode(userDTO.getPassword()))
                .firstName(userDTO.getFirstName())
                .lastName(userDTO.getLastName())
                .status(userDTO.getStatus() != null ? userDTO.getStatus() : UserStatus.ACTIVE)
                .roles(userDTO.getRoles() != null ? userDTO.getRoles() : Set.of(RoleType.VIEWER))
                .build();

        User savedUser = userRepository.save(user);
        logger.info("Created user with id: {}", savedUser.getId());

        return convertToDTO(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        return convertToDTO(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getUserByUsername(String username) {
        User user = userRepository.findByUsername(username.toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        return convertToDTO(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        // Check current user permissions
        checkAdminOrSelf(user.getId());

        // Prevent email/username conflicts if changed
        if (!user.getUsername().equals(userDTO.getUsername().toLowerCase()) &&
                userRepository.existsByUsername(userDTO.getUsername().toLowerCase())) {
            throw new ResourceAlreadyExistsException("Username already exists: " + userDTO.getUsername());
        }
        if (!user.getEmail().equals(userDTO.getEmail().toLowerCase()) &&
                userRepository.existsByEmail(userDTO.getEmail().toLowerCase())) {
            throw new ResourceAlreadyExistsException("Email already exists: " + userDTO.getEmail());
        }

        // Update fields (excluding password for security)
        user.setUsername(userDTO.getUsername().toLowerCase());
        user.setEmail(userDTO.getEmail().toLowerCase());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        if (userDTO.getStatus() != null) {
            user.setStatus(userDTO.getStatus());
        }
        if (userDTO.getRoles() != null) {
            user.setRoles(userDTO.getRoles());
        }

        User updatedUser = userRepository.save(user);
        logger.info("Updated user with id: {}", updatedUser.getId());

        return convertToDTO(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        checkAdminOrSelf(user.getId());

        userRepository.delete(user);
        logger.info("Deleted user with id: {}", id);
    }

    @Override
    @Transactional
    public void changeUserStatus(Long id, boolean active) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        user.setStatus(active ? UserStatus.ACTIVE : UserStatus.INACTIVE);
        userRepository.save(user);
        logger.info("Changed user {} status to: {}", id, active ? "ACTIVE" : "INACTIVE");
    }

    @Override
    @Transactional
    public UserDTO assignRoles(Long userId, Set<RoleType> roles) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Only admins can assign roles (checked via @PreAuthorize)
        user.setRoles(roles);
        User updatedUser = userRepository.save(user);
        logger.info("Assigned roles {} to user: {}", roles, userId);

        return convertToDTO(updatedUser);
    }

    private UserDTO convertToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .status(user.getStatus())
                .roles(new HashSet<>(user.getRoles()))
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    private void checkAdminOrSelf(Long userId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new AccessDeniedException("Authentication required");
        }

        UserDetails currentUser = (UserDetails) auth.getPrincipal();
        User current = userRepository.findByUsername(currentUser.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Current user not found"));

        boolean isAdmin = current.getRoles().contains(RoleType.ADMIN);
        boolean isSelf = current.getId().equals(userId);

        if (!isAdmin && !isSelf) {
            throw new AccessDeniedException("You can only modify your own profile unless you are an admin");
        }
    }
}
