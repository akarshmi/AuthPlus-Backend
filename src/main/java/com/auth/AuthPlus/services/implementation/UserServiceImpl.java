package com.auth.AuthPlus.services.implementation;

import com.auth.AuthPlus.configs.AppConstants;
import com.auth.AuthPlus.dtos.RoleDto;
import com.auth.AuthPlus.dtos.UserDto;
import com.auth.AuthPlus.entities.Provider;
import com.auth.AuthPlus.entities.Role;
import com.auth.AuthPlus.entities.User;
import com.auth.AuthPlus.exceptions.ResourceNotFoundException;
import com.auth.AuthPlus.helper.UserHelper;
import com.auth.AuthPlus.repositories.RoleRepository;
import com.auth.AuthPlus.repositories.UserRepository;
import com.auth.AuthPlus.services.UserService;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Getter
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final RoleRepository roleRepository;


    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {
        if (userDto.getEmail()==null || userDto.getEmail().isBlank()){
            throw new IllegalArgumentException("Can't be null!, Email is required");
        }
        if(userRepository.existsUserByEmail(userDto.getEmail())){
            throw new IllegalArgumentException("User is already exists with this Email Address.");
        }

        User user = modelMapper.map(userDto, User.class);
        user.setProvider(userDto.getProvider()!=null ? userDto.getProvider() : Provider.LOCAL);

        //role assignment to new guest user.
        Role role = roleRepository.findByName("ROLE_" + AppConstants.ROLE_USER).orElse(null);
        user.getRoles().add(role);

        User savedUser = userRepository.save(user);
        return modelMapper.map(savedUser, UserDto.class);
    }

    @Override
    public UserDto updateUser(UserDto userDto, String userId) {
        UUID uId = UserHelper.parseUUID(userId);
        User existingUser = userRepository
                .findById(uId)
                .orElseThrow(()-> new ResourceNotFoundException("User not found with given id!"));
        if (userDto.getName()!=null){existingUser.setName(userDto.getName());}
        if (userDto.getPassword()!=null){existingUser.setPassword(userDto.getPassword());}
        if (userDto.getImage()!=null){existingUser.setImage(userDto.getImage());}
//        if (userDto.getProvider()!=null){existingUser.setProvider(userDto.getProvider());}
        existingUser.setEnabled(userDto.isEnabled());
        existingUser.setUpdatedAt(Instant.now());

        if (userDto.getEmail() != null) {
            if (!existingUser.getProvider().equals(Provider.LOCAL)) {
                throw new IllegalArgumentException("OAuth2 users can't change their email address.");
            }
            Optional<User> userWithSameEmail = userRepository.findByEmail(userDto.getEmail());
            if (userWithSameEmail.isPresent() &&
                    !userWithSameEmail.get().getProvider().equals(existingUser.getProvider())) {
                throw new IllegalArgumentException("User already exists with this Email Address.");
            }
            existingUser.setEmail(userDto.getEmail());
        }



        User updatedUser = userRepository.save(existingUser);
        return modelMapper.map(updatedUser, UserDto.class);
    }

    @Override
    public void deleteUser(String userId) {
        UUID uid = UserHelper.parseUUID(userId);
        User user = userRepository.findById(uid).orElseThrow(()-> new ResourceNotFoundException("User not found!!"));
        userRepository.delete(user);

    }

    @Override
    public UserDto getUser(UserDto userDto) {
        return null;
    }

    @Override
    public UserDto getUserById(String uid) {
        User user = userRepository.findById(UserHelper.parseUUID(uid)).orElseThrow(()-> new ResourceNotFoundException("User not found with given ID!!"));

        return modelMapper.map(user, UserDto.class);

    }

    @Override
    public UserDto getUserByName(String name) {
        return null;
    }

    @Override
    public UserDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with given email id!"));
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public UserDto getUserByRole(RoleDto roleDto) {
        return null;
    }

    @Override
    public UserDto existsUserByEmail(String email) {
        return null;
    }

    @Override
    @Transactional
    public Iterable<UserDto> getAllUsers(){
        return userRepository
                .findAll()
                .stream()
                .map(user -> modelMapper.map(user, UserDto.class))
                .toList();
    }

}
