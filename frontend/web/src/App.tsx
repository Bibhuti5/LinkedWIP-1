import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from 'react-query';
import { Provider as ReduxProvider } from 'react-redux';
import { ThemeProvider, createTheme } from '@mui/material/styles';
import CssBaseline from '@mui/material/CssBaseline';
import { HelmetProvider } from 'react-helmet-async';
import { Toaster } from 'react-hot-toast';

// Context Providers
import { AuthProvider } from '@/context/AuthContext';
import { SocketProvider } from '@/context/SocketContext';

// Redux Store
import { store } from '@/store/store';

// Components
import Layout from '@/components/common/Layout';
import ProtectedRoute from '@/components/auth/ProtectedRoute';
import LoadingScreen from '@/components/common/LoadingScreen';

// Pages
import LoginPage from '@/pages/auth/LoginPage';
import RegisterPage from '@/pages/auth/RegisterPage';
import DashboardPage from '@/pages/dashboard/DashboardPage';
import ProfilePage from '@/pages/profile/ProfilePage';
import PostsPage from '@/pages/posts/PostsPage';
import PostDetailPage from '@/pages/posts/PostDetailPage';
import CreatePostPage from '@/pages/posts/CreatePostPage';
import MessagesPage from '@/pages/messages/MessagesPage';
import SettingsPage from '@/pages/settings/SettingsPage';
import NotFoundPage from '@/pages/common/NotFoundPage';

// Styles
import '@/styles/global.css';

/**
 * React Query Client Configuration
 */
const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      retry: 2,
      refetchOnWindowFocus: false,
      staleTime: 5 * 60 * 1000, // 5 minutes
      cacheTime: 10 * 60 * 1000, // 10 minutes
    },
    mutations: {
      retry: 1,
    },
  },
});

/**
 * Material-UI Theme Configuration
 */
const theme = createTheme({
  palette: {
    mode: 'light',
    primary: {
      main: '#1976d2',
      light: '#42a5f5',
      dark: '#1565c0',
    },
    secondary: {
      main: '#dc004e',
      light: '#ff5983',
      dark: '#9a0036',
    },
    background: {
      default: '#f5f5f5',
      paper: '#ffffff',
    },
    text: {
      primary: '#333333',
      secondary: '#666666',
    },
  },
  typography: {
    fontFamily: '"Inter", "Roboto", "Helvetica", "Arial", sans-serif',
    h1: {
      fontWeight: 700,
    },
    h2: {
      fontWeight: 600,
    },
    h3: {
      fontWeight: 600,
    },
    h4: {
      fontWeight: 500,
    },
    h5: {
      fontWeight: 500,
    },
    h6: {
      fontWeight: 500,
    },
  },
  components: {
    MuiButton: {
      styleOverrides: {
        root: {
          textTransform: 'none',
          borderRadius: 8,
          fontWeight: 500,
        },
      },
    },
    MuiCard: {
      styleOverrides: {
        root: {
          borderRadius: 12,
          boxShadow: '0 2px 8px rgba(0, 0, 0, 0.1)',
        },
      },
    },
    MuiTextField: {
      styleOverrides: {
        root: {
          '& .MuiOutlinedInput-root': {
            borderRadius: 8,
          },
        },
      },
    },
  },
});

/**
 * Main Application Component
 * 
 * Features:
 * - React Router for navigation
 * - Redux for state management
 * - React Query for server state
 * - Material-UI for components
 * - Authentication context
 * - WebSocket context for real-time features
 * - PWA support
 * - Error boundaries
 * 
 * @returns {JSX.Element} The main application
 */
const App: React.FC = () => {
  return (
    <HelmetProvider>
      <ReduxProvider store={store}>
        <QueryClientProvider client={queryClient}>
          <ThemeProvider theme={theme}>
            <CssBaseline />
            <AuthProvider>
              <SocketProvider>
                <Router>
                  <Routes>
                    {/* Public Routes */}
                    <Route path="/login" element={<LoginPage />} />
                    <Route path="/register" element={<RegisterPage />} />
                    
                    {/* Protected Routes */}
                    <Route
                      path="/*"
                      element={
                        <ProtectedRoute>
                          <Layout>
                            <Routes>
                              <Route path="/" element={<Navigate to="/dashboard" replace />} />
                              <Route path="/dashboard" element={<DashboardPage />} />
                              <Route path="/profile/:userId?" element={<ProfilePage />} />
                              <Route path="/posts" element={<PostsPage />} />
                              <Route path="/posts/:postId" element={<PostDetailPage />} />
                              <Route path="/create-post" element={<CreatePostPage />} />
                              <Route path="/messages" element={<MessagesPage />} />
                              <Route path="/messages/:conversationId" element={<MessagesPage />} />
                              <Route path="/settings" element={<SettingsPage />} />
                              <Route path="*" element={<NotFoundPage />} />
                            </Routes>
                          </Layout>
                        </ProtectedRoute>
                      }
                    />
                  </Routes>
                </Router>
                
                {/* Global Components */}
                <Toaster
                  position="top-right"
                  toastOptions={{
                    duration: 4000,
                    style: {
                      background: '#363636',
                      color: '#fff',
                    },
                    success: {
                      duration: 3000,
                      iconTheme: {
                        primary: '#4caf50',
                        secondary: '#fff',
                      },
                    },
                    error: {
                      duration: 5000,
                      iconTheme: {
                        primary: '#f44336',
                        secondary: '#fff',
                      },
                    },
                  }}
                />
                
                {/* Loading Screen for initial app load */}
                <React.Suspense fallback={<LoadingScreen />}>
                  {/* Lazy loaded components will show loading screen */}
                </React.Suspense>
              </SocketProvider>
            </AuthProvider>
          </ThemeProvider>
        </QueryClientProvider>
      </ReduxProvider>
    </HelmetProvider>
  );
};

export default App;