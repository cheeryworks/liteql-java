package org.cheeryworks.liteql.boot.configuration;

import org.cheeryworks.liteql.service.migration.MigrationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class LiteQLApplicationTest {

    @Test
    public void testingMigrationService(@Autowired MigrationService migrationService) {
        migrationService.migrate();
    }

    @Test
    @WithMockUser(username = "admin", password = "admin")
    public void testingQueryServiceEndpoint(@Autowired MockMvc mvc) throws Exception {
        mvc.perform(get("/liteql/schema"))
                .andExpect(status().isOk())
                .andExpect(content().json("[\"liteql\",\"liteql_test\"]"));
    }

    @Test
    @WithMockUser(username = "admin", password = "admin")
    public void testingGraphQLServiceEndpoint(@Autowired MockMvc mvc) throws Exception {
        mvc.perform(get("/graphql/schema"))
                .andExpect(status().isOk());
    }

}
