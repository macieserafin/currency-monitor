package macieserafin.pjwstk.edu.pl.currencymonitorapp.api.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class ApiLoggingFilter extends OncePerRequestFilter {

    private static final Logger log =
            LoggerFactory.getLogger(ApiLoggingFilter.class);

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        long start = System.currentTimeMillis();

        String method = request.getMethod();
        String uri = request.getRequestURI();
        String query = request.getQueryString();

        log.info("➡️ API REQUEST: {} {}{}",
                method,
                uri,
                query != null ? "?" + query : ""
        );

        try {
            filterChain.doFilter(request, response);
        } finally {
            long time = System.currentTimeMillis() - start;
            int status = response.getStatus();

            log.info("⬅️ API RESPONSE: {} {} -> {} ({} ms)",
                    method,
                    uri,
                    status,
                    time
            );
        }
    }
}
