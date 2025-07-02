package ru.volodin.deal.controller;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
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

        if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
            ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper((HttpServletRequest) request);
            ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper((HttpServletResponse) response);

            String method = wrappedRequest.getMethod();
            String uri = wrappedRequest.getRequestURI();
            String query = wrappedRequest.getQueryString();

            log.info("Incoming request: {} {}{}", method, uri, query != null ? "?" + query : "");

            chain.doFilter(wrappedRequest, wrappedResponse);

            String requestBody = new String(wrappedRequest.getContentAsByteArray(), wrappedRequest.getCharacterEncoding());
            String responseBody = new String(wrappedResponse.getContentAsByteArray(), wrappedResponse.getCharacterEncoding());

            int status = wrappedResponse.getStatus();
            log.debug("Request body: {}", requestBody);
            log.info("Outgoing response: {} {} -> HTTP {}", method, uri, status);
            log.debug("Response body: {}", responseBody);

            wrappedResponse.copyBodyToResponse();
        } else {
            chain.doFilter(request, response);
        }
    }
}
