package com.devsocial.auth.service;

import com.devsocial.auth.model.User;
import com.devsocial.auth.repository.UserRepository;
import com.devsocial.common.dto.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service for user management operations
 * 
 * This service provides user-related business logic including:
 * - User profile management
 * - DTO conversions
 * - User lookup operations
 * - Account status management
 * 
 * @author DevSocial Team
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    
    private final UserRepository userRepository;
    
    /**
     * Converts User entity to UserDto
     * 
     * This method safely converts a User entity to a UserDto, excluding sensitive
     * information like passwords and internal tokens.
     * 
     * @param user User entity to convert
     * @return UserDto with public user information
     */
    public UserDto convertToDto(User user) {
        if (user == null) {
            return null;
        }
        
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .isPublic(true) // Default to public for now
                .isActive(user.isEnabled())
                .isEmailVerified(user.getEmailVerified())
                .followersCount(0) // Will be populated by user-service
                .followingCount(0) // Will be populated by user-service
                .postsCount(0) // Will be populated by post-service
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .lastActiveAt(user.getLastLoginAt())
                .build();
    }
    
    /**
     * Finds a user by username and converts to DTO
     * 
     * @param username Username to search for
     * @return Optional containing UserDto if found
     */
    public Optional<UserDto> findByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(this::convertToDto);
    }
    
    /**
     * Finds a user by email and converts to DTO
     * 
     * @param email Email to search for
     * @return Optional containing UserDto if found
     */
    public Optional<UserDto> findByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(this::convertToDto);
    }
    
    /**
     * Finds a user by ID and converts to DTO
     * 
     * @param id User ID to search for
     * @return Optional containing UserDto if found
     */
    public Optional<UserDto> findById(Long id) {
        return userRepository.findById(id)
                .map(this::convertToDto);
    }
    
    /**
     * Checks if a username is available
     * 
     * @param username Username to check
     * @return true if username is available, false if taken
     */
    public boolean isUsernameAvailable(String username) {
        return !userRepository.existsByUsername(username);
    }
    
    /**
     * Checks if an email is available
     * 
     * @param email Email to check
     * @return true if email is available, false if taken
     */
    public boolean isEmailAvailable(String email) {
        return !userRepository.existsByEmail(email);
    }
}