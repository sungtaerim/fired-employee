package com.lepse.employee.config;

import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class RESTConfig implements WebMvcConfigurer {
    public RESTConfig() {
    }

    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.defaultContentType(MediaType.APPLICATION_XML);
    }
}
