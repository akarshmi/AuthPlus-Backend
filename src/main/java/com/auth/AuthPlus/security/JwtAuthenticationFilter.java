package com.auth.AuthPlus.security;

import com.auth.AuthPlus.exceptions.InvalidTokenException;
import com.auth.AuthPlus.helper.UserHelper;
import com.auth.AuthPlus.repositories.UserRepository;
import io.jsonwebtoken.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;



    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {


        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {

            //First Extract Token and validate and then create authentication and finally set into SecurityContext

            String token = header.substring("Bearer ".length());

            try {
                Jws<Claims> claimsJws = jwtService.parseToken(token);

                //Check Access Token
                if (!jwtService.isAccessToken(token)) {
                    filterChain.doFilter(request, response);
                    return;
                }

                Claims payload = claimsJws.getPayload();
                String userId = payload.getSubject();

                UUID userUUID = UserHelper.parseUUID(userId);

                userRepository.findById(userUUID).ifPresent(user -> {
                    // If user is not enabled
                    if (user.isEnabled()){

                        // User is found from the database.....
                        List<GrantedAuthority> authorities = user.getRoles()==null ? List.of() :user.getRoles().stream()
                                .map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());

                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userId, null, authorities);

                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    //This line by which we finally set authentication to security context
                        if (SecurityContextHolder.getContext().getAuthentication() == null){
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                        }

                    }



                });


            }catch (ExpiredJwtException ex) {
                request.setAttribute("error", "Token expired");

            } catch (SecurityException ex) {
                request.setAttribute("error", "Invalid token signature");

            } catch (MalformedJwtException ex) {
                request.setAttribute("error", "Malformed token");

            } catch (UnsupportedJwtException ex) {
                request.setAttribute("error", "Unsupported token");

            } catch (IllegalArgumentException ex) {
                request.setAttribute("error", "Token is empty");

            } catch (JwtException ex) {
                request.setAttribute("error", "Invalid token");

            } catch (InvalidTokenException e) {
                request.setAttribute("error", "Invalid token!!");

            } catch (Exception e) {
                request.setAttribute("error", "Unexpected error!");
            }
        }
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return request.getRequestURI().startsWith("/api/v1/auth");
    }


}
