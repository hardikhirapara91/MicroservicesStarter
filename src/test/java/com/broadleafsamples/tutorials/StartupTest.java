package com.broadleafsamples.tutorials;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.internal.util.collections.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import com.broadleafcommerce.common.jpa.schema.SchemaCompatibiltyUtility;
import com.broadleafcommerce.data.tracking.core.context.ContextRequest;
import com.broadleafcommerce.oauth2.resource.security.test.MockMvcOAuth2AuthenticationUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.extern.apachecommons.CommonsLog;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Elbert Bautista (elbertbautista)
 */
@AutoConfigureMockMvc
@ContextConfiguration(initializers = StartupTest.Initializer.class)
@CommonsLog
@SpringBootTest(properties = {
        "broadleaf.solr.startup-validation=false"
})
public class StartupTest {

    public static PostgreSQLContainer container =
            new PostgreSQLContainer("postgres:11.2");

    public static final List<String> schemas = Arrays.asList("adminnavigation", "adminuser",
            "asset", "campaign", "cart", "cartoperation", "catalog", "customer", "dataimport",
            "indexer", "menu", "metadata", "offer", "personalization", "pricing", "sandbox",
            "scheduledjob", "search", "tenant");

    public static final List<String> schemaNames = Arrays.asList("adminnavigation", "adminuser",
            "asset", "campaign", "cart", "cartoperation", "catalog", "customer", "import",
            "indexer", "menu", "metadata", "offer", "personalization", "pricing", "sandbox",
            "scheduledjob", "search", "tenant");

    @BeforeAll
    public static void startup() {
        container.start();
        SchemaCompatibiltyUtility.setup(container.getJdbcUrl(),
                container.getUsername(), container.getPassword(), container.getDatabaseName());
    }

    @Configuration
    @EnableAutoConfiguration
    static class TestConfig {
        @Bean
        public SolrClient solrClient() {
            List<String> zkHosts = Collections.singletonList("localhost:2181");
            Optional<String> zkChroot = Optional.of("/solr");
            return new CloudSolrClient.Builder(zkHosts, zkChroot).build();
        }
    }

    @Autowired
    ApplicationContext appctx;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    MockMvcOAuth2AuthenticationUtil mockMvcUtil;

    @Test
    public void applicationContextLoads() {
        assertNotNull(appctx);
    }

    @Test
    public void readProducts() throws Exception {
        ContextRequest request =
                new ContextRequest().withCatalogId("3")
                        .withTenantId("5DF1363059675161A85F576D");
        mockMvc.perform(
                get("/products")
                        .with(mockMvcUtil.withAuthoritiesAndTenant(
                                Sets.newSet("READ_PRODUCT"),
                                null, false, null))
                        .header("X-Context-Request", toJson(request)))
                .andExpect(status().isOk());
    }

    private String toJson(ContextRequest ctx) throws JsonProcessingException {
        if (ctx == null) {
            return null;
        }
        return new ObjectMapper().writeValueAsString(ctx);
    }

    public static class Initializer
            implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(
                @NonNull ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(getDataRoutingSchemaProperties())
                    .applyTo(configurableApplicationContext.getEnvironment());

        }

        private List<String> getDataRoutingSchemaProperties() {
            List<String> props = new ArrayList<>();
            int j = 0;
            for (String schema : schemas) {
                props.add("broadleaf." + schema + ".datasource.url="
                        + System.getProperty("compat_jdbcurl") + "&currentSchema="
                        + schemaNames.get(j) + "");
                props.add("broadleaf." + schema + ".datasource.username="
                        + System.getProperty("compat_username"));
                props.add("broadleaf." + schema + ".datasource.password="
                        + System.getProperty("compat_password"));
                j++;
            }

            log.info(Arrays.toString(props.toArray()));
            return props;
        }

    }

}

