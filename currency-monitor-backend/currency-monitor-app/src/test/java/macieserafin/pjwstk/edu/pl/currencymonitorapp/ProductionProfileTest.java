package macieserafin.pjwstk.edu.pl.currencymonitorapp;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Checks Flyway + Hibernate validation after dependency updates without Docker.
// CI's production smoke job additionally checks the real PostgreSQL containers.
@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:production-profile;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
})
@ActiveProfiles("prod")
@AutoConfigureMockMvc
class ProductionProfileTest {
    @Autowired private Flyway flyway;
    @Autowired private MockMvc mockMvc;
    @MockBean private NbpScheduler scheduler;

    @Test
    void migratesAndStartsApiWithProductionProfile() throws Exception {
        assertThat(flyway.info().current().getVersion().getVersion()).isEqualTo("1");
        mockMvc.perform(get("/api/currencies"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }
}
