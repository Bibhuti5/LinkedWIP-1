/**
 * Register Page Component
 * 
 * Comprehensive registration page with:
 * - Multi-step form with validation
 * - OAuth integration (GitHub, Google)
 * - Terms acceptance
 * - Email verification
 * - Responsive design
 */

import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
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
  Grid,
} from '@mui/material';
import {
  Visibility,
  VisibilityOff,
  GitHub,
  Google,
  PersonAddOutlined,
} from '@mui/icons-material';
import { styled } from '@mui/material/styles';
import { motion } from 'framer-motion';
import { Helmet } from 'react-helmet-async';

import { useAuth } from '@/context/AuthContext';
import { RegisterRequest } from '@/types/auth';
import { authService } from '@/services/authService';
import LoadingScreen from '@/components/common/LoadingScreen';

/**
 * Styled components (reusing from LoginPage with modifications)
 */
const RegisterContainer = styled(Box)(({ theme }) => ({
  minHeight: '100vh',
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center',
  background: `linear-gradient(135deg, ${theme.palette.primary.main}15 0%, ${theme.palette.secondary.main}15 100%)`,
  padding: theme.spacing(2),
}));

const RegisterPaper = styled(Paper)(({ theme }) => ({
  padding: theme.spacing(4),
  maxWidth: 500,
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
interface RegisterFormData extends RegisterRequest {
  confirmPassword: string;
}

/**
 * Register Page Component
 */
const RegisterPage: React.FC = () => {
  const navigate = useNavigate();
  const { register: registerUser, isLoading, error } = useAuth();
  
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);

  /**
   * React Hook Form setup
   */
  const {
    register,
    handleSubmit,
    formState: { errors, isValid },
    setError,
    watch,
  } = useForm<RegisterFormData>({
    mode: 'onChange',
    defaultValues: {
      email: '',
      username: '',
      password: '',
      confirmPassword: '',
      firstName: '',
      lastName: '',
      acceptTerms: false,
    },
  });

  // Watch password for confirmation validation
  const password = watch('password');

  /**
   * Handle form submission
   */
  const onSubmit = async (data: RegisterFormData) => {
    try {
      setIsSubmitting(true);
      
      // Remove confirmPassword from data before sending to API
      const { confirmPassword, ...registerData } = data;
      
      await registerUser(registerData);
      navigate('/dashboard', { replace: true });
    } catch (error: any) {
      console.error('Registration error:', error);
      setError('root', {
        message: error.message || 'Registration failed. Please try again.',
      });
    } finally {
      setIsSubmitting(false);
    }
  };

  /**
   * Handle OAuth registration
   */
  const handleOAuthRegister = (provider: string) => {
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
        <title>Sign Up - DevSocial</title>
        <meta name="description" content="Join DevSocial - Connect with developers worldwide" />
      </Helmet>

      <RegisterContainer>
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5 }}
        >
          <RegisterPaper elevation={0}>
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
              Join the Community
            </Typography>
            
            <Typography
              variant="body2"
              align="center"
              color="text.secondary"
              sx={{ mb: 3 }}
            >
              Create your account and start connecting with developers
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
                onClick={() => handleOAuthRegister('github')}
                sx={{
                  color: '#333',
                  borderColor: '#d0d7de',
                  '&:hover': {
                    backgroundColor: '#f6f8fa',
                    borderColor: '#d0d7de',
                  },
                }}
              >
                Sign up with GitHub
              </OAuthButton>
              
              <OAuthButton
                variant="outlined"
                startIcon={<Google />}
                onClick={() => handleOAuthRegister('google')}
                sx={{
                  color: '#1976d2',
                  borderColor: '#dadce0',
                  '&:hover': {
                    backgroundColor: '#f8f9fa',
                    borderColor: '#dadce0',
                  },
                }}
              >
                Sign up with Google
              </OAuthButton>
            </Stack>

            {/* Divider */}
            <Divider sx={{ my: 2 }}>
              <Typography variant="body2" color="text.secondary">
                or
              </Typography>
            </Divider>

            {/* Registration Form */}
            <Box component="form" onSubmit={handleSubmit(onSubmit)} noValidate>
              {/* Name Fields */}
              <Grid container spacing={2} sx={{ mb: 2 }}>
                <Grid item xs={6}>
                  <TextField
                    {...register('firstName', {
                      required: 'First name is required',
                      minLength: {
                        value: 2,
                        message: 'First name must be at least 2 characters',
                      },
                    })}
                    fullWidth
                    label="First Name"
                    autoComplete="given-name"
                    autoFocus
                    error={!!errors.firstName}
                    helperText={errors.firstName?.message}
                  />
                </Grid>
                <Grid item xs={6}>
                  <TextField
                    {...register('lastName', {
                      required: 'Last name is required',
                      minLength: {
                        value: 2,
                        message: 'Last name must be at least 2 characters',
                      },
                    })}
                    fullWidth
                    label="Last Name"
                    autoComplete="family-name"
                    error={!!errors.lastName}
                    helperText={errors.lastName?.message}
                  />
                </Grid>
              </Grid>

              {/* Email */}
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
                margin="normal"
                error={!!errors.email}
                helperText={errors.email?.message}
                sx={{ mb: 2 }}
              />

              {/* Username */}
              <TextField
                {...register('username', {
                  required: 'Username is required',
                  minLength: {
                    value: 3,
                    message: 'Username must be at least 3 characters',
                  },
                  maxLength: {
                    value: 20,
                    message: 'Username must not exceed 20 characters',
                  },
                  pattern: {
                    value: /^[a-zA-Z0-9_]+$/,
                    message: 'Username can only contain letters, numbers, and underscores',
                  },
                })}
                fullWidth
                label="Username"
                autoComplete="username"
                margin="normal"
                error={!!errors.username}
                helperText={errors.username?.message || 'This will be your unique identifier'}
                sx={{ mb: 2 }}
              />

              {/* Password */}
              <TextField
                {...register('password', {
                  required: 'Password is required',
                  minLength: {
                    value: 8,
                    message: 'Password must be at least 8 characters',
                  },
                  pattern: {
                    value: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)/,
                    message: 'Password must contain at least one uppercase letter, one lowercase letter, and one number',
                  },
                })}
                fullWidth
                label="Password"
                type={showPassword ? 'text' : 'password'}
                autoComplete="new-password"
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
                sx={{ mb: 2 }}
              />

              {/* Confirm Password */}
              <TextField
                {...register('confirmPassword', {
                  required: 'Please confirm your password',
                  validate: (value) => value === password || 'Passwords do not match',
                })}
                fullWidth
                label="Confirm Password"
                type={showConfirmPassword ? 'text' : 'password'}
                autoComplete="new-password"
                margin="normal"
                error={!!errors.confirmPassword}
                helperText={errors.confirmPassword?.message}
                InputProps={{
                  endAdornment: (
                    <InputAdornment position="end">
                      <IconButton
                        onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                        edge="end"
                        aria-label="toggle confirm password visibility"
                      >
                        {showConfirmPassword ? <VisibilityOff /> : <Visibility />}
                      </IconButton>
                    </InputAdornment>
                  ),
                }}
                sx={{ mb: 2 }}
              />

              {/* Terms Acceptance */}
              <FormControlLabel
                control={
                  <Checkbox
                    {...register('acceptTerms', {
                      required: 'You must accept the terms and conditions',
                    })}
                    color="primary"
                  />
                }
                label={
                  <Typography variant="body2" color="text.secondary">
                    I agree to the{' '}
                    <Link
                      to="/terms"
                      style={{ color: '#1976d2', textDecoration: 'none' }}
                      target="_blank"
                    >
                      Terms of Service
                    </Link>{' '}
                    and{' '}
                    <Link
                      to="/privacy"
                      style={{ color: '#1976d2', textDecoration: 'none' }}
                      target="_blank"
                    >
                      Privacy Policy
                    </Link>
                  </Typography>
                }
                sx={{ mb: 2 }}
              />
              
              {errors.acceptTerms && (
                <Typography variant="caption" color="error" sx={{ mb: 2, display: 'block' }}>
                  {errors.acceptTerms.message}
                </Typography>
              )}

              <Button
                type="submit"
                fullWidth
                variant="contained"
                size="large"
                disabled={!isValid || isSubmitting}
                startIcon={<PersonAddOutlined />}
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
                {isSubmitting ? 'Creating Account...' : 'Create Account'}
              </Button>

              <Typography variant="body2" align="center" color="text.secondary">
                Already have an account?{' '}
                <Link
                  to="/login"
                  style={{
                    color: '#1976d2',
                    textDecoration: 'none',
                    fontWeight: 500,
                  }}
                >
                  Sign in here
                </Link>
              </Typography>
            </Box>
          </RegisterPaper>
        </motion.div>
      </RegisterContainer>
    </>
  );
};

export default RegisterPage;