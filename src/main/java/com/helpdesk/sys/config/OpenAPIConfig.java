package com.helpdesk.sys.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Smart IT Helpdesk System API",
                version = "1.0.0",
                description = "REST API Documentation for Smart IT Helpdesk System with Gen AI Triage, PL/SQL SLA Engine, and Spring Security JWT Authentication.",
                contact = @Contact(
                        name = "Cognizant GenC Next Candidate Portfolio",
                        email = "candidate@cognizant.com"
                )
        )
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
public class OpenAPIConfig {
}
