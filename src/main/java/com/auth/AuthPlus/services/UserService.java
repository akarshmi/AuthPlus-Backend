package com.auth.AuthPlus.services;


import com.auth.AuthPlus.dtos.RoleDto;
import com.auth.AuthPlus.dtos.UserDto;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface UserService {

//    create user
    UserDto createUser(UserDto userDto);
    void deleteUser(String id);
    UserDto updateUser(UserDto userDto,String id);
    UserDto getUser(UserDto userDto);
    UserDto getUserById(String id);
    UserDto getUserByName(String name);
    UserDto getUserByEmail(String email);
    UserDto getUserByRole(RoleDto roleDto);
    UserDto existsUserByEmail(String email);
    Iterable<UserDto> getAllUsers();
//    UserDto existsUserByName(String name);

}
