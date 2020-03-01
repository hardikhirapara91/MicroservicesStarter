package com.broadleafsamples.tutorials;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.junit.jupiter.api.Test;
import org.mockito.internal.util.collections.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.web.servlet.MockMvc;
import com.broadleafcommerce.common.jpa.autoconfigure.AutoConfigureTestDb;
import com.broadleafcommerce.data.tracking.core.context.ContextRequest;
import com.broadleafcommerce.oauth2.resource.security.test.MockMvcOAuth2AuthenticationUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Elbert Bautista (elbertbautista)
 */
@AutoConfigureTestDb
@AutoConfigureMockMvc
@SpringBootTest(properties = {
        "broadleaf.solr.startup-validation=false",
        "broadleaf.database.provider=jpa"
})
public class StartupTest {

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

}
