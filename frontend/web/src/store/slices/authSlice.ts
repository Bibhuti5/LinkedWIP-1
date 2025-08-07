/**
 * Authentication Redux Slice
 * 
 * Manages authentication state including:
 * - User login/logout
 * - Token management
 * - Authentication status
 * - OAuth flow
 * - Error handling
 */

import { createSlice, createAsyncThunk, PayloadAction } from '@reduxjs/toolkit';
import { AuthState, User, LoginRequest, RegisterRequest, AuthResponse } from '@/types/auth';
import { authService } from '@/services/authService';
import { toast } from 'react-hot-toast';

/**
 * Initial authentication state
 */
const initialState: AuthState = {
  user: authService.getStoredUser(),
  accessToken: localStorage.getItem('accessToken'),
  refreshToken: localStorage.getItem('refreshToken'),
  isAuthenticated: authService.isAuthenticated(),
  isLoading: false,
  error: null,
};

/**
 * Async Thunks for authentication actions
 */

/**
 * Login user with email and password
 */
export const loginUser = createAsyncThunk(
  'auth/login',
  async (credentials: LoginRequest, { rejectWithValue }) => {
    try {
      const response = await authService.login(credentials);
      toast.success(`Welcome back, ${response.user.firstName}!`);
      return response;
    } catch (error: any) {
      const message = error.message || 'Login failed. Please try again.';
      toast.error(message);
      return rejectWithValue(message);
    }
  }
);

/**
 * Register new user account
 */
export const registerUser = createAsyncThunk(
  'auth/register',
  async (userData: RegisterRequest, { rejectWithValue }) => {
    try {
      const response = await authService.register(userData);
      toast.success(`Welcome to DevSocial, ${response.user.firstName}!`);
      return response;
    } catch (error: any) {
      const message = error.message || 'Registration failed. Please try again.';
      toast.error(message);
      return rejectWithValue(message);
    }
  }
);

/**
 * Logout user
 */
export const logoutUser = createAsyncThunk(
  'auth/logout',
  async (_, { rejectWithValue }) => {
    try {
      await authService.logout();
      toast.success('You have been logged out successfully.');
    } catch (error: any) {
      // Continue with logout even if server call fails
      console.warn('Logout error:', error);
    }
  }
);

/**
 * Refresh authentication token
 */
export const refreshToken = createAsyncThunk(
  'auth/refreshToken',
  async (_, { rejectWithValue }) => {
    try {
      const response = await authService.refreshToken();
      return response;
    } catch (error: any) {
      const message = error.message || 'Token refresh failed';
      return rejectWithValue(message);
    }
  }
);

/**
 * Get current user profile
 */
export const getCurrentUser = createAsyncThunk(
  'auth/getCurrentUser',
  async (_, { rejectWithValue }) => {
    try {
      const user = await authService.getCurrentUser();
      return user;
    } catch (error: any) {
      const message = error.message || 'Failed to fetch user profile';
      return rejectWithValue(message);
    }
  }
);

/**
 * Update user profile
 */
export const updateUserProfile = createAsyncThunk(
  'auth/updateProfile',
  async (userData: Partial<User>, { rejectWithValue }) => {
    try {
      const user = await authService.updateProfile(userData);
      toast.success('Profile updated successfully!');
      return user;
    } catch (error: any) {
      const message = error.message || 'Profile update failed';
      toast.error(message);
      return rejectWithValue(message);
    }
  }
);

/**
 * Check authentication status on app load
 */
export const checkAuthStatus = createAsyncThunk(
  'auth/checkStatus',
  async (_, { rejectWithValue }) => {
    try {
      const isAuthenticated = await authService.checkAuthStatus();
      
      if (isAuthenticated) {
        // Get fresh user data
        const user = await authService.getCurrentUser();
        return { isAuthenticated: true, user };
      }
      
      return { isAuthenticated: false, user: null };
    } catch (error: any) {
      return rejectWithValue(error.message || 'Authentication check failed');
    }
  }
);

/**
 * Authentication slice
 */
const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    /**
     * Clear authentication error
     */
    clearAuthError: (state) => {
      state.error = null;
    },

    /**
     * Set authentication loading state
     */
    setAuthLoading: (state, action: PayloadAction<boolean>) => {
      state.isLoading = action.payload;
    },

    /**
     * Update user data locally
     */
    updateUserData: (state, action: PayloadAction<Partial<User>>) => {
      if (state.user) {
        state.user = { ...state.user, ...action.payload };
        localStorage.setItem('user', JSON.stringify(state.user));
      }
    },

    /**
     * Reset authentication state
     */
    resetAuthState: () => {
      return initialState;
    },
  },
  extraReducers: (builder) => {
    // Login
    builder
      .addCase(loginUser.pending, (state) => {
        state.isLoading = true;
        state.error = null;
      })
      .addCase(loginUser.fulfilled, (state, action) => {
        state.isLoading = false;
        state.user = action.payload.user;
        state.accessToken = action.payload.accessToken;
        state.refreshToken = action.payload.refreshToken;
        state.isAuthenticated = true;
        state.error = null;
      })
      .addCase(loginUser.rejected, (state, action) => {
        state.isLoading = false;
        state.error = action.payload as string;
        state.isAuthenticated = false;
      });

    // Register
    builder
      .addCase(registerUser.pending, (state) => {
        state.isLoading = true;
        state.error = null;
      })
      .addCase(registerUser.fulfilled, (state, action) => {
        state.isLoading = false;
        state.user = action.payload.user;
        state.accessToken = action.payload.accessToken;
        state.refreshToken = action.payload.refreshToken;
        state.isAuthenticated = true;
        state.error = null;
      })
      .addCase(registerUser.rejected, (state, action) => {
        state.isLoading = false;
        state.error = action.payload as string;
        state.isAuthenticated = false;
      });

    // Logout
    builder
      .addCase(logoutUser.pending, (state) => {
        state.isLoading = true;
      })
      .addCase(logoutUser.fulfilled, (state) => {
        state.isLoading = false;
        state.user = null;
        state.accessToken = null;
        state.refreshToken = null;
        state.isAuthenticated = false;
        state.error = null;
      })
      .addCase(logoutUser.rejected, (state) => {
        // Still logout locally even if server call failed
        state.isLoading = false;
        state.user = null;
        state.accessToken = null;
        state.refreshToken = null;
        state.isAuthenticated = false;
        state.error = null;
      });

    // Refresh Token
    builder
      .addCase(refreshToken.fulfilled, (state, action) => {
        state.accessToken = action.payload.accessToken;
        state.refreshToken = action.payload.refreshToken;
        state.user = action.payload.user;
        state.isAuthenticated = true;
      })
      .addCase(refreshToken.rejected, (state) => {
        state.user = null;
        state.accessToken = null;
        state.refreshToken = null;
        state.isAuthenticated = false;
      });

    // Get Current User
    builder
      .addCase(getCurrentUser.pending, (state) => {
        state.isLoading = true;
      })
      .addCase(getCurrentUser.fulfilled, (state, action) => {
        state.isLoading = false;
        state.user = action.payload;
        state.error = null;
      })
      .addCase(getCurrentUser.rejected, (state, action) => {
        state.isLoading = false;
        state.error = action.payload as string;
      });

    // Update Profile
    builder
      .addCase(updateUserProfile.pending, (state) => {
        state.isLoading = true;
        state.error = null;
      })
      .addCase(updateUserProfile.fulfilled, (state, action) => {
        state.isLoading = false;
        state.user = action.payload;
        state.error = null;
      })
      .addCase(updateUserProfile.rejected, (state, action) => {
        state.isLoading = false;
        state.error = action.payload as string;
      });

    // Check Auth Status
    builder
      .addCase(checkAuthStatus.pending, (state) => {
        state.isLoading = true;
      })
      .addCase(checkAuthStatus.fulfilled, (state, action) => {
        state.isLoading = false;
        state.isAuthenticated = action.payload.isAuthenticated;
        state.user = action.payload.user;
        state.error = null;
      })
      .addCase(checkAuthStatus.rejected, (state, action) => {
        state.isLoading = false;
        state.isAuthenticated = false;
        state.user = null;
        state.accessToken = null;
        state.refreshToken = null;
        state.error = action.payload as string;
      });
  },
});

// Export actions
export const {
  clearAuthError,
  setAuthLoading,
  updateUserData,
  resetAuthState,
} = authSlice.actions;

// Export selectors
export const selectAuth = (state: { auth: AuthState }) => state.auth;
export const selectUser = (state: { auth: AuthState }) => state.auth.user;
export const selectIsAuthenticated = (state: { auth: AuthState }) => state.auth.isAuthenticated;
export const selectAuthLoading = (state: { auth: AuthState }) => state.auth.isLoading;
export const selectAuthError = (state: { auth: AuthState }) => state.auth.error;

// Export reducer
export default authSlice.reducer;