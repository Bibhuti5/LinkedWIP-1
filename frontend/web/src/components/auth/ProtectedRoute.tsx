/**
 * Protected Route Component
 * 
 * Higher-order component that protects routes requiring authentication.
 * Redirects unauthenticated users to login page while preserving
 * the intended destination for post-login redirect.
 */

import React from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import { useAuth } from '@/context/AuthContext';
import LoadingScreen from '@/components/common/LoadingScreen';

/**
 * Protected Route Component Props
 */
interface ProtectedRouteProps {
  children: React.ReactNode;
  requiredRole?: string;
  fallback?: React.ReactNode;
}

/**
 * Protected Route Component
 * 
 * Wraps components that require authentication. Shows loading screen
 * while checking auth status, redirects to login if not authenticated,
 * and renders children if authenticated.
 * 
 * @param children - Components to render if authenticated
 * @param requiredRole - Optional role requirement (future use)
 * @param fallback - Optional custom loading component
 */
const ProtectedRoute: React.FC<ProtectedRouteProps> = ({
  children,
  requiredRole,
  fallback,
}) => {
  const { isAuthenticated, isLoading, user } = useAuth();
  const location = useLocation();

  /**
   * Show loading screen while checking authentication
   */
  if (isLoading) {
    return fallback || <LoadingScreen message="Verifying authentication..." />;
  }

  /**
   * Redirect to login if not authenticated
   * Preserve the current location for post-login redirect
   */
  if (!isAuthenticated) {
    return (
      <Navigate
        to="/login"
        state={{ from: location }}
        replace
      />
    );
  }

  /**
   * Check role-based access (future implementation)
   * This can be extended to support different user roles
   */
  if (requiredRole && user) {
    // TODO: Implement role checking when backend supports user roles
    // const hasRequiredRole = user.roles?.includes(requiredRole);
    // if (!hasRequiredRole) {
    //   return <Navigate to="/unauthorized" replace />;
    // }
  }

  /**
   * User is authenticated, render protected content
   */
  return <>{children}</>;
};

export default ProtectedRoute;