/**
 * Login Page Component
 * 
 * Beautiful login page with:
 * - Email/password form with validation
 * - OAuth integration (GitHub, Google)
 * - Remember me functionality
 * - Forgot password link
 * - Responsive design
 */

import React, { useState } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import {
  Box,
  Paper,
  TextField,
  Button,
  Typography,
  Checkbox,
  FormControlLabel,
  Divider,
  Alert,
  IconButton,
  InputAdornment,
  Stack,
} from '@mui/material';
import {
  Visibility,
  VisibilityOff,
  GitHub,
  Google,
  LoginOutlined,
} from '@mui/icons-material';
import { styled } from '@mui/material/styles';
import { motion } from 'framer-motion';
import { Helmet } from 'react-helmet-async';

import { useAuth } from '@/context/AuthContext';
import { LoginRequest } from '@/types/auth';
import { authService, oauthProviders } from '@/services/authService';
import LoadingScreen from '@/components/common/LoadingScreen';

/**
 * Styled components
 */
const LoginContainer = styled(Box)(({ theme }) => ({
  minHeight: '100vh',
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center',
  background: `linear-gradient(135deg, ${theme.palette.primary.main}15 0%, ${theme.palette.secondary.main}15 100%)`,
  padding: theme.spacing(2),
}));

const LoginPaper = styled(Paper)(({ theme }) => ({
  padding: theme.spacing(4),
  maxWidth: 400,
  width: '100%',
  borderRadius: theme.spacing(2),
  boxShadow: '0 20px 40px rgba(0,0,0,0.1)',
  backdropFilter: 'blur(10px)',
  border: `1px solid ${theme.palette.divider}`,
}));

const LogoBox = styled(Box)(({ theme }) => ({
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center',
  marginBottom: theme.spacing(3),
  gap: theme.spacing(1),
}));

const OAuthButton = styled(Button)(({ theme }) => ({
  width: '100%',
  padding: theme.spacing(1.5),
  marginBottom: theme.spacing(1),
  borderRadius: theme.spacing(1),
  textTransform: 'none',
  fontWeight: 500,
  border: `1px solid ${theme.palette.divider}`,
  '&:hover': {
    transform: 'translateY(-1px)',
    boxShadow: '0 4px 12px rgba(0,0,0,0.15)',
  },
  transition: 'all 0.2s ease-in-out',
}));

/**
 * Form validation schema
 */
interface LoginFormData extends LoginRequest {
  email: string;
  password: string;
  rememberMe: boolean;
}

/**
 * Login Page Component
 */
const LoginPage: React.FC = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { login, isLoading, error } = useAuth();
  
  const [showPassword, setShowPassword] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);

  // Get redirect path from location state
  const from = location.state?.from?.pathname || '/dashboard';

  /**
   * React Hook Form setup
   */
  const {
    register,
    handleSubmit,
    formState: { errors, isValid },
    setError,
  } = useForm<LoginFormData>({
    mode: 'onChange',
    defaultValues: {
      email: '',
      password: '',
      rememberMe: false,
    },
  });

  /**
   * Handle form submission
   */
  const onSubmit = async (data: LoginFormData) => {
    try {
      setIsSubmitting(true);
      await login(data);
      navigate(from, { replace: true });
    } catch (error: any) {
      console.error('Login error:', error);
      setError('root', {
        message: error.message || 'Login failed. Please try again.',
      });
    } finally {
      setIsSubmitting(false);
    }
  };

  /**
   * Handle OAuth login
   */
  const handleOAuthLogin = (provider: string) => {
    authService.initiateOAuthLogin(provider);
  };

  /**
   * Show loading screen during authentication
   */
  if (isLoading && !isSubmitting) {
    return <LoadingScreen message="Checking authentication..." />;
  }

  return (
    <>
      <Helmet>
        <title>Login - DevSocial</title>
        <meta name="description" content="Login to DevSocial - Connect with developers worldwide" />
      </Helmet>

      <LoginContainer>
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5 }}
        >
          <LoginPaper elevation={0}>
            {/* Logo and Title */}
            <LogoBox>
              <Box
                sx={{
                  width: 48,
                  height: 48,
                  borderRadius: '50%',
                  background: 'linear-gradient(45deg, #1976d2 30%, #42a5f5 90%)',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  mr: 1,
                }}
              >
                <Typography variant="h6" sx={{ color: 'white', fontWeight: 'bold' }}>
                  DS
                </Typography>
              </Box>
              <Typography variant="h4" sx={{ fontWeight: 700, color: 'primary.main' }}>
                DevSocial
              </Typography>
            </LogoBox>

            <Typography
              variant="h5"
              align="center"
              sx={{ mb: 1, fontWeight: 600 }}
            >
              Welcome Back
            </Typography>
            
            <Typography
              variant="body2"
              align="center"
              color="text.secondary"
              sx={{ mb: 3 }}
            >
              Sign in to continue to your developer community
            </Typography>

            {/* Error Alert */}
            {(error || errors.root) && (
              <Alert severity="error" sx={{ mb: 2 }}>
                {error || errors.root?.message}
              </Alert>
            )}

            {/* OAuth Buttons */}
            <Stack spacing={1} sx={{ mb: 2 }}>
              <OAuthButton
                variant="outlined"
                startIcon={<GitHub />}
                onClick={() => handleOAuthLogin('github')}
                sx={{
                  color: '#333',
                  borderColor: '#d0d7de',
                  '&:hover': {
                    backgroundColor: '#f6f8fa',
                    borderColor: '#d0d7de',
                  },
                }}
              >
                Continue with GitHub
              </OAuthButton>
              
              <OAuthButton
                variant="outlined"
                startIcon={<Google />}
                onClick={() => handleOAuthLogin('google')}
                sx={{
                  color: '#1976d2',
                  borderColor: '#dadce0',
                  '&:hover': {
                    backgroundColor: '#f8f9fa',
                    borderColor: '#dadce0',
                  },
                }}
              >
                Continue with Google
              </OAuthButton>
            </Stack>

            {/* Divider */}
            <Divider sx={{ my: 2 }}>
              <Typography variant="body2" color="text.secondary">
                or
              </Typography>
            </Divider>

            {/* Login Form */}
            <Box component="form" onSubmit={handleSubmit(onSubmit)} noValidate>
              <TextField
                {...register('email', {
                  required: 'Email is required',
                  pattern: {
                    value: /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i,
                    message: 'Please enter a valid email address',
                  },
                })}
                fullWidth
                label="Email Address"
                type="email"
                autoComplete="email"
                autoFocus
                margin="normal"
                error={!!errors.email}
                helperText={errors.email?.message}
                sx={{ mb: 2 }}
              />

              <TextField
                {...register('password', {
                  required: 'Password is required',
                  minLength: {
                    value: 6,
                    message: 'Password must be at least 6 characters',
                  },
                })}
                fullWidth
                label="Password"
                type={showPassword ? 'text' : 'password'}
                autoComplete="current-password"
                margin="normal"
                error={!!errors.password}
                helperText={errors.password?.message}
                InputProps={{
                  endAdornment: (
                    <InputAdornment position="end">
                      <IconButton
                        onClick={() => setShowPassword(!showPassword)}
                        edge="end"
                        aria-label="toggle password visibility"
                      >
                        {showPassword ? <VisibilityOff /> : <Visibility />}
                      </IconButton>
                    </InputAdornment>
                  ),
                }}
                sx={{ mb: 1 }}
              />

              <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
                <FormControlLabel
                  control={
                    <Checkbox
                      {...register('rememberMe')}
                      color="primary"
                      size="small"
                    />
                  }
                  label={
                    <Typography variant="body2" color="text.secondary">
                      Remember me
                    </Typography>
                  }
                />
                
                <Link
                  to="/forgot-password"
                  style={{
                    textDecoration: 'none',
                    color: '#1976d2',
                    fontSize: '0.875rem',
                  }}
                >
                  Forgot password?
                </Link>
              </Box>

              <Button
                type="submit"
                fullWidth
                variant="contained"
                size="large"
                disabled={!isValid || isSubmitting}
                startIcon={<LoginOutlined />}
                sx={{
                  py: 1.5,
                  mb: 2,
                  borderRadius: 2,
                  textTransform: 'none',
                  fontWeight: 600,
                  background: 'linear-gradient(45deg, #1976d2 30%, #42a5f5 90%)',
                  '&:hover': {
                    background: 'linear-gradient(45deg, #1565c0 30%, #1976d2 90%)',
                  },
                }}
              >
                {isSubmitting ? 'Signing In...' : 'Sign In'}
              </Button>

              <Typography variant="body2" align="center" color="text.secondary">
                Don't have an account?{' '}
                <Link
                  to="/register"
                  style={{
                    color: '#1976d2',
                    textDecoration: 'none',
                    fontWeight: 500,
                  }}
                >
                  Sign up here
                </Link>
              </Typography>
            </Box>
          </LoginPaper>
        </motion.div>
      </LoginContainer>
    </>
  );
};

export default LoginPage;