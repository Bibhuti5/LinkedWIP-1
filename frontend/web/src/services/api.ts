/**
 * API Service Layer
 * 
 * Centralized HTTP client configuration with interceptors for:
 * - Authentication token management
 * - Request/response logging
 * - Error handling
 * - Automatic token refresh
 */

import axios, { AxiosInstance, AxiosRequestConfig, AxiosResponse } from 'axios';
import { toast } from 'react-hot-toast';
import { ApiResponse, ApiError } from '@/types/auth';

/**
 * API Configuration
 */
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';
const REQUEST_TIMEOUT = 30000; // 30 seconds

/**
 * Create axios instance with default configuration
 */
const apiClient: AxiosInstance = axios.create({
  baseURL: API_BASE_URL,
  timeout: REQUEST_TIMEOUT,
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json',
  },
});

/**
 * Token management utilities
 */
export const tokenManager = {
  getAccessToken: (): string | null => {
    return localStorage.getItem('accessToken');
  },

  getRefreshToken: (): string | null => {
    return localStorage.getItem('refreshToken');
  },

  setTokens: (accessToken: string, refreshToken: string): void => {
    localStorage.setItem('accessToken', accessToken);
    localStorage.setItem('refreshToken', refreshToken);
  },

  clearTokens: (): void => {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('user');
  },

  isTokenExpired: (token: string): boolean => {
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return payload.exp * 1000 < Date.now();
    } catch {
      return true;
    }
  }
};

/**
 * Request interceptor to add authentication token
 */
apiClient.interceptors.request.use(
  (config) => {
    const token = tokenManager.getAccessToken();
    
    if (token && !tokenManager.isTokenExpired(token)) {
      config.headers.Authorization = `Bearer ${token}`;
    }

    // Log request in development
    if (__DEV__) {
      console.log(`🚀 API Request: ${config.method?.toUpperCase()} ${config.url}`, {
        headers: config.headers,
        data: config.data,
      });
    }

    return config;
  },
  (error) => {
    console.error('❌ Request interceptor error:', error);
    return Promise.reject(error);
  }
);

/**
 * Response interceptor for error handling and token refresh
 */
apiClient.interceptors.response.use(
  (response: AxiosResponse<ApiResponse<any>>) => {
    // Log successful responses in development
    if (__DEV__) {
      console.log(`✅ API Response: ${response.config.method?.toUpperCase()} ${response.config.url}`, {
        status: response.status,
        data: response.data,
      });
    }

    return response;
  },
  async (error) => {
    const originalRequest = error.config;

    // Log errors in development
    if (__DEV__) {
      console.error(`❌ API Error: ${originalRequest?.method?.toUpperCase()} ${originalRequest?.url}`, {
        status: error.response?.status,
        data: error.response?.data,
        message: error.message,
      });
    }

    // Handle 401 Unauthorized - Token expired or invalid
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

      try {
        const refreshToken = tokenManager.getRefreshToken();
        
        if (refreshToken) {
          // Attempt to refresh the token
          const response = await axios.post(`${API_BASE_URL}/api/auth/refresh`, {
            refreshToken
          });

          const { accessToken, refreshToken: newRefreshToken } = response.data.data;
          
          // Update stored tokens
          tokenManager.setTokens(accessToken, newRefreshToken);
          
          // Retry the original request with new token
          originalRequest.headers.Authorization = `Bearer ${accessToken}`;
          return apiClient(originalRequest);
        }
      } catch (refreshError) {
        // Refresh failed, redirect to login
        tokenManager.clearTokens();
        window.location.href = '/login';
        return Promise.reject(refreshError);
      }
    }

    // Handle different error status codes
    const apiError: ApiError = error.response?.data || {
      message: error.message || 'An unexpected error occurred',
      code: 'NETWORK_ERROR',
      timestamp: new Date().toISOString(),
      path: originalRequest?.url || '',
    };

    // Show user-friendly error messages
    switch (error.response?.status) {
      case 400:
        toast.error(apiError.message || 'Invalid request. Please check your input.');
        break;
      case 401:
        toast.error('Authentication required. Please log in.');
        break;
      case 403:
        toast.error('Access denied. You don\'t have permission for this action.');
        break;
      case 404:
        toast.error('Resource not found.');
        break;
      case 429:
        toast.error('Too many requests. Please try again later.');
        break;
      case 500:
        toast.error('Server error. Please try again later.');
        break;
      default:
        if (!navigator.onLine) {
          toast.error('No internet connection. Please check your network.');
        } else {
          toast.error(apiError.message || 'Something went wrong. Please try again.');
        }
    }

    return Promise.reject(apiError);
  }
);

/**
 * Generic API methods
 */
export const api = {
  /**
   * GET request
   */
  get: <T = any>(url: string, config?: AxiosRequestConfig): Promise<AxiosResponse<ApiResponse<T>>> => {
    return apiClient.get(url, config);
  },

  /**
   * POST request
   */
  post: <T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<AxiosResponse<ApiResponse<T>>> => {
    return apiClient.post(url, data, config);
  },

  /**
   * PUT request
   */
  put: <T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<AxiosResponse<ApiResponse<T>>> => {
    return apiClient.put(url, data, config);
  },

  /**
   * DELETE request
   */
  delete: <T = any>(url: string, config?: AxiosRequestConfig): Promise<AxiosResponse<ApiResponse<T>>> => {
    return apiClient.delete(url, config);
  },

  /**
   * PATCH request
   */
  patch: <T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<AxiosResponse<ApiResponse<T>>> => {
    return apiClient.patch(url, data, config);
  },

  /**
   * File upload with progress tracking
   */
  upload: <T = any>(
    url: string, 
    file: File | FormData, 
    onUploadProgress?: (progressEvent: any) => void
  ): Promise<AxiosResponse<ApiResponse<T>>> => {
    const formData = file instanceof FormData ? file : new FormData();
    
    if (file instanceof File) {
      formData.append('file', file);
    }

    return apiClient.post(url, formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
      onUploadProgress,
    });
  }
};

/**
 * Health check utility
 */
export const healthCheck = async (): Promise<boolean> => {
  try {
    const response = await api.get('/actuator/health');
    return response.data.success;
  } catch {
    return false;
  }
};

export default api;