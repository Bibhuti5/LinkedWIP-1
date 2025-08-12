/**
 * Authentication Context Provider
 * 
 * Provides authentication state and methods throughout the application.
 * Integrates with Redux store and handles authentication lifecycle.
 */

import React, { createContext, useContext, useEffect, ReactNode } from 'react';
import { useAppDispatch, useAppSelector } from '@/store/hooks';
import { 
  checkAuthStatus, 
  selectAuth,
  selectIsAuthenticated,
  selectUser,
  selectAuthLoading,
  selectAuthError,
  loginUser,
  registerUser,
  logoutUser
} from '@/store/slices/authSlice';
import { LoginRequest, RegisterRequest, User } from '@/types/auth';

/**
 * Authentication context interface
 */
interface AuthContextType {
  // State
  user: User | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  error: string | null;
  
  // Actions
  login: (credentials: LoginRequest) => Promise<void>;
  register: (userData: RegisterRequest) => Promise<void>;
  logout: () => Promise<void>;
  refreshAuth: () => Promise<void>;
}

/**
 * Create authentication context
 */
const AuthContext = createContext<AuthContextType | undefined>(undefined);

/**
 * Authentication context provider props
 */
interface AuthProviderProps {
  children: ReactNode;
}

/**
 * Authentication Context Provider Component
 * 
 * Manages authentication state and provides auth methods to child components.
 * Automatically checks authentication status on mount and handles token refresh.
 */
export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
  const dispatch = useAppDispatch();
  
  // Select auth state from Redux store
  const user = useAppSelector(selectUser);
  const isAuthenticated = useAppSelector(selectIsAuthenticated);
  const isLoading = useAppSelector(selectAuthLoading);
  const error = useAppSelector(selectAuthError);

  /**
   * Check authentication status on component mount
   */
  useEffect(() => {
    dispatch(checkAuthStatus());
  }, [dispatch]);

  /**
   * Login user with credentials
   */
  const login = async (credentials: LoginRequest): Promise<void> => {
    await dispatch(loginUser(credentials)).unwrap();
  };

  /**
   * Register new user account
   */
  const register = async (userData: RegisterRequest): Promise<void> => {
    await dispatch(registerUser(userData)).unwrap();
  };

  /**
   * Logout current user
   */
  const logout = async (): Promise<void> => {
    await dispatch(logoutUser()).unwrap();
  };

  /**
   * Refresh authentication status
   */
  const refreshAuth = async (): Promise<void> => {
    await dispatch(checkAuthStatus()).unwrap();
  };

  /**
   * Context value
   */
  const contextValue: AuthContextType = {
    // State
    user,
    isAuthenticated,
    isLoading,
    error,
    
    // Actions
    login,
    register,
    logout,
    refreshAuth,
  };

  return (
    <AuthContext.Provider value={contextValue}>
      {children}
    </AuthContext.Provider>
  );
};

/**
 * Custom hook to use authentication context
 * 
 * @returns Authentication context value
 * @throws Error if used outside AuthProvider
 */
export const useAuth = (): AuthContextType => {
  const context = useContext(AuthContext);
  
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  
  return context;
};

/**
 * Higher-order component to require authentication
 * 
 * @param Component - Component to wrap
 * @returns Wrapped component that requires authentication
 */
export const withAuth = <P extends object>(
  Component: React.ComponentType<P>
): React.FC<P> => {
  return (props: P) => {
    const { isAuthenticated, isLoading } = useAuth();
    
    if (isLoading) {
      return <div>Loading...</div>; // Replace with proper loading component
    }
    
    if (!isAuthenticated) {
      return <div>Please log in to access this page.</div>; // Replace with redirect logic
    }
    
    return <Component {...props} />;
  };
};

export default AuthContext;