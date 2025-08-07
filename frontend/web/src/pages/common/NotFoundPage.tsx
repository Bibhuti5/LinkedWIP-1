/**
 * 404 Not Found Page Component
 */

import React from 'react';
import { Box, Typography, Button, Container } from '@mui/material';
import { Home, ArrowBack } from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
import { Helmet } from 'react-helmet-async';

const NotFoundPage: React.FC = () => {
  const navigate = useNavigate();

  return (
    <>
      <Helmet>
        <title>Page Not Found - DevSocial</title>
      </Helmet>
      
      <Container maxWidth="sm">
        <Box
          sx={{
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
            justifyContent: 'center',
            minHeight: '100vh',
            textAlign: 'center',
          }}
        >
          <Typography variant="h1" sx={{ fontSize: '8rem', fontWeight: 'bold', mb: 2 }}>
            404
          </Typography>
          <Typography variant="h4" sx={{ mb: 2 }}>
            Page Not Found
          </Typography>
          <Typography variant="body1" color="text.secondary" sx={{ mb: 4 }}>
            The page you're looking for doesn't exist or has been moved.
          </Typography>
          <Box sx={{ display: 'flex', gap: 2 }}>
            <Button
              variant="contained"
              startIcon={<Home />}
              onClick={() => navigate('/dashboard')}
            >
              Go Home
            </Button>
            <Button
              variant="outlined"
              startIcon={<ArrowBack />}
              onClick={() => navigate(-1)}
            >
              Go Back
            </Button>
          </Box>
        </Box>
      </Container>
    </>
  );
};

export default NotFoundPage;