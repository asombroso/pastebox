package com.pastebox.pastebox.security.antibruteforce;

import com.pastebox.pastebox.security.antibruteforce.event.LoginFailedEvent;
import com.pastebox.pastebox.security.model.User;
import com.pastebox.pastebox.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class FailedAuthenticationHandler {

    private final UserRepository userRepository;

    @Autowired
    public FailedAuthenticationHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @EventListener(LoginFailedEvent.class)
    public void handleFailedEvent(LoginFailedEvent event) {
        Optional<User> user = userRepository.findUserByUsername(event.getUsername());
        User foundUser = user.orElseThrow(() -> new UsernameNotFoundException("Username isn't found"));
        if (foundUser.getFailedLogin() > 5) {
            foundUser.setEnabled(false);
            userRepository.save(foundUser);
            enableUser(foundUser);
        } else {
            foundUser.setFailedLogin(foundUser.getFailedLogin() + 1);
            userRepository.save(foundUser);
        }
    }

    public void enableUser(User user) {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.schedule(() -> {
            user.setEnabled(true);
            user.setFailedLogin(0);
            userRepository.save(user);
        }, 30, TimeUnit.MINUTES);
    }
}