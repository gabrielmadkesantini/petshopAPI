package com.petshop.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.hibernate5.jakarta.Hibernate5JakartaModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class JacksonConfig {

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        
        Hibernate5JakartaModule hibernateModule = new Hibernate5JakartaModule();
        hibernateModule.enable(Hibernate5JakartaModule.Feature.FORCE_LAZY_LOADING);
        hibernateModule.disable(Hibernate5JakartaModule.Feature.REQUIRE_EXPLICIT_LAZY_LOADING_MARKER);
        
        mapper.registerModule(hibernateModule);
        mapper.registerModule(new JavaTimeModule());
        
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        return mapper;
    }
}

