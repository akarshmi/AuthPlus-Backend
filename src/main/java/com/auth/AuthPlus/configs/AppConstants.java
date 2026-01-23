package com.auth.AuthPlus.configs;

public class AppConstants {


    public static final String [] PUBLIC_ACCESS_URLS = {
            "/api/v1/auth/**",
            "/v3/api-docs/**",
            "/swagger-ui.html/**",
            "/swagger-ui/**",

    };

    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_USER = "USER";

}
