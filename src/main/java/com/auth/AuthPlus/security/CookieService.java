package com.auth.AuthPlus.security;

import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Service
@Getter
public class CookieService {

    private final String refreshTokenCookieName;
    private final boolean cookieHttpOnly;
    private final boolean cookieSecure;
//    private final String cookieMaxAge;
//    private final String cookieDomain;
    private final String cookieSameSite;

    public CookieService(@Value("${security.jwt.refresh-token-cookie-name}") String refreshTokenCookieName,
                         @Value("${security.jwt.cookie-http-only}") boolean cookieHttpOnly,
                         @Value("${security.jwt.cookie-secure}") boolean cookieSecure,

                         @Value("${security.jwt.cookie-same-site}") String cookieSameSite
    ) {
        this.refreshTokenCookieName = refreshTokenCookieName;
        this.cookieHttpOnly = cookieHttpOnly;
        this.cookieSecure = cookieSecure;
//        this.cookieDomain = cookieDomain;
        this.cookieSameSite = cookieSameSite;
    }


//    now we will create the method to attach cookie response

    public void attachRefreshCookie(HttpServletResponse response, String refreshToken, int maxAge) {



        ResponseCookie.ResponseCookieBuilder responseCookieBuilder = ResponseCookie.from(refreshTokenCookieName, refreshToken)
                .httpOnly(cookieHttpOnly)
                .secure(cookieSecure)
                .sameSite(cookieSameSite)
                .path("/")
                .maxAge(maxAge);

//        if (cookieDomain != null && !cookieDomain.isEmpty()) {
//            responseCookieBuilder.domain(cookieDomain);
//        }
        ResponseCookie responseCookie = responseCookieBuilder.build();
        response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());

    }

    //Clear Refresh Cookie at logout

    public void clearRefreshCookie(HttpServletResponse response) {

        ResponseCookie.ResponseCookieBuilder responseCookieBuilder = ResponseCookie.from(refreshTokenCookieName, "")
                .httpOnly(cookieHttpOnly)
                .secure(cookieSecure)
                .sameSite(cookieSameSite)
                .path("/")
                .maxAge(0);
//        if (!cookieDomain.isBlank()) {
//            responseCookieBuilder.domain(cookieDomain);
//        }

        ResponseCookie responseCookie = responseCookieBuilder.build();
        response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());
    }

    public void addNoStoreHeaders(HttpServletResponse response) {
        response.addHeader(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, max-age=0");
        response.addHeader(HttpHeaders.PRAGMA, "no-cache");
    }


}