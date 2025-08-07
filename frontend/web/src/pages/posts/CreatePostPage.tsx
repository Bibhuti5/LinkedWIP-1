/**
 * Create Post Page Component - Placeholder
 */

import React from 'react';
import { Container, Typography, Card, CardContent, Box, Button } from '@mui/material';
import { Add, VideoCall, Image, Code } from '@mui/icons-material';
import { Helmet } from 'react-helmet-async';

const CreatePostPage: React.FC = () => {
  return (
    <>
      <Helmet>
        <title>Create Post - DevSocial</title>
      </Helmet>
      
      <Container maxWidth="md">
        <Box sx={{ mb: 3 }}>
          <Typography variant="h4" sx={{ fontWeight: 600, mb: 1 }}>
            Create New Post
          </Typography>
          <Typography variant="body1" color="text.secondary">
            Share your projects, tutorials, and insights with the community
          </Typography>
        </Box>

        <Card>
          <CardContent sx={{ textAlign: 'center', py: 6 }}>
            <Add sx={{ fontSize: 80, color: 'primary.main', mb: 2 }} />
            <Typography variant="h5" sx={{ fontWeight: 600, mb: 2 }}>
              Post Creation Coming Soon
            </Typography>
            <Typography variant="body1" sx={{ mb: 3, maxWidth: 500, mx: 'auto' }}>
              🚧 The post creation interface will support various content types:
            </Typography>

            <Box sx={{ display: 'flex', gap: 2, justifyContent: 'center', flexWrap: 'wrap', mb: 3 }}>
              <Button
                variant="outlined"
                startIcon={<VideoCall />}
                disabled
              >
                Video Posts
              </Button>
              <Button
                variant="outlined"
                startIcon={<Image />}
                disabled
              >
                Image Posts
              </Button>
              <Button
                variant="outlined"
                startIcon={<Code />}
                disabled
              >
                Code Snippets
              </Button>
            </Box>

            <Typography variant="body2" color="text.secondary">
              Rich text editor, media uploads, and project showcases will be available soon.
            </Typography>
          </CardContent>
        </Card>
      </Container>
    </>
  );
};

export default CreatePostPage;