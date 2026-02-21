package com.auth.AuthPlus.dtos;

import com.auth.AuthPlus.entities.Provider;
import com.auth.AuthPlus.entities.Role;
import com.auth.AuthPlus.entities.User;
import lombok.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {

    private UUID userId;
    private String email;
    private String name;
    private String password;
    private String image;
    private boolean enabled = true;
    private Instant createdAt = Instant.now();
    private Instant updatedAt = Instant.now();
    private Provider provider = Provider.LOCAL;
    private Set<RoleDto> roles = new HashSet<>();


    public UserDto(UUID userId, String email, String name, String image, boolean enabled, Instant createdAt, Instant updatedAt, Provider provider, Set<Role> roles) {
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.image = image;
        this.enabled = enabled;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.provider = provider;
        this.roles = roles != null
                ? roles.stream()
                .map(role -> new RoleDto(role.getRoleId(), role.getName()))
                .collect(java.util.stream.Collectors.toSet())
                : new HashSet<>();
    }


    public static UserDto from(User user) {
        UserDto dto = new UserDto();
        dto.setUserId(user.getUserId());
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());
        dto.setImage(user.getImage());
        dto.setEnabled(user.isEnabled());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        dto.setProvider(user.getProvider());
        dto.setRoles(user.getRoles() != null
                ? user.getRoles().stream()
                .map(role -> new RoleDto(role.getRoleId(), role.getName()))
                .collect(java.util.stream.Collectors.toSet())
                : new HashSet<>());
        return dto;
    }
}
