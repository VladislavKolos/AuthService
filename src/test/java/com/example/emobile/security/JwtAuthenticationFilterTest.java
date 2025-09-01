package com.example.emobile.security;

import com.example.emobile.exception.JwtAuthenticationException;
import com.example.emobile.service.JwtService;
import com.example.emobile.util.AuthenticationServiceConstantUtil;
import com.example.emobile.util.TestDataBuilderUtil;
import io.jsonwebtoken.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private final String validToken = "valid.jwt.token";
    private final String username = "test@example.com";
    private UserDetails testUserDetails;


    @BeforeEach
    public void clearContext() {
        testUserDetails = TestDataBuilderUtil.buildUserDetails(username);
        SecurityContextHolder.clearContext();
    }

    @Test
    public void shouldSetAuthenticationWhenTokenIsValid() throws Exception {
        givenAuthorizationHeaderWithToken();
        given(jwtService.isAccessTokenValid(validToken)).willReturn(true);
        given(jwtService.getAccessSecretKey()).willReturn("test-secret-key");
        given(jwtService.extractUsername(validToken, "test-secret-key")).willReturn(username);
        given(userDetailsService.loadUserByUsername(username)).willReturn(testUserDetails);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNotNull();
        assertThat(authentication.getPrincipal()).isEqualTo(testUserDetails);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    public void shouldNotSetAuthenticationWhenTokenIsInvalid() throws Exception {
        givenAuthorizationHeaderWithToken();
        given(jwtService.isAccessTokenValid(validToken)).willReturn(false);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
    }

    @Test
    public void shouldNotSetAuthenticationWhenHeaderMissing() throws Exception {
        given(request.getHeader(AuthenticationServiceConstantUtil.AUTH_HEADER)).willReturn(null);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
    }

    @Test
    public void shouldHandleIOException() {
        givenAuthorizationHeaderWithToken();
        given(jwtService.isAccessTokenValid(validToken)).willThrow(new IOException("test IO"));

        assertThatThrownBy(() -> jwtAuthenticationFilter.doFilterInternal(request, response, filterChain))
                .isInstanceOf(JwtAuthenticationException.class)
                .hasMessageContaining("JWT_AUTH_IO_ERROR");
    }

    @Test
    public void shouldHandleUnexpectedException() {
        givenAuthorizationHeaderWithToken();
        given(jwtService.isAccessTokenValid(validToken)).willThrow(new RuntimeException("Boom"));

        assertThatThrownBy(() -> jwtAuthenticationFilter.doFilterInternal(request, response, filterChain))
                .isInstanceOf(JwtAuthenticationException.class)
                .hasMessageContaining("JWT_AUTH_UNEXPECTED_ERROR");
    }

    private void givenAuthorizationHeaderWithToken() {
        given(request.getHeader(AuthenticationServiceConstantUtil.AUTH_HEADER))
                .willReturn(AuthenticationServiceConstantUtil.BEARER_PREFIX + validToken);
    }
}