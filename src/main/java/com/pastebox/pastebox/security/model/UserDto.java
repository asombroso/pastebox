package com.pastebox.pastebox.security.model;

import org.springframework.stereotype.Service;
import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;

@Service
public class UserDto {

    @Email
    private String username;

    @Pattern(regexp="^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{9,100}$",
            message = "Password is weak, check rules for a password strength below.")
    private String password;

    private String token;

    private String mfa;

    public UserDto() {
    }

    public UserDto(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String isMfa() {
        return mfa;
    }

    public void setMfa(String mfa) {
        this.mfa = mfa;
    }
}