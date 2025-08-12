/**
 * Loading Screen Component
 * 
 * Full-screen loading indicator with DevSocial branding
 * Used during app initialization and major state transitions
 */

import React from 'react';
import { Box, CircularProgress, Typography, LinearProgress } from '@mui/material';
import { styled, keyframes } from '@mui/material/styles';

/**
 * Pulse animation for the logo
 */
const pulse = keyframes`
  0% {
    opacity: 1;
    transform: scale(1);
  }
  50% {
    opacity: 0.7;
    transform: scale(1.05);
  }
  100% {
    opacity: 1;
    transform: scale(1);
  }
`;

/**
 * Styled components
 */
const LoadingContainer = styled(Box)(({ theme }) => ({
  position: 'fixed',
  top: 0,
  left: 0,
  width: '100%',
  height: '100%',
  display: 'flex',
  flexDirection: 'column',
  justifyContent: 'center',
  alignItems: 'center',
  backgroundColor: theme.palette.background.default,
  zIndex: 9999,
}));

const LogoContainer = styled(Box)({
  display: 'flex',
  alignItems: 'center',
  marginBottom: '2rem',
  animation: `${pulse} 2s ease-in-out infinite`,
});

const ProgressContainer = styled(Box)({
  width: '200px',
  marginTop: '1rem',
});

/**
 * Loading Screen Component Props
 */
interface LoadingScreenProps {
  message?: string;
  showProgress?: boolean;
  progress?: number;
}

/**
 * Loading Screen Component
 * 
 * Displays a full-screen loading indicator with optional progress bar
 * and custom loading message.
 * 
 * @param message - Custom loading message
 * @param showProgress - Whether to show progress bar
 * @param progress - Progress value (0-100)
 */
const LoadingScreen: React.FC<LoadingScreenProps> = ({
  message = 'Loading DevSocial...',
  showProgress = false,
  progress = 0,
}) => {
  return (
    <LoadingContainer>
      <LogoContainer>
        {/* DevSocial Logo */}
        <Box
          sx={{
            width: 60,
            height: 60,
            borderRadius: '50%',
            background: 'linear-gradient(45deg, #1976d2 30%, #42a5f5 90%)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            marginRight: 2,
            boxShadow: '0 4px 20px rgba(25, 118, 210, 0.3)',
          }}
        >
          <Typography
            variant="h4"
            sx={{
              color: 'white',
              fontWeight: 'bold',
              fontFamily: '"Inter", sans-serif',
            }}
          >
            DS
          </Typography>
        </Box>
        
        <Typography
          variant="h3"
          sx={{
            fontWeight: 700,
            background: 'linear-gradient(45deg, #1976d2 30%, #42a5f5 90%)',
            backgroundClip: 'text',
            WebkitBackgroundClip: 'text',
            WebkitTextFillColor: 'transparent',
            fontFamily: '"Inter", sans-serif',
          }}
        >
          DevSocial
        </Typography>
      </LogoContainer>

      {/* Loading Spinner */}
      <CircularProgress
        size={40}
        thickness={4}
        sx={{
          color: 'primary.main',
          marginBottom: 2,
        }}
      />

      {/* Loading Message */}
      <Typography
        variant="body1"
        color="text.secondary"
        sx={{
          marginBottom: 2,
          textAlign: 'center',
          maxWidth: '300px',
        }}
      >
        {message}
      </Typography>

      {/* Progress Bar */}
      {showProgress && (
        <ProgressContainer>
          <LinearProgress
            variant="determinate"
            value={progress}
            sx={{
              height: 4,
              borderRadius: 2,
              backgroundColor: 'rgba(25, 118, 210, 0.1)',
              '& .MuiLinearProgress-bar': {
                borderRadius: 2,
                background: 'linear-gradient(45deg, #1976d2 30%, #42a5f5 90%)',
              },
            }}
          />
          <Typography
            variant="caption"
            color="text.secondary"
            sx={{
              display: 'block',
              textAlign: 'center',
              marginTop: 1,
            }}
          >
            {Math.round(progress)}%
          </Typography>
        </ProgressContainer>
      )}

      {/* Subtitle */}
      <Typography
        variant="caption"
        color="text.secondary"
        sx={{
          position: 'absolute',
          bottom: '2rem',
          textAlign: 'center',
          opacity: 0.7,
        }}
      >
        Connecting developers worldwide
      </Typography>
    </LoadingContainer>
  );
};

export default LoadingScreen;