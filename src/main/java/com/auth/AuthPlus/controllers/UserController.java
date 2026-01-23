package com.auth.AuthPlus.controllers;


import com.auth.AuthPlus.dtos.UserDto;
import com.auth.AuthPlus.services.UserService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;


    //Create User API
    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto userDto) {
        return  ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(userDto));
    }

    //Get All User API
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

}

