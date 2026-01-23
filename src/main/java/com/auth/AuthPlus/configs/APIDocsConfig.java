package com.auth.AuthPlus.configs;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "AuthPlus is build by Akarsh Mishra.",
                description = "Its a learning project which was created by me using 'JAVA SPRING BOOT' and thanks to Durgesh Tiwari sir he directed me to through out the development journey. .",
                contact = @Contact(
                        name = "Akarsh Mishra",
                        url = "https://akarshmi.vercel.app/",
                        email = "akarshmi.am@gmail.com"
                ),
                version = "1.0",
                summary = "This would be useful for your project while adding Spring Security for the secure version of your project with minimal configurations."



        )
        ,
        security = {
                @SecurityRequirement(
                        name="bearerAuth"
                )
        }


)

@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer", //Authorization: Bearer,
        bearerFormat = "JWT"

)
public class APIDocsConfig {


}