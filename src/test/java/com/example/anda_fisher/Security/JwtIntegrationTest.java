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
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtIntegrationTest {

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
    void testValidTokenIntegration() throws ServletException, IOException {

        String validToken = "validToken";
        request.addHeader("Authorization", "Bearer " + validToken);
        when(jwtService.validateToken(validToken)).thenReturn(true);
        when(jwtService.getUsernameFromToken(validToken)).thenReturn("testUser");
        when(jwtService.extractRole(validToken)).thenReturn("ROLE_USER");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(jwtService, times(1)).validateToken(validToken);
        verify(jwtService, times(1)).getUsernameFromToken(validToken);
        verify(jwtService, times(1)).extractRole(validToken);
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testInvalidTokenIntegration() throws ServletException, IOException {
        // Given
        String invalidToken = "invalidToken";
        request.addHeader("Authorization", "Bearer " + invalidToken);
        when(jwtService.validateToken(invalidToken)).thenReturn(false);

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(jwtService, times(1)).validateToken(invalidToken);
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void testNoTokenIntegration() throws ServletException, IOException {

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(jwtService, never()).validateToken(anyString());
        verify(filterChain, times(1)).doFilter(request, response);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
    }

    @Test
    void testValidTokenAddsToSecurityContext() throws ServletException, IOException {
        // Given
        String validToken = "validToken";
        request.addHeader("Authorization", "Bearer " + validToken);
        when(jwtService.validateToken(validToken)).thenReturn(true);
        when(jwtService.getUsernameFromToken(validToken)).thenReturn("testUser");
        when(jwtService.extractRole(validToken)).thenReturn("ROLE_ADMIN");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("testUser", SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        assertTrue(SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));
    }
}
