package com.broadleafsamples.tutorials.autoconfigure;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;

import com.broadleafcommerce.common.jpa.schema.SchemaCompatibiltyUtility;

import java.util.Arrays;

public class DemoSchemaInitializerEnvironmentPostProcessor implements EnvironmentPostProcessor {

    private static boolean finished;

    @SuppressWarnings("squid:S106")
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment,
                                       SpringApplication application) {
        if (!finished) {
            String schemas =
                    environment.getProperty("broadleaf.database.demo.oracle.initialize.schemas");
            String sysPassword =
                    environment
                            .getProperty("broadleaf.database.demo.oracle.initialize.sys-password");
            String jdbcUrl =
                    environment.getProperty("broadleaf.database.demo.oracle.initialize.jdbc-url");

            if (StringUtils.isNotEmpty(schemas) && StringUtils.isNotEmpty(sysPassword)
                    && StringUtils.isNotEmpty(jdbcUrl)) {
                String[] schemaSplit = schemas.split(",");
                Arrays.stream(schemaSplit).forEach(item -> {
                    item = item.trim();
                    SchemaCompatibiltyUtility.initializeOracleUser(
                            sysPassword,
                            jdbcUrl, item, item);
                    System.out.println(String.format("Initialized Oracle schema for %s", item));
                });
                finished = true;
            }
        }
    }
}
