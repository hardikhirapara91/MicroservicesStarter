package com.broadleafsamples.tutorials;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.broadleafcommerce.resource.security.SecurityEnhancer;

import java.util.Collections;

@SpringBootApplication
public class TutorialGettingStartedApplication {

    @Bean
    SecurityEnhancer cartAnonymous() {
        return http -> {
            http.authorizeRequests().antMatchers("/carts").permitAll();
            http.authorizeRequests().antMatchers("/carts/**").permitAll();
            http.authorizeRequests().antMatchers("/cart-items").permitAll();
            http.authorizeRequests().antMatchers("/cart-items/**").permitAll();
        };
    }

    @Bean
    SecurityEnhancer tenantAnonymous() {
        return http -> {
            http.authorizeRequests().antMatchers("/resolver/**").permitAll();
            http.authorizeRequests().antMatchers("/url-resolver/**").permitAll();
        };
    }

    @Bean
    @ConditionalOnProperty("tutorial.cors.filter.enabled")
    public CorsFilter corsFilter() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        final CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        // Don't do this in production, use a proper list of allowed origins
        config.setAllowedOrigins(Collections.singletonList(CorsConfiguration.ALL));
        config.setAllowedHeaders(Collections.singletonList(CorsConfiguration.ALL));
        config.setAllowedMethods(Collections.singletonList(CorsConfiguration.ALL));
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    public static void main(String[] args) {
        SpringApplication.run(TutorialGettingStartedApplication.class, args);
    }

}
