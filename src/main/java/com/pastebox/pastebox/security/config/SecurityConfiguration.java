package com.pastebox.pastebox.security.config;

import com.pastebox.pastebox.exception.CustomAuthenticationFailureHandler;
import com.pastebox.pastebox.security.mfa.CustomAuthenticationProvider;
import com.pastebox.pastebox.security.mfa.CustomWebAuthenticationDetailsSource;
import com.pastebox.pastebox.security.mfa.manager.MfaTokenManager;
import com.pastebox.pastebox.security.repository.UserRepository;
import com.pastebox.pastebox.security.service.JPAUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.PortMapperImpl;
import org.springframework.security.web.PortResolverImpl;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import javax.sql.DataSource;
import java.util.Collections;

@Configuration
public class SecurityConfiguration {

    private final DataSource dataSource;
    private final JPAUserDetailsService userDetailsService;
    private final CustomWebAuthenticationDetailsSource authenticationDetailsSource;
    private final UserRepository userRepository;
    private final MfaTokenManager mfaTokenManager;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public SecurityConfiguration(DataSource dataSource, JPAUserDetailsService userDetailsService,
                                 CustomWebAuthenticationDetailsSource authenticationDetailsSource,
                                 UserRepository userRepository, MfaTokenManager mfaTokenManager,
                                 ApplicationEventPublisher eventPublisher) {
        this.dataSource = dataSource;
        this.userDetailsService = userDetailsService;
        this.authenticationDetailsSource = authenticationDetailsSource;
        this.userRepository = userRepository;
        this.mfaTokenManager = mfaTokenManager;
        this.eventPublisher = eventPublisher;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(c -> c.ignoringAntMatchers("/main"));

        http.authenticationProvider(authProvider());

        http.formLogin().failureHandler(authenticationFailureHandler())
                .defaultSuccessUrl("/main")
                .loginPage("/login")
                .authenticationDetailsSource(authenticationDetailsSource);

        http.sessionManagement(session -> session
                .maximumSessions(1)
                .maxSessionsPreventsLogin(true));

        http.logout().logoutUrl("/logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .clearAuthentication(true);

        http.rememberMe().tokenRepository(persistentTokenRepository());

        PortMapperImpl portMapper = new PortMapperImpl();
        portMapper.setPortMappings(Collections.singletonMap("8080","8080"));
        PortResolverImpl portResolver = new PortResolverImpl();
        portResolver.setPortMapper(portMapper);
        LoginUrlAuthenticationEntryPoint entryPoint = new LoginUrlAuthenticationEntryPoint(
                "/login");
        entryPoint.setPortMapper(portMapper);
        entryPoint.setPortResolver(portResolver);

        http.exceptionHandling().authenticationEntryPoint(entryPoint);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public PersistentTokenRepository persistentTokenRepository(){
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);
        return jdbcTokenRepository;
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return new CustomAuthenticationFailureHandler();
    }

    @Bean
    public DaoAuthenticationProvider authProvider() {
        CustomAuthenticationProvider authProvider =
                new CustomAuthenticationProvider(userRepository, passwordEncoder(),
                        mfaTokenManager, userDetailsService, eventPublisher);
        authProvider.setUserDetailsService(userDetailsService);
        return authProvider;
    }
}
