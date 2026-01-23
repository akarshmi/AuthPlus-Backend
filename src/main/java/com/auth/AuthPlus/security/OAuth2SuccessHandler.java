package com.auth.AuthPlus.security;

import com.auth.AuthPlus.entities.Provider;
import com.auth.AuthPlus.entities.RefreshToken;
import com.auth.AuthPlus.entities.User;
import com.auth.AuthPlus.repositories.RefreshTokenRepository;
import com.auth.AuthPlus.repositories.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final CookieService cookieService;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${app.auth.frontend.login-success-redirect-uri}")
    private String successRedirectUri;

    @Value("${app.auth.frontend.login-failure-redirect-uri}")
    private String failureRedirectUri;



    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {


        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

//        now identify the user

        String registrationId = "unknown";
        if (authentication instanceof OAuth2AuthenticationToken token){
            registrationId = token.getAuthorizedClientRegistrationId();
//            IO.println("RegistrationsId: " + registrationId);
        }

        User user;
        switch (registrationId){
            case "google" -> {
                String  googleId = oAuth2User.getAttributes().getOrDefault("sub","").toString();
                String  email = oAuth2User.getAttributes().getOrDefault("email","").toString();
                String  name = oAuth2User.getAttributes().getOrDefault("name","").toString();
                String  picture = oAuth2User.getAttributes().getOrDefault("picture","").toString();

                User newUser = User.builder()
                        .name(name)
                        .email(email)
                        .enabled(true)
                        .image(picture)
                        .provider(Provider.GOOGLE)
                        .providerId(googleId)
                        .build();
                user = userRepository.findByEmail(email).orElseGet(() -> userRepository.save(newUser));
            }

            case "github" -> {
                String name = oAuth2User.getAttributes().getOrDefault("name","").toString();
                String  picture = oAuth2User.getAttributes().getOrDefault("avatar_url","").toString();
                String  githubId = oAuth2User.getAttributes().getOrDefault("id","").toString();
                String  email = (String) oAuth2User.getAttributes().getOrDefault("email","");
                String  username = (String) oAuth2User.getAttributes().getOrDefault("login","");

                if(email==null){
                    email = username.toLowerCase()+"@github.com";
                }

                User newUser = User.builder()
                        .email(email)
                        .name(name)
                        .image(picture)
                        .provider(Provider.GITHUB)
                        .enabled(true)
                        .providerId(githubId)
                        .build();
                user = userRepository.findByEmail(email).orElseGet(() -> userRepository.save(newUser));
            }

            default -> {
                throw new RuntimeException("Invalid registration id");
            }
        }

        // creating jwt refresh and access tokens for frontend and then redirect to frontend

        String jti = UUID.randomUUID().toString();
        RefreshToken refreshTokenObj = RefreshToken.builder()
                .jti(jti)
                .user(user)
                .revoked(false)
                .createdAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(jwtService.getRefreshTokenTTL()))
                .build();

        refreshTokenRepository.save(refreshTokenObj);
        String accessToken  = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user, refreshTokenObj.getJti());

        cookieService.attachRefreshCookie(response,refreshToken,(int)jwtService.getRefreshTokenTTL());

        response.sendRedirect(successRedirectUri);
    }



}
