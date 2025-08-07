/**
 * Post Detail Page Component - Placeholder
 */

import React from 'react';
import { Container, Typography, Card, CardContent, Box } from '@mui/material';
import { Article } from '@mui/icons-material';
import { Helmet } from 'react-helmet-async';

const PostDetailPage: React.FC = () => {
  return (
    <>
      <Helmet>
        <title>Post Details - DevSocial</title>
      </Helmet>
      
      <Container maxWidth="md">
        <Card>
          <CardContent sx={{ textAlign: 'center', py: 6 }}>
            <Article sx={{ fontSize: 80, color: 'primary.main', mb: 2 }} />
            <Typography variant="h5" sx={{ fontWeight: 600, mb: 2 }}>
              Post Detail View Coming Soon
            </Typography>
            <Typography variant="body1" color="text.secondary">
              🚧 Individual post viewing and interaction features are being developed.
            </Typography>
          </CardContent>
        </Card>
      </Container>
    </>
  );
};

export default PostDetailPage;