package com.broadleafsamples.tutorials;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.broadleafcommerce.common.extension.TypeFactory;
import com.broadleafcommerce.data.tracking.core.Trackable;
import com.broadleafcommerce.data.tracking.core.TrackableBehaviorUtil;
import com.broadleafcommerce.data.tracking.core.context.ContextInfo;
import com.broadleafcommerce.data.tracking.core.mapping.ContextStateBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;
import lombok.extern.apachecommons.CommonsLog;

@SpringBootApplication
public class TutorialGettingStartedApplication {

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

    @Component
    @ConditionalOnProperty(name = "broadleaf.common.policy.validation.enforce",
                            havingValue = "false")
    @CommonsLog
    static class AlwaysMutableContextStateBuilder extends ContextStateBuilder {

        public AlwaysMutableContextStateBuilder(TypeFactory factory,
                                                TrackableBehaviorUtil behaviorUtil, ObjectMapper fieldChangeMapper,
                                                ModelMapper cloneMapper) {
            super(factory, behaviorUtil, fieldChangeMapper, cloneMapper);
            log.info("Loaded AlwaysMutableContextStateBuilder");
        }

        @Override
        protected boolean determineContextStateMutability(Trackable domain,
                                                          ContextInfo contextInfo) {
            return true;
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(TutorialGettingStartedApplication.class, args);
    }

}
