package com.pastebox.pastebox.security.service;

import com.pastebox.pastebox.security.repository.UserRepository;
import com.pastebox.pastebox.security.model.SecurityUser;
import com.pastebox.pastebox.security.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class JPAUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public JPAUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        Optional<User> user = userRepository.findUserByUsername(username);
        return user.map(SecurityUser::new)
                .orElseThrow( () -> new UsernameNotFoundException("Username isn't found " + username));
    }
}
