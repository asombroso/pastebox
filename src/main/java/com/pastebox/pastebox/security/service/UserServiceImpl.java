package com.pastebox.pastebox.security.service;

import com.pastebox.pastebox.exception.InvalidCodeException;
import com.pastebox.pastebox.exception.UserAlreadyExistException;
import com.pastebox.pastebox.security.mail.EmailService;
import com.pastebox.pastebox.security.mfa.manager.CustomMfaTokenManager;
import com.pastebox.pastebox.security.mfa.model.MfaTokenData;
import com.pastebox.pastebox.security.model.User;
import com.pastebox.pastebox.security.model.UserDto;
import com.pastebox.pastebox.security.repository.UserRepository;
import dev.samstevens.totp.exceptions.QrGenerationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JPAUserDetailsService userDetailsService;
    private final CustomMfaTokenManager mfaTokenManager;
    private final ApplicationEventPublisher eventPublisher;
    private final EmailService emailService;

    @Autowired
    public UserServiceImpl(PasswordEncoder passwordEncoder, UserRepository userRepository,
                           JPAUserDetailsService userDetailsService, CustomMfaTokenManager mfaTokenManager,
                           ApplicationEventPublisher eventPublisher, EmailService emailService) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.userDetailsService = userDetailsService;
        this.mfaTokenManager = mfaTokenManager;
        this.eventPublisher = eventPublisher;
        this.emailService = emailService;
    }

    @Override
    public User registerUser(UserDto userDto) {
        if (userRepository.existsByUsername(userDto.getUsername())) {
            throw new UserAlreadyExistException("User with this email already registered.");
        }
        User user = new User();
        String email = userDto.getUsername();
        user.setUsername(email);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setEnabled(false);
        user.setRoles(Set.of("USER"));
        if(userDto.isMfa() != null){
            user.setMfaEnabled(true);
            user.setSecret(mfaTokenManager.generateSecretKey());
        }
        String code = emailService.generateCode();
        user.setCode(code);
        emailService.sendMessage(code, email);
        return userRepository.save(user);
    }

    public MfaTokenData mfaSetup(String email){
        Optional<User> user = userRepository.findUserByUsername(email);
        User foundUser = user.orElseThrow(() -> new UsernameNotFoundException("Username isn't found."));
        MfaTokenData mfaTokenData;
        try {
            mfaTokenData = new MfaTokenData(mfaTokenManager.getQrCode(foundUser.getSecret()), foundUser.getSecret());
        } catch (QrGenerationException ex) {
            throw new RuntimeException(ex);
        }
        return mfaTokenData;
    }

    public void confirmUser(String code){
        Optional<User> optionalUser = userRepository.findUserByCode(code);
        if(optionalUser.isPresent()){
            User user = optionalUser.get();
            user.setEnabled(true);
            user.setCode("");
            userRepository.save(user);
        } else {
            throw new InvalidCodeException("Code is invalid or already registered.");
        }
    }
}
