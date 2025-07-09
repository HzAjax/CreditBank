package ru.volodin.deal.controller;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.example.mylib.config.LoggingProperties;
import org.example.mylib.filter.LoggingFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LoggingFilterTest {

    private LoggingFilter filter;
    private LoggingProperties loggingProperties;

    @BeforeEach
    void setUp() {
        loggingProperties = mock(LoggingProperties.class);
        filter = new LoggingFilter(loggingProperties);
    }

    @Test
    void testFilterDisabled() throws IOException, ServletException {
        when(loggingProperties.isHttpFilterEnabled()).thenReturn(false);

        ServletRequest request = mock(ServletRequest.class);
        ServletResponse response = mock(ServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
    }

    @Test
    void testFilterWithNonHttpRequest() throws IOException, ServletException {
        when(loggingProperties.isHttpFilterEnabled()).thenReturn(true);

        ServletRequest request = mock(ServletRequest.class);
        ServletResponse response = mock(ServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
    }

    @Test
    void testHttpRequestLogging() throws IOException, ServletException {
        when(loggingProperties.isHttpFilterEnabled()).thenReturn(true);

        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        servletRequest.setMethod("POST");
        servletRequest.setRequestURI("/test");
        servletRequest.setQueryString("id=123");
        servletRequest.setContent("request-body".getBytes());

        MockHttpServletResponse servletResponse = new MockHttpServletResponse();
        servletResponse.setCharacterEncoding("UTF-8");

        FilterChain chain = (request, response) -> {
            ContentCachingRequestWrapper reqWrapper = (ContentCachingRequestWrapper) request;
            ContentCachingResponseWrapper resWrapper = (ContentCachingResponseWrapper) response;

            resWrapper.getWriter().write("response-body");
            resWrapper.setStatus(200);
        };

        filter.doFilter(servletRequest, servletResponse, chain);

        String responseContent = servletResponse.getContentAsString();
        assertEquals("response-body", responseContent);
    }
}