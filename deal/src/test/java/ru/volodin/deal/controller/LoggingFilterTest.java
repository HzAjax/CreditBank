package ru.volodin.deal.controller;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import ru.volodin.deal.configuration.props.LoggingProperties;

import java.io.IOException;

import static org.mockito.Mockito.*;

class LoggingFilterTest {

    @Test
    void shouldSkipLoggingWhenDisabled() throws Exception {
        LoggingProperties props = mock(LoggingProperties.class);
        when(props.isHttpFilterEnabled()).thenReturn(false);

        LoggingFilter filter = new LoggingFilter(props);

        ServletRequest request = mock(ServletRequest.class);
        ServletResponse response = mock(ServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(request, response, chain);

        verify(chain, times(1)).doFilter(request, response);
        verifyNoMoreInteractions(chain);
    }

    @Test
    void testDoFilter_logsRequestAndResponse() throws IOException, ServletException {
        LoggingProperties props = mock(LoggingProperties.class);
        when(props.isHttpFilterEnabled()).thenReturn(true);

        LoggingFilter filter = new LoggingFilter(props);

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/deal");
        when(request.getQueryString()).thenReturn("statement");
        when(response.getStatus()).thenReturn(200);

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        verify(request).getMethod();
        verify(request).getRequestURI();
        verify(response).getStatus();
    }

    @Test
    void testDoFilter_nonHttpRequest_stillPassesThrough() throws IOException, ServletException {
        LoggingProperties props = mock(LoggingProperties.class);
        when(props.isHttpFilterEnabled()).thenReturn(true);

        LoggingFilter filter = new LoggingFilter(props);

        ServletRequest req = mock(ServletRequest.class);
        ServletResponse res = mock(ServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(req, res, chain);

        verify(chain).doFilter(req, res);
    }
}
