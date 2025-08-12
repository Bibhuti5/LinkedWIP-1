package com.devsocial.common.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT Utility class for handling JSON Web Token operations
 * 
 * This utility provides methods for:
 * - Generating JWT tokens
 * - Validating JWT tokens
 * - Extracting claims from tokens
 * - Token expiration management
 * 
 * Security features:
 * - HMAC-SHA256 signing algorithm
 * - Configurable token expiration
 * - Secure key generation
 * - Comprehensive token validation
 * 
 * @author DevSocial Team
 * @version 1.0
 */
@Slf4j
@Component
public class JwtUtil {
    
    /**
     * JWT secret key for signing tokens (should be stored in environment variables)
     */
    @Value("${jwt.secret:devSocialSecretKeyThatIsVeryLongAndSecureForProductionUse}")
    private String jwtSecret;
    
    /**
     * JWT token expiration time in milliseconds (default: 24 hours)
     */
    @Value("${jwt.expiration:86400000}")
    private Long jwtExpiration;
    
    /**
     * Refresh token expiration time in milliseconds (default: 7 days)
     */
    @Value("${jwt.refresh-expiration:604800000}")
    private Long refreshExpiration;
    
    /**
     * Gets the signing key for JWT tokens
     * 
     * @return SecretKey for signing JWT tokens
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }
    
    /**
     * Generates a JWT token for the authenticated user
     * 
     * @param authentication Spring Security authentication object
     * @return JWT token string
     */
    public String generateToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
        return generateTokenFromUsername(userPrincipal.getUsername());
    }
    
    /**
     * Generates a JWT token from username
     * 
     * @param username User's username
     * @return JWT token string
     */
    public String generateTokenFromUsername(String username) {
        return generateTokenFromUsername(username, new HashMap<>());
    }
    
    /**
     * Generates a JWT token from username with additional claims
     * 
     * @param username User's username
     * @param extraClaims Additional claims to include in the token
     * @return JWT token string
     */
    public String generateTokenFromUsername(String username, Map<String, Object> extraClaims) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);
        
        log.debug("Generating JWT token for user: {} with expiration: {}", username, expiryDate);
        
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    
    /**
     * Generates a refresh token for the user
     * 
     * @param username User's username
     * @return Refresh token string
     */
    public String generateRefreshToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshExpiration);
        
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .claim("type", "refresh")
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    
    /**
     * Extracts username from JWT token
     * 
     * @param token JWT token
     * @return Username from token
     */
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }
    
    /**
     * Extracts expiration date from JWT token
     * 
     * @param token JWT token
     * @return Expiration date
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }
    
    /**
     * Extracts issued date from JWT token
     * 
     * @param token JWT token
     * @return Issue date
     */
    public Date getIssuedDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getIssuedAt);
    }
    
    /**
     * Extracts a specific claim from JWT token
     * 
     * @param token JWT token
     * @param claimsResolver Function to extract specific claim
     * @param <T> Type of the claim
     * @return Extracted claim
     */
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }
    
    /**
     * Extracts all claims from JWT token
     * 
     * @param token JWT token
     * @return All claims from the token
     */
    private Claims getAllClaimsFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            log.error("Error parsing JWT token: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Checks if JWT token is expired
     * 
     * @param token JWT token
     * @return true if token is expired
     */
    public Boolean isTokenExpired(String token) {
        try {
            final Date expiration = getExpirationDateFromToken(token);
            return expiration.before(new Date());
        } catch (JwtException e) {
            log.warn("Token validation failed: {}", e.getMessage());
            return true;
        }
    }
    
    /**
     * Validates JWT token against user details
     * 
     * @param token JWT token
     * @param userDetails Spring Security user details
     * @return true if token is valid
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = getUsernameFromToken(token);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (JwtException e) {
            log.error("JWT token validation failed: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Validates JWT token format and signature
     * 
     * @param token JWT token
     * @return true if token is valid
     */
    public Boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        } catch (JwtException e) {
            log.error("JWT token validation error: {}", e.getMessage());
        }
        return false;
    }
    
    /**
     * Gets the remaining time until token expiration
     * 
     * @param token JWT token
     * @return Remaining time in milliseconds
     */
    public Long getTokenRemainingTime(String token) {
        try {
            Date expiration = getExpirationDateFromToken(token);
            return expiration.getTime() - new Date().getTime();
        } catch (JwtException e) {
            log.error("Error getting token remaining time: {}", e.getMessage());
            return 0L;
        }
    }
    
    /**
     * Checks if the token needs to be refreshed (expires within 30 minutes)
     * 
     * @param token JWT token
     * @return true if token should be refreshed
     */
    public Boolean shouldRefreshToken(String token) {
        try {
            Long remainingTime = getTokenRemainingTime(token);
            return remainingTime < 1800000; // 30 minutes
        } catch (Exception e) {
            log.error("Error checking if token should be refreshed: {}", e.getMessage());
            return true;
        }
    }
}