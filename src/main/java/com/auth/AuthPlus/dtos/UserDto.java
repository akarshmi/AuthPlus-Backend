package com.auth.AuthPlus.dtos;

import com.auth.AuthPlus.entities.Provider;
import com.auth.AuthPlus.entities.Role;
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
    }
}
