package com.auth.AuthPlus.repositories;

import com.auth.AuthPlus.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    boolean existsUserByEmail(String email);
    Optional<User> findByEmail(String email);

}
