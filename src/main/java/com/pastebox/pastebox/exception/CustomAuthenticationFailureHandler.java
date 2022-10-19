package com.pastebox.pastebox.exception;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        response.setStatus(401);
        if (exception instanceof UsernameNotFoundException || exception instanceof BadCredentialsException
                || exception instanceof CustomAuthenticationException) {
            response.sendRedirect("/fail");
        } else if (exception instanceof DisabledException) {
            response.sendRedirect("/disabled");
        }
    }
}