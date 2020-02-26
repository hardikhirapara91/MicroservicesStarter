package com.broadleafsamples.tutorials;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.broadleafcommerce.resource.security.SecurityEnhancer;

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

    public static void main(String[] args) {
        SpringApplication.run(TutorialGettingStartedApplication.class, args);
    }

}
