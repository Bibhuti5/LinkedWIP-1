package com.devsocial.auth.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

/**
 * OAuth2 Authentication Failure Handler
 * 
 * This handler is invoked when OAuth2 authentication fails.
 * It logs the failure reason and redirects the user to the
 * frontend with appropriate error information.
 * 
 * Common failure scenarios:
 * - User denies authorization
 * - OAuth2 provider is down
 * - Invalid client configuration
 * - Network connectivity issues
 * 
 * @author DevSocial Team
 * @version 1.0
 */
@Slf4j
@Component
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    
    /**
     * Frontend URL for redirecting after failed authentication
     */
    @Value("${app.oauth2.authorizedRedirectUri:http://localhost:3000/auth/oauth2/redirect}")
    private String authorizedRedirectUri;
    
    /**
     * Handles OAuth2 authentication failure
     * 
     * @param request HTTP request
     * @param response HTTP response
     * @param exception Authentication exception that caused the failure
     * @throws IOException if redirect fails
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, 
                                      HttpServletResponse response,
                                      AuthenticationException exception) throws IOException {
        
        String errorMessage = exception.getLocalizedMessage();
        log.error("OAuth2 authentication failed: {}", errorMessage, exception);
        
        // Determine error type based on exception
        String errorType = determineErrorType(exception);
        
        // Build redirect URL with error information
        String redirectUrl = UriComponentsBuilder.fromUriString(authorizedRedirectUri)
                .queryParam("error", errorType)
                .queryParam("message", errorMessage)
                .build().toUriString();
        
        log.info("Redirecting OAuth2 failure to: {}", authorizedRedirectUri);
        
        // Redirect to frontend with error information
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
    
    /**
     * Determines the error type based on the authentication exception
     * 
     * @param exception Authentication exception
     * @return Error type string for frontend handling
     */
    private String determineErrorType(AuthenticationException exception) {
        String exceptionClass = exception.getClass().getSimpleName();
        String message = exception.getMessage() != null ? exception.getMessage().toLowerCase() : "";
        
        // Map different exception types to user-friendly error codes
        if (message.contains("access_denied") || message.contains("user_denied")) {
            return "access_denied";
        } else if (message.contains("invalid_client")) {
            return "invalid_client";
        } else if (message.contains("invalid_request")) {
            return "invalid_request";
        } else if (message.contains("server_error")) {
            return "server_error";
        } else if (message.contains("temporarily_unavailable")) {
            return "temporarily_unavailable";
        } else {
            // Generic OAuth2 error
            return "oauth2_error";
        }
    }
}