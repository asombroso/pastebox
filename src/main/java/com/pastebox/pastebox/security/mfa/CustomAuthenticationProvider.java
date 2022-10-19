package com.pastebox.pastebox.security.mfa;

import com.pastebox.pastebox.exception.CustomAuthenticationException;
import com.pastebox.pastebox.security.antibruteforce.event.LoginFailedEvent;
import com.pastebox.pastebox.security.mfa.manager.MfaTokenManager;
import com.pastebox.pastebox.security.model.User;
import com.pastebox.pastebox.security.repository.UserRepository;
import com.pastebox.pastebox.security.service.JPAUserDetailsService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.List;
import java.util.Optional;

public class CustomAuthenticationProvider extends DaoAuthenticationProvider {

    private final MfaTokenManager mfaTokenManager;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JPAUserDetailsService userDetailsService;
    private final ApplicationEventPublisher eventPublisher;

    public CustomAuthenticationProvider(UserRepository userRepository, PasswordEncoder passwordEncoder,
                                        MfaTokenManager mfaTokenManager, JPAUserDetailsService userDetailsService,
                                        ApplicationEventPublisher eventPublisher) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.mfaTokenManager = mfaTokenManager;
        this.userDetailsService = userDetailsService;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Authentication authenticate(Authentication auth) throws AuthenticationException {
        String mfaToken = ((CustomWebAuthenticationDetails) auth.getDetails()).getToken();
        Optional<User> user = userRepository.findUserByUsername(auth.getName());
        UserDetails userDetails = userDetailsService.loadUserByUsername(auth.getName());
        User foundUser = user.orElseThrow(() -> new UsernameNotFoundException("User isn't found."));
        if (!foundUser.isEnabled()) {
            throw new DisabledException("User is disabled.");
        }
        if (foundUser.isMfaEnabled() && !mfaTokenManager.verifyCode(mfaToken, foundUser.getSecret())) {
            eventPublisher.publishEvent(new LoginFailedEvent(this, foundUser.getUsername()));
            throw new CustomAuthenticationException("Mfa is incorrect.");
        }
        if (passwordEncoder.matches((String) auth.getCredentials(), userDetails.getPassword())){
            foundUser.setFailedLogin(0);
            userRepository.save(foundUser);
            return UsernamePasswordAuthenticationToken.authenticated(foundUser, foundUser.getPassword(),
                    List.of( () -> "USER"));
        } else {
            eventPublisher.publishEvent(new LoginFailedEvent(this, foundUser.getUsername()));
            throw new BadCredentialsException("Password is incorrect.");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}