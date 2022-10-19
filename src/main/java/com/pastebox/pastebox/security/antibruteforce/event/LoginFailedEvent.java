package com.pastebox.pastebox.security.antibruteforce.event;

import org.springframework.context.ApplicationEvent;

public class LoginFailedEvent extends ApplicationEvent {

    private String username;

    public LoginFailedEvent(Object source, String username) {
        super(source);
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}