package com.devsocial.auth.config;

import com.devsocial.common.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT Authentication Filter for processing JWT tokens in HTTP requests
 * 
 * This filter intercepts all HTTP requests and:
 * - Extracts JWT tokens from the Authorization header
 * - Validates the token format and signature
 * - Loads user details if token is valid
 * - Sets up Spring Security authentication context
 * 
 * The filter runs once per request and is positioned before the
 * UsernamePasswordAuthenticationFilter in the security filter chain.
 * 
 * Token format expected: "Bearer <jwt-token>"
 * 
 * @author DevSocial Team
 * @version 1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    
    /**
     * Authorization header name
     */
    private static final String AUTHORIZATION_HEADER = "Authorization";
    
    /**
     * Bearer token prefix
     */
    private static final String BEARER_PREFIX = "Bearer ";
    
    /**
     * Main filter method that processes each HTTP request
     * 
     * @param request HTTP request
     * @param response HTTP response
     * @param filterChain Filter chain for continuing request processing
     * @throws ServletException if servlet processing fails
     * @throws IOException if I/O operation fails
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                  @NonNull HttpServletResponse response,
                                  @NonNull FilterChain filterChain) throws ServletException, IOException {
        
        try {
            // Extract JWT token from request
            String jwt = getJwtFromRequest(request);
            
            // Process token if present and no authentication exists
            if (StringUtils.hasText(jwt) && SecurityContextHolder.getContext().getAuthentication() == null) {
                processJwtToken(jwt, request);
            }
        } catch (Exception e) {
            log.error("Cannot set user authentication in security context", e);
            // Don't throw exception, just log and continue
            // This allows the request to proceed without authentication
            // and let the security configuration handle unauthorized access
        }
        
        // Continue with the filter chain
        filterChain.doFilter(request, response);
    }
    
    /**
     * Extracts JWT token from the Authorization header
     * 
     * @param request HTTP request
     * @return JWT token string or null if not found
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader(AUTHORIZATION_HEADER);
        
        if (StringUtils.hasText(authHeader) && authHeader.startsWith(BEARER_PREFIX)) {
            String token = authHeader.substring(BEARER_PREFIX.length());
            log.debug("Extracted JWT token from Authorization header");
            return token;
        }
        
        return null;
    }
    
    /**
     * Processes the JWT token and sets up authentication context
     * 
     * @param jwt JWT token string
     * @param request HTTP request for additional context
     */
    private void processJwtToken(String jwt, HttpServletRequest request) {
        try {
            // Validate token format and signature
            if (!jwtUtil.validateToken(jwt)) {
                log.debug("Invalid JWT token provided");
                return;
            }
            
            // Extract username from token
            String username = jwtUtil.getUsernameFromToken(jwt);
            
            if (username == null) {
                log.debug("Username not found in JWT token");
                return;
            }
            
            // Load user details
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            
            // Validate token against user details
            if (jwtUtil.validateToken(jwt, userDetails)) {
                // Create authentication token
                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(
                        userDetails, 
                        null, 
                        userDetails.getAuthorities()
                    );
                
                // Set additional details from request
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                // Set authentication in security context
                SecurityContextHolder.getContext().setAuthentication(authentication);
                
                log.debug("Successfully authenticated user: {}", username);
            } else {
                log.debug("JWT token validation failed for user: {}", username);
            }
            
        } catch (Exception e) {
            log.error("Error processing JWT token", e);
            // Clear any partial authentication that might have been set
            SecurityContextHolder.clearContext();
        }
    }
    
    /**
     * Determines if the filter should be applied to the request
     * 
     * Override this method to skip certain paths if needed.
     * Currently applies to all requests.
     * 
     * @param request HTTP request
     * @return true if filter should be applied
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        
        // Skip JWT processing for public endpoints that don't need authentication
        return path.equals("/actuator/health") || 
               path.equals("/error") || 
               path.equals("/favicon.ico") ||
               path.startsWith("/oauth2/") ||
               path.startsWith("/login/oauth2/");
    }
}