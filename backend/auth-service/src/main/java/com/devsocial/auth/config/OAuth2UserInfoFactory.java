package com.devsocial.auth.config;

import java.util.Map;

/**
 * Factory class for creating OAuth2UserInfo implementations
 * 
 * This factory creates appropriate OAuth2UserInfo implementations
 * based on the OAuth2 provider registration ID. It supports
 * multiple providers and can be easily extended for new providers.
 * 
 * Supported providers:
 * - GitHub
 * - Google (can be extended)
 * 
 * @author DevSocial Team
 * @version 1.0
 */
public class OAuth2UserInfoFactory {
    
    /**
     * Creates OAuth2UserInfo implementation based on provider
     * 
     * @param registrationId OAuth2 provider registration ID
     * @param attributes User attributes from OAuth2 provider
     * @return OAuth2UserInfo implementation for the provider
     * @throws RuntimeException if provider is not supported
     */
    public static OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
        switch (registrationId.toLowerCase()) {
            case "github":
                return new GitHubOAuth2UserInfo(attributes);
            case "google":
                return new GoogleOAuth2UserInfo(attributes);
            default:
                throw new RuntimeException("OAuth2 provider not supported: " + registrationId);
        }
    }
    
    /**
     * GitHub OAuth2 user information implementation
     */
    public static class GitHubOAuth2UserInfo implements OAuth2UserInfo {
        private final Map<String, Object> attributes;
        
        public GitHubOAuth2UserInfo(Map<String, Object> attributes) {
            this.attributes = attributes;
        }
        
        @Override
        public String getId() {
            return String.valueOf(attributes.get("id"));
        }
        
        @Override
        public String getName() {
            return (String) attributes.get("name");
        }
        
        @Override
        public String getEmail() {
            return (String) attributes.get("email");
        }
        
        @Override
        public String getFirstName() {
            String name = getName();
            if (name != null && !name.trim().isEmpty()) {
                String[] parts = name.trim().split("\\s+");
                return parts[0];
            }
            return null;
        }
        
        @Override
        public String getLastName() {
            String name = getName();
            if (name != null && !name.trim().isEmpty()) {
                String[] parts = name.trim().split("\\s+");
                if (parts.length > 1) {
                    return String.join(" ", java.util.Arrays.copyOfRange(parts, 1, parts.length));
                }
            }
            return null;
        }
        
        @Override
        public String getImageUrl() {
            return (String) attributes.get("avatar_url");
        }
    }
    
    /**
     * Google OAuth2 user information implementation
     */
    public static class GoogleOAuth2UserInfo implements OAuth2UserInfo {
        private final Map<String, Object> attributes;
        
        public GoogleOAuth2UserInfo(Map<String, Object> attributes) {
            this.attributes = attributes;
        }
        
        @Override
        public String getId() {
            return (String) attributes.get("sub");
        }
        
        @Override
        public String getName() {
            return (String) attributes.get("name");
        }
        
        @Override
        public String getEmail() {
            return (String) attributes.get("email");
        }
        
        @Override
        public String getFirstName() {
            return (String) attributes.get("given_name");
        }
        
        @Override
        public String getLastName() {
            return (String) attributes.get("family_name");
        }
        
        @Override
        public String getImageUrl() {
            return (String) attributes.get("picture");
        }
    }
}