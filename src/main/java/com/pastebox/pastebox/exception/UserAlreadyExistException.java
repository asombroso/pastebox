package com.pastebox.pastebox.exception;

public class UserAlreadyExistException extends RuntimeException{
    public UserAlreadyExistException(String s) {
        super(s);
    }
}
