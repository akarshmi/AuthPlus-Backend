package com.auth.AuthPlus.repositories;

import com.auth.AuthPlus.entities.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

   Optional<RefreshToken> findByJti(String jti);

}
