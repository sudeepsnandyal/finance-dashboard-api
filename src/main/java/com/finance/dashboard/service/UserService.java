package com.finance.dashboard.service;

import com.finance.dashboard.model.dto.UserDTO;

import java.util.List;
import java.util.Set;

public interface UserService {
    UserDTO createUser(UserDTO userDTO);
    UserDTO getUserById(Long id);
    UserDTO getUserByUsername(String username);
    List<UserDTO> getAllUsers();
    UserDTO updateUser(Long id, UserDTO userDTO);
    void deleteUser(Long id);
    void changeUserStatus(Long id, boolean active);
    UserDTO assignRoles(Long userId, Set<com.finance.dashboard.model.enums.RoleType> roles);
}
