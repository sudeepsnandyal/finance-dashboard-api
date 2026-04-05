package com.finance.dashboard.service;

import com.finance.dashboard.model.User;
import com.finance.dashboard.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        if (user.getStatus() == com.finance.dashboard.model.enums.UserStatus.INACTIVE) {
            throw new UsernameNotFoundException("User account is inactive");
        }

        return user;
    }

    @Transactional
 public UserDetails loadUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));

        if (user.getStatus() == com.finance.dashboard.model.enums.UserStatus.INACTIVE) {
            throw new UsernameNotFoundException("User account is inactive");
        }

        return user;
    }
}
