package ru.volodin.deal.controller;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.volodin.deal.configuration.props.LoggingProperties;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoggingFilter implements Filter {

    private final LoggingProperties properties;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (!properties.isHttpFilterEnabled()) {
            chain.doFilter(request, response);
            return;
        }

        if (request instanceof HttpServletRequest httpRequest &&
                response instanceof HttpServletResponse httpResponse) {

            String method = httpRequest.getMethod();
            String uri = httpRequest.getRequestURI();
            String query = httpRequest.getQueryString();

            log.info("Incoming request: {} {}{}", method, uri, query != null ? "?" + query : "");

            chain.doFilter(request, response); // continue filter chain

            int status = httpResponse.getStatus();
            log.info("Outgoing response: {} {} -> HTTP {}", method, uri, status);
        } else {
            chain.doFilter(request, response);
        }
    }
}
