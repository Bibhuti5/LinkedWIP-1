/**
 * Authentication Service
 * 
 * Handles all authentication-related API calls including:
 * - Login/Register
 * - OAuth integration
 * - Token management
 * - Password reset
 * - User profile management
 */

import { api, tokenManager } from './api';
import {
  AuthResponse,
  LoginRequest,
  RegisterRequest,
  RefreshTokenRequest,
  ForgotPasswordRequest,
  ResetPasswordRequest,
  ChangePasswordRequest,
  OAuthCallback,
  User,
} from '@/types/auth';

/**
 * OAuth Providers Configuration
 */
export const oauthProviders = [
  {
    id: 'github',
    name: 'GitHub',
    icon: 'github',
    color: '#333333',
    authUrl: '/api/auth/oauth2/github',
  },
  {
    id: 'google',
    name: 'Google',
    icon: 'google',
    color: '#4285f4',
    authUrl: '/api/auth/oauth2/google',
  },
];

/**
 * Authentication Service Class
 */
class AuthService {
  /**
   * Login with email and password
   */
  async login(credentials: LoginRequest): Promise<AuthResponse> {
    const response = await api.post<AuthResponse>('/api/auth/login', credentials);
    
    const authData = response.data.data;
    
    // Store tokens and user data
    tokenManager.setTokens(authData.accessToken, authData.refreshToken);
    localStorage.setItem('user', JSON.stringify(authData.user));
    
    return authData;
  }

  /**
   * Register new user account
   */
  async register(userData: RegisterRequest): Promise<AuthResponse> {
    const response = await api.post<AuthResponse>('/api/auth/register', userData);
    
    const authData = response.data.data;
    
    // Store tokens and user data
    tokenManager.setTokens(authData.accessToken, authData.refreshToken);
    localStorage.setItem('user', JSON.stringify(authData.user));
    
    return authData;
  }

  /**
   * Logout user and clear tokens
   */
  async logout(): Promise<void> {
    try {
      // Call logout endpoint to invalidate token on server
      await api.post('/api/auth/logout');
    } catch (error) {
      // Continue with local logout even if server call fails
      console.warn('Server logout failed, proceeding with local logout:', error);
    } finally {
      // Clear local storage
      tokenManager.clearTokens();
    }
  }

  /**
   * Refresh access token using refresh token
   */
  async refreshToken(): Promise<AuthResponse> {
    const refreshToken = tokenManager.getRefreshToken();
    
    if (!refreshToken) {
      throw new Error('No refresh token available');
    }

    const response = await api.post<AuthResponse>('/api/auth/refresh', {
      refreshToken,
    } as RefreshTokenRequest);
    
    const authData = response.data.data;
    
    // Update stored tokens
    tokenManager.setTokens(authData.accessToken, authData.refreshToken);
    
    return authData;
  }

  /**
   * Get current user profile
   */
  async getCurrentUser(): Promise<User> {
    const response = await api.get<User>('/api/auth/me');
    
    const user = response.data.data;
    
    // Update stored user data
    localStorage.setItem('user', JSON.stringify(user));
    
    return user;
  }

  /**
   * Update user profile
   */
  async updateProfile(userData: Partial<User>): Promise<User> {
    const response = await api.put<User>('/api/auth/profile', userData);
    
    const user = response.data.data;
    
    // Update stored user data
    localStorage.setItem('user', JSON.stringify(user));
    
    return user;
  }

  /**
   * Change user password
   */
  async changePassword(passwordData: ChangePasswordRequest): Promise<void> {
    await api.post('/api/auth/change-password', passwordData);
  }

  /**
   * Request password reset
   */
  async forgotPassword(data: ForgotPasswordRequest): Promise<void> {
    await api.post('/api/auth/forgot-password', data);
  }

  /**
   * Reset password with token
   */
  async resetPassword(data: ResetPasswordRequest): Promise<void> {
    await api.post('/api/auth/reset-password', data);
  }

  /**
   * Verify email address
   */
  async verifyEmail(token: string): Promise<void> {
    await api.post('/api/auth/verify-email', { token });
  }

  /**
   * Resend email verification
   */
  async resendVerification(): Promise<void> {
    await api.post('/api/auth/resend-verification');
  }

  /**
   * OAuth login initiation
   */
  initiateOAuthLogin(provider: string): void {
    const baseUrl = window.location.origin;
    const redirectUri = `${baseUrl}/auth/callback/${provider}`;
    const state = Math.random().toString(36).substring(2, 15);
    
    // Store state for verification
    localStorage.setItem('oauth_state', state);
    
    // Redirect to OAuth provider
    const authUrl = `/api/auth/oauth2/${provider}?redirect_uri=${encodeURIComponent(redirectUri)}&state=${state}`;
    window.location.href = authUrl;
  }

  /**
   * Handle OAuth callback
   */
  async handleOAuthCallback(callbackData: OAuthCallback): Promise<AuthResponse> {
    // Verify state parameter
    const storedState = localStorage.getItem('oauth_state');
    if (storedState !== callbackData.state) {
      throw new Error('Invalid OAuth state parameter');
    }
    
    // Clear stored state
    localStorage.removeItem('oauth_state');
    
    const response = await api.post<AuthResponse>(`/api/auth/oauth2/${callbackData.provider}/callback`, {
      code: callbackData.code,
      state: callbackData.state,
    });
    
    const authData = response.data.data;
    
    // Store tokens and user data
    tokenManager.setTokens(authData.accessToken, authData.refreshToken);
    localStorage.setItem('user', JSON.stringify(authData.user));
    
    return authData;
  }

  /**
   * Check if user is authenticated
   */
  isAuthenticated(): boolean {
    const token = tokenManager.getAccessToken();
    return token !== null && !tokenManager.isTokenExpired(token);
  }

  /**
   * Get stored user data
   */
  getStoredUser(): User | null {
    try {
      const userData = localStorage.getItem('user');
      return userData ? JSON.parse(userData) : null;
    } catch {
      return null;
    }
  }

  /**
   * Check authentication status and refresh if needed
   */
  async checkAuthStatus(): Promise<boolean> {
    const token = tokenManager.getAccessToken();
    
    if (!token) {
      return false;
    }
    
    if (tokenManager.isTokenExpired(token)) {
      try {
        await this.refreshToken();
        return true;
      } catch {
        tokenManager.clearTokens();
        return false;
      }
    }
    
    return true;
  }

  /**
   * Enable two-factor authentication
   */
  async enableTwoFactor(): Promise<{ qrCode: string; secret: string }> {
    const response = await api.post<{ qrCode: string; secret: string }>('/api/auth/2fa/enable');
    return response.data.data;
  }

  /**
   * Verify two-factor authentication setup
   */
  async verifyTwoFactor(code: string): Promise<{ backupCodes: string[] }> {
    const response = await api.post<{ backupCodes: string[] }>('/api/auth/2fa/verify', { code });
    return response.data.data;
  }

  /**
   * Disable two-factor authentication
   */
  async disableTwoFactor(password: string): Promise<void> {
    await api.post('/api/auth/2fa/disable', { password });
  }
}

// Export singleton instance
export const authService = new AuthService();
export default authService;