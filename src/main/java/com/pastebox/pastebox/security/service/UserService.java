package com.pastebox.pastebox.security.service;

import com.pastebox.pastebox.security.model.User;
import com.pastebox.pastebox.security.model.UserDto;

public interface UserService {

    User registerUser(UserDto userDto);
    void confirmUser(String code);
}
