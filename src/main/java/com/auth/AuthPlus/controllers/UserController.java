package com.auth.AuthPlus.controllers;


import com.auth.AuthPlus.configs.AppConstants;
import com.auth.AuthPlus.dtos.UserDto;
import com.auth.AuthPlus.entities.User;
import com.auth.AuthPlus.repositories.UserRepository;
import com.auth.AuthPlus.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;


    //Create User API
    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto userDto) {
        return  ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(userDto));
    }

    //Get All User API
    @PreAuthorize("hasRole('"+ AppConstants.ROLE_ADMIN +"')")
    @GetMapping
    public ResponseEntity<Iterable<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }


    //get User using Email
    @GetMapping("/email/{email}")
    public ResponseEntity<UserDto> getUserByEmail (@PathVariable String email) {
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }



//    delete user
    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
    }



//    update user
    @PutMapping("/{userId}")
    public ResponseEntity<UserDto> updateUser(@PathVariable String userId, @RequestBody UserDto userDto) {
        return ResponseEntity.ok(userService.updateUser(userDto,userId));
    }

//    get user by id
    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUserById(@PathVariable String userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> me(Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UUID userId = UUID.fromString(authentication.getName());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return ResponseEntity.ok(new UserDto(
                user.getUserId(),
                user.getEmail(),
                user.getName(),
                user.getImage(),
                user.isEnabled(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getProvider(),
                user.getRoles()
        ));
    }


    @PutMapping("/me")
    public ResponseEntity<UserDto> updateProfile(Authentication authentication,
                                                 @RequestBody UserDto userDto) {
        String userId = (String) authentication.getPrincipal();  // UUID string

        return ResponseEntity.ok(
                userService.updateUser(userDto, userId)
        );
    }






}

