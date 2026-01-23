package com.auth.AuthPlus.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Collection;
import java.util.UUID;


@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "roles")
public class Role {
    @Id
    private UUID roleId=UUID.randomUUID();
    @Column(unique = true,nullable = true)
    private String name;

    @ManyToMany(mappedBy = "roles")
    private Collection<User> users;
}
