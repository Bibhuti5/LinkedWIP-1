/**
 * Profile Page Component - Placeholder
 */

import React from 'react';
import { Container, Typography, Card, CardContent, Box, Button } from '@mui/material';
import { Person, Edit } from '@mui/icons-material';
import { Helmet } from 'react-helmet-async';
import { useAuth } from '@/context/AuthContext';

const ProfilePage: React.FC = () => {
  const { user } = useAuth();

  return (
    <>
      <Helmet>
        <title>Profile - DevSocial</title>
      </Helmet>
      
      <Container maxWidth="md">
        <Box sx={{ mb: 3 }}>
          <Typography variant="h4" sx={{ fontWeight: 600, mb: 1 }}>
            Profile
          </Typography>
          <Typography variant="body1" color="text.secondary">
            Manage your profile and account settings
          </Typography>
        </Box>

        <Card>
          <CardContent>
            <Box sx={{ display: 'flex', alignItems: 'center', mb: 3 }}>
              <Person sx={{ fontSize: 60, mr: 2, color: 'primary.main' }} />
              <Box>
                <Typography variant="h5" sx={{ fontWeight: 600 }}>
                  {user?.firstName} {user?.lastName}
                </Typography>
                <Typography variant="body1" color="text.secondary">
                  @{user?.username}
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  {user?.email}
                </Typography>
              </Box>
            </Box>

            <Typography variant="body1" sx={{ mb: 3 }}>
              🚧 Profile management features coming soon! This will include:
            </Typography>

            <Box component="ul" sx={{ mb: 3 }}>
              <li>Edit profile information</li>
              <li>Upload profile picture</li>
              <li>GitHub integration</li>
              <li>Skills and experience</li>
              <li>Social connections</li>
            </Box>

            <Button
              variant="contained"
              startIcon={<Edit />}
              disabled
            >
              Edit Profile (Coming Soon)
            </Button>
          </CardContent>
        </Card>
      </Container>
    </>
  );
};

export default ProfilePage;