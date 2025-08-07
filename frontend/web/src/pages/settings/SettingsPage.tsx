/**
 * Settings Page Component - Placeholder
 */

import React from 'react';
import { Container, Typography, Card, CardContent, Box, Button, Switch, FormControlLabel } from '@mui/material';
import { Settings, GitHub, Notifications, Security } from '@mui/icons-material';
import { Helmet } from 'react-helmet-async';
import { useAppDispatch, useAppSelector } from '@/store/hooks';
import { selectDarkMode, toggleDarkMode } from '@/store/slices/uiSlice';

const SettingsPage: React.FC = () => {
  const dispatch = useAppDispatch();
  const darkMode = useAppSelector(selectDarkMode);

  return (
    <>
      <Helmet>
        <title>Settings - DevSocial</title>
      </Helmet>
      
      <Container maxWidth="md">
        <Box sx={{ mb: 3 }}>
          <Typography variant="h4" sx={{ fontWeight: 600, mb: 1 }}>
            Settings
          </Typography>
          <Typography variant="body1" color="text.secondary">
            Customize your DevSocial experience
          </Typography>
        </Box>

        {/* Theme Settings */}
        <Card sx={{ mb: 3 }}>
          <CardContent>
            <Typography variant="h6" sx={{ fontWeight: 600, mb: 2 }}>
              Appearance
            </Typography>
            <FormControlLabel
              control={
                <Switch
                  checked={darkMode}
                  onChange={() => dispatch(toggleDarkMode())}
                />
              }
              label="Dark Mode"
            />
            <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
              Toggle between light and dark themes
            </Typography>
          </CardContent>
        </Card>

        {/* GitHub Integration */}
        <Card sx={{ mb: 3 }}>
          <CardContent>
            <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
              <GitHub sx={{ mr: 1 }} />
              <Typography variant="h6" sx={{ fontWeight: 600 }}>
                GitHub Integration
              </Typography>
            </Box>
            <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
              Connect your GitHub account to showcase your repositories and contributions
            </Typography>
            <Button
              variant="outlined"
              startIcon={<GitHub />}
              disabled
            >
              Connect GitHub (Coming Soon)
            </Button>
          </CardContent>
        </Card>

        {/* Notifications */}
        <Card sx={{ mb: 3 }}>
          <CardContent>
            <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
              <Notifications sx={{ mr: 1 }} />
              <Typography variant="h6" sx={{ fontWeight: 600 }}>
                Notifications
              </Typography>
            </Box>
            <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
              🚧 Notification preferences will be available soon:
            </Typography>
            <Box component="ul" sx={{ mb: 2, pl: 2 }}>
              <li>Email notifications</li>
              <li>Push notifications</li>
              <li>Message alerts</li>
              <li>Post interactions</li>
              <li>Following updates</li>
            </Box>
            <Button
              variant="outlined"
              startIcon={<Notifications />}
              disabled
            >
              Configure Notifications (Coming Soon)
            </Button>
          </CardContent>
        </Card>

        {/* Security */}
        <Card>
          <CardContent>
            <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
              <Security sx={{ mr: 1 }} />
              <Typography variant="h6" sx={{ fontWeight: 600 }}>
                Security & Privacy
              </Typography>
            </Box>
            <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
              🚧 Security features coming soon:
            </Typography>
            <Box component="ul" sx={{ mb: 2, pl: 2 }}>
              <li>Change password</li>
              <li>Two-factor authentication</li>
              <li>Privacy settings</li>
              <li>Account deletion</li>
              <li>Data export</li>
            </Box>
            <Button
              variant="outlined"
              startIcon={<Security />}
              disabled
            >
              Security Settings (Coming Soon)
            </Button>
          </CardContent>
        </Card>
      </Container>
    </>
  );
};

export default SettingsPage;