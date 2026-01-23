package com.auth.AuthPlus.services;

import com.auth.AuthPlus.dtos.UserDto;

public interface AuthService
{

    UserDto registerUser(UserDto userDto);

}
