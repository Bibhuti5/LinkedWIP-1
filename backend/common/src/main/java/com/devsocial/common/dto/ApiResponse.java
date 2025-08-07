package com.devsocial.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Generic API Response wrapper for consistent response format across all services
 * 
 * This class provides a standardized structure for all API responses, including:
 * - Success/failure status
 * - HTTP status code
 * - Response message
 * - Data payload
 * - Timestamp
 * - Error details (when applicable)
 * 
 * @param <T> The type of data being returned in the response
 * 
 * @author DevSocial Team
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    
    /**
     * Indicates whether the API call was successful
     */
    private boolean success;
    
    /**
     * HTTP status code of the response
     */
    private int statusCode;
    
    /**
     * Human-readable message describing the response
     */
    private String message;
    
    /**
     * The actual data payload of the response
     */
    private T data;
    
    /**
     * Timestamp when the response was generated
     */
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
    
    /**
     * Error details in case of failure (only included when success = false)
     */
    private String error;
    
    /**
     * Creates a successful API response with data
     * 
     * @param data The response data
     * @param message Success message
     * @param <T> Type of the response data
     * @return ApiResponse with success status
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .statusCode(200)
                .message(message)
                .data(data)
                .build();
    }
    
    /**
     * Creates a successful API response with data and custom status code
     * 
     * @param data The response data
     * @param message Success message
     * @param statusCode HTTP status code
     * @param <T> Type of the response data
     * @return ApiResponse with success status
     */
    public static <T> ApiResponse<T> success(T data, String message, int statusCode) {
        return ApiResponse.<T>builder()
                .success(true)
                .statusCode(statusCode)
                .message(message)
                .data(data)
                .build();
    }
    
    /**
     * Creates a successful API response without data
     * 
     * @param message Success message
     * @param <T> Type of the response data
     * @return ApiResponse with success status
     */
    public static <T> ApiResponse<T> success(String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .statusCode(200)
                .message(message)
                .build();
    }
    
    /**
     * Creates an error API response
     * 
     * @param message Error message
     * @param statusCode HTTP status code
     * @param error Detailed error information
     * @param <T> Type of the response data
     * @return ApiResponse with error status
     */
    public static <T> ApiResponse<T> error(String message, int statusCode, String error) {
        return ApiResponse.<T>builder()
                .success(false)
                .statusCode(statusCode)
                .message(message)
                .error(error)
                .build();
    }
    
    /**
     * Creates an error API response with default 500 status code
     * 
     * @param message Error message
     * @param <T> Type of the response data
     * @return ApiResponse with error status
     */
    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .statusCode(500)
                .message(message)
                .error("Internal Server Error")
                .build();
    }
}