package com.auth.AuthPlus.controllers;


import com.auth.AuthPlus.dtos.LoginRequest;
import com.auth.AuthPlus.dtos.RefreshTokenRequest;
import com.auth.AuthPlus.dtos.TokenResponse;
import com.auth.AuthPlus.dtos.UserDto;
import com.auth.AuthPlus.entities.RefreshToken;
import com.auth.AuthPlus.entities.User;
import com.auth.AuthPlus.repositories.RefreshTokenRepository;
import com.auth.AuthPlus.repositories.UserRepository;
import com.auth.AuthPlus.security.CookieService;
import com.auth.AuthPlus.security.JwtService;
import com.auth.AuthPlus.services.AuthService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthenticationManager authenticationManager;
    private final CookieService cookieService;
    private final JwtService jwtService;
    private final ModelMapper mapper;


    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response){

        // We will authenticate the login req

        Authentication authentication = authenticate(loginRequest);

        User user = userRepository.findByEmail(loginRequest.email()).orElseThrow(()-> new BadCredentialsException("Invalid Username or Password!!"));
        if (!user.isEnabled()){
            throw new DisabledException("User is disabled!!");
        }

        //generate the refresh token

        String jti = UUID.randomUUID().toString();
        var refreshTokenObj = RefreshToken.builder()
                .jti(jti)
                .user(user)
                .createdAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(jwtService.getRefreshTokenTTL()))
                .revoked(false)
                .build();
        // save this info of the refresh token in the db
        refreshTokenRepository.save(refreshTokenObj);

//      generate the access token
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user, refreshTokenObj.getJti());

        //user cookie service to attach refresh token
        cookieService.attachRefreshCookie(response,refreshToken, (int) jwtService.getRefreshTokenTTL());
        cookieService.addNoStoreHeaders(response);

        TokenResponse tokenResponse = TokenResponse.of(accessToken, refreshToken, jwtService.getAccessTokenTTL(), mapper.map(user, UserDto.class));

        return ResponseEntity.ok(tokenResponse);
    }


//    API for renewing the refresh token and access token...
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> renewTokens(@RequestBody (required = false) RefreshTokenRequest  refreshTokenRequest, HttpServletResponse response, HttpServletRequest request){
        String refreshToken = readRefreshTokenFromCookie(refreshTokenRequest,request).orElseThrow(()-> new BadCredentialsException("Refresh Token Not Found!!"));

        if (!jwtService.isRefreshToken(refreshToken)){
            throw new BadCredentialsException("Invalid Refresh Token!!!");
        }

        String jti = jwtService.getJti(refreshToken);
        IO.println("jti: " + jti);

        UUID userId = jwtService.getUserId(refreshToken);
        RefreshToken storedRefreshToken = refreshTokenRepository.findByJti(jti).orElseThrow(() -> new BadCredentialsException("Refresh Token Not Recognized!!"));

        if (storedRefreshToken.getExpiresAt().isBefore(Instant.now())){
            throw new BadCredentialsException("Refresh Token Expired or Revoked!!!");
        }
        if (storedRefreshToken.isRevoked()){
            throw new BadCredentialsException("Refresh Token Revoked or Revoked!!!");
        }

        if (!storedRefreshToken.getUser().getUserId().equals(userId)){
            throw new BadCredentialsException("Refresh Token doesn't belong to this User!!!");
        }


        // Rotating the refresh tokens

        storedRefreshToken.setRevoked(true);
        String newJti = UUID.randomUUID().toString();
        storedRefreshToken.setReplacedByToken(newJti);
        refreshTokenRepository.save(storedRefreshToken);

        User user = storedRefreshToken.getUser();

        var newRefreshTokenObj = RefreshToken.builder()
                .jti(newJti)
                .user(user)
                .createdAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(jwtService.getRefreshTokenTTL()))
                .revoked(false)
                .build();

        refreshTokenRepository.save(newRefreshTokenObj);

        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken  =  jwtService.generateRefreshToken(user, newRefreshTokenObj.getJti());

        cookieService.attachRefreshCookie(response,newRefreshToken, (int) jwtService.getRefreshTokenTTL());
        cookieService.addNoStoreHeaders(response);
        return ResponseEntity.ok(TokenResponse.of(newAccessToken,newRefreshToken,jwtService.getAccessTokenTTL(), mapper.map(user, UserDto.class)));

    }






    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response, HttpServletRequest request) {
        readRefreshTokenFromCookie(null,request).ifPresent(token -> {
            try {
                if (jwtService.isRefreshToken(token)){
                    String jti = jwtService.getJti(token);
                    refreshTokenRepository.findByJti(jti).ifPresent(refreshTokenObj -> {
                        refreshTokenObj.setRevoked(true);
                        refreshTokenRepository.save(refreshTokenObj);
                    });
                }
            }catch (JwtException ignored){
            }
        });

        cookieService.clearRefreshCookie(response);
        cookieService.addNoStoreHeaders(response);
        SecurityContextHolder.clearContext();
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();


    }




//    This will return refresh token form header or cookie
    private Optional<String> readRefreshTokenFromCookie(RefreshTokenRequest refreshTokenRequest, HttpServletRequest request) {
//        1. prefer reading refresh token from cookie
        if (request.getCookies() != null) {
            Optional<String> fromCookie = Arrays.stream(request.getCookies())
                    .filter(c -> cookieService.getRefreshTokenCookieName().equals(c.getName()))
                    .map(Cookie::getValue)
                    .filter(v -> !v.isBlank())
                    .findFirst();

            if (fromCookie.isPresent()) {
                return fromCookie;
            }

        }

//        2. if the cookie is in the JSON body
        if (refreshTokenRequest!=null && refreshTokenRequest.refreshToken() != null && !refreshTokenRequest.refreshToken().isEmpty()) {
            return Optional.of(refreshTokenRequest.refreshToken());
        }

        // 3. custom header

        String refreshHeader = request.getHeader("X-Refresh-Token");
        if (refreshHeader != null && !refreshHeader.isBlank()) {
            return Optional.of(refreshHeader.trim());
        }

        // Authorization  = Bearer <token>
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String candidate = authHeader.substring(7).trim();
            if (!candidate.isEmpty()) {
                try {
                    if (jwtService.isRefreshToken(candidate)) {
                        return Optional.of(candidate);
                    }
                }catch (Exception ignored){

                }
            }
        }

        return Optional.empty();


    }

    private Authentication authenticate(LoginRequest loginRequest) {

        try
        {
            return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.email(),loginRequest.password()));

        }catch (Exception e){
            throw new BadCredentialsException("Invalid Username Or Password !!");
        }
    }


    @PostMapping("/register")
    public ResponseEntity<UserDto> registerUser(@RequestBody UserDto userDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registerUser(userDto));
    }


    @GetMapping("/me")
    public ResponseEntity<UserDto> me(Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = (User) authentication.getPrincipal();

        return ResponseEntity.ok(new UserDto(
                user.getUserId(),
                user.getEmail(),
                user.getProvider(),
                user.getImage()
        ));
    }





}
