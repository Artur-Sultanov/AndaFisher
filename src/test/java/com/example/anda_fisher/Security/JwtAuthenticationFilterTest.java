package com.example.anda_fisher.Security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test
    void testDoFilterInternal_AuthLoginPath() throws ServletException, IOException {

        request.setServletPath("/auth/login");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(jwtService, never()).validateToken(anyString());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_NoAuthorizationHeader() throws ServletException, IOException {

        request.setServletPath("/api/data");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(jwtService, never()).validateToken(anyString());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_InvalidToken() throws ServletException, IOException {

        request.setServletPath("/api/data");
        request.addHeader("Authorization", "Bearer invalidToken");
        when(jwtService.validateToken("invalidToken")).thenReturn(false);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(jwtService, times(1)).validateToken("invalidToken");
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_ValidToken() throws ServletException, IOException {

        request.setServletPath("/api/data");
        request.addHeader("Authorization", "Bearer validToken");
        when(jwtService.validateToken("validToken")).thenReturn(true);
        when(jwtService.getUsernameFromToken("validToken")).thenReturn("testUser");
        when(jwtService.extractRole("validToken")).thenReturn("ROLE_USER");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(jwtService, times(1)).validateToken("validToken");
        verify(jwtService, times(1)).getUsernameFromToken("validToken");
        verify(jwtService, times(1)).extractRole("validToken");
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_NoTokenButContinue() throws ServletException, IOException {

        request.setServletPath("/api/data");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(jwtService, never()).validateToken(anyString());
        verify(filterChain, times(1)).doFilter(request, response);
    }
}
