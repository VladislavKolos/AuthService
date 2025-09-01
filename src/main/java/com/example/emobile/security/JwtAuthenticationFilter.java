package com.example.emobile.security;

import com.example.emobile.exception.JwtAuthenticationException;
import com.example.emobile.service.JwtService;
import com.example.emobile.util.AuthenticationServiceConstantUtil;
import io.jsonwebtoken.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import static com.example.emobile.exception.JwtAuthenticationException.ErrorType;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) {
        try {
            String accessToken = extractToken(request);

            if (accessToken == null || !jwtService.isAccessTokenValid(accessToken)) {
                filterChain.doFilter(request, response);
                return;
            }

            String username = jwtService.extractUsername(accessToken, jwtService.getAccessSecretKey());
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                var userDetails = userDetailsService.loadUserByUsername(username);
                setAuthentication(userDetails, request);
            }
            filterChain.doFilter(request, response);
        } catch (IOException ex) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throw new JwtAuthenticationException(ErrorType.JWT_AUTH_IO_ERROR, ex);
        } catch (Exception ex) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throw new JwtAuthenticationException(ErrorType.JWT_AUTH_UNEXPECTED_ERROR, ex);
        }
    }

    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader(AuthenticationServiceConstantUtil.AUTH_HEADER);
        return (authHeader != null && authHeader.startsWith(AuthenticationServiceConstantUtil.BEARER_PREFIX))
                ? authHeader.substring(AuthenticationServiceConstantUtil.BEARER_PREFIX_LENGTH)
                : null;
    }

    private void setAuthentication(UserDetails userDetails, HttpServletRequest request) {
        var authToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authToken);
    }
}