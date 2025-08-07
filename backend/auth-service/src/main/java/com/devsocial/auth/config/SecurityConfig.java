package com.devsocial.auth.config;

import com.devsocial.auth.repository.UserRepository;
import com.devsocial.common.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

/**
 * Spring Security configuration for the authentication service
 * 
 * This configuration sets up:
 * - JWT-based authentication
 * - OAuth2 integration (GitHub, Google, etc.)
 * - Password encoding with BCrypt
 * - CORS configuration for frontend integration
 * - Security filter chain with proper endpoint access rules
 * 
 * Security features:
 * - Stateless session management
 * - Custom JWT authentication filter
 * - OAuth2 success and failure handlers
 * - Secure password encoding
 * - CSRF protection disabled for REST APIs
 * 
 * @author DevSocial Team
 * @version 1.0
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    
    /**
     * Password encoder bean using BCrypt
     * BCrypt is a strong, adaptive hashing function designed for password hashing
     * 
     * @return PasswordEncoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12); // Strength of 12 for good security vs performance balance
    }
    
    /**
     * UserDetailsService implementation for loading user details during authentication
     * 
     * @return UserDetailsService that loads users from the database
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
    
    /**
     * Authentication provider using DAO (Database Access Object) pattern
     * 
     * @return DaoAuthenticationProvider configured with UserDetailsService and PasswordEncoder
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
    
    /**
     * Authentication manager bean for handling authentication requests
     * 
     * @param config Authentication configuration
     * @return AuthenticationManager instance
     * @throws Exception if configuration fails
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    
    /**
     * JWT authentication filter for processing JWT tokens in requests
     * 
     * @return JwtAuthenticationFilter instance
     */
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtUtil, userDetailsService());
    }
    
    /**
     * OAuth2 user service for handling OAuth2 user information
     * 
     * @return OAuth2UserService instance
     */
    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService() {
        return new DefaultOAuth2UserService();
    }
    
    /**
     * OAuth2 success handler for handling successful OAuth2 authentication
     * 
     * @return OAuth2AuthenticationSuccessHandler instance
     */
    @Bean
    public OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler() {
        return new OAuth2AuthenticationSuccessHandler(jwtUtil, userRepository);
    }
    
    /**
     * OAuth2 failure handler for handling failed OAuth2 authentication
     * 
     * @return OAuth2AuthenticationFailureHandler instance
     */
    @Bean
    public OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler() {
        return new OAuth2AuthenticationFailureHandler();
    }
    
    /**
     * CORS configuration source for handling cross-origin requests
     * 
     * @return CorsConfigurationSource with appropriate CORS settings
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Allow specific origins (configure based on your frontend URLs)
        configuration.setAllowedOriginPatterns(Arrays.asList("*")); // In production, specify exact origins
        
        // Allow specific HTTP methods
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        
        // Allow specific headers
        configuration.setAllowedHeaders(Arrays.asList("*"));
        
        // Allow credentials (cookies, authorization headers, etc.)
        configuration.setAllowCredentials(true);
        
        // Cache preflight response for 1 hour
        configuration.setMaxAge(3600L);
        
        // Apply CORS configuration to all endpoints
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
    
    /**
     * Main security filter chain configuration
     * 
     * This method configures the security filter chain with:
     * - CORS and CSRF settings
     * - Session management
     * - Request authorization rules
     * - OAuth2 login configuration
     * - Custom authentication filters
     * 
     * @param http HttpSecurity configuration object
     * @return SecurityFilterChain configured for the application
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Enable CORS and disable CSRF for REST APIs
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(AbstractHttpConfigurer::disable)
            
            // Configure session management to be stateless
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // Configure request authorization
            .authorizeHttpRequests(authz -> authz
                // Public endpoints - no authentication required
                .requestMatchers(
                    "/api/auth/login",
                    "/api/auth/register",
                    "/api/auth/refresh",
                    "/api/auth/verify-email",
                    "/api/auth/forgot-password",
                    "/api/auth/reset-password",
                    "/api/auth/check-username",
                    "/api/auth/check-email",
                    "/oauth2/**",
                    "/login/oauth2/**",
                    "/actuator/health",
                    "/error",
                    "/favicon.ico"
                ).permitAll()
                
                // Admin endpoints - require admin role
                .requestMatchers("/api/auth/admin/**").hasRole("ADMIN")
                
                // All other requests require authentication
                .anyRequest().authenticated()
            )
            
            // Configure OAuth2 login
            .oauth2Login(oauth2 -> oauth2
                .authorizationEndpoint(authorization -> authorization
                    .baseUri("/oauth2/authorize")
                )
                .redirectionEndpoint(redirection -> redirection
                    .baseUri("/oauth2/callback/*")
                )
                .userInfoEndpoint(userInfo -> userInfo
                    .userService(oAuth2UserService())
                )
                .successHandler(oAuth2AuthenticationSuccessHandler())
                .failureHandler(oAuth2AuthenticationFailureHandler())
            )
            
            // Set authentication provider
            .authenticationProvider(authenticationProvider())
            
            // Add JWT filter before username/password authentication filter
            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}