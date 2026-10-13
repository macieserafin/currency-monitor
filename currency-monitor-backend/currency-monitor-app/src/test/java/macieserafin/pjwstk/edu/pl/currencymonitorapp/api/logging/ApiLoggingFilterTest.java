package macieserafin.pjwstk.edu.pl.currencymonitorapp.api.logging;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;

class ApiLoggingFilterTest {
    @Test
    void logsRequestAndStatusWithoutQueryParameters() throws Exception {
        Logger logger = (Logger) LoggerFactory.getLogger(ApiLoggingFilter.class);
        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        logger.addAppender(appender);
        try {
            MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/currencies");
            request.setQueryString("token=private-token&email=private@example.com");
            new ApiLoggingFilter().doFilter(request, new MockHttpServletResponse(),
                    (req, res) -> ((MockHttpServletResponse) res).setStatus(200));

            assertThat(appender.list).hasSize(2);
            assertThat(appender.list).extracting(ILoggingEvent::getFormattedMessage)
                    .allSatisfy(message -> assertThat(message)
                            .contains("/api/currencies")
                            .doesNotContain("token", "private", "email", "?"));
            assertThat(appender.list.get(1).getFormattedMessage()).contains("200");
        } finally {
            logger.detachAppender(appender);
            appender.stop();
        }
    }
}
