package macieserafin.pjwstk.edu.pl.currencymonitorapp.api.exception;

import macieserafin.pjwstk.edu.pl.currencymonitorapp.api.controller.CurrencyController;
import macieserafin.pjwstk.edu.pl.currencymonitorpersistence.currency.CurrencyRepository;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class GlobalExceptionHandlerTest {
    @Test
    void unexpectedErrorDoesNotExposeDatabaseDetails() throws Exception {
        CurrencyRepository repository = mock(CurrencyRepository.class);
        when(repository.findAll()).thenThrow(new IllegalStateException("password=private database details"));

        MockMvcBuilders.standaloneSetup(new CurrencyController(repository))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build().perform(get("/api/currencies"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.error").value("INTERNAL_SERVER_ERROR"))
                .andExpect(jsonPath("$.message").value("An unexpected error occurred. Please try again later."));
    }

    @Test
    void currencyNotFoundKeepsItsExistingResponse() throws Exception {
        CurrencyRepository repository = mock(CurrencyRepository.class);
        when(repository.findAll()).thenThrow(new CurrencyNotFoundException("XYZ"));

        MockMvcBuilders.standaloneSetup(new CurrencyController(repository))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build().perform(get("/api/currencies"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("CURRENCY_NOT_FOUND"));
    }
}
