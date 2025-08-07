/**
 * Messages Page Component - Placeholder
 */

import React from 'react';
import { Container, Typography, Card, CardContent, Box, Button } from '@mui/material';
import { Message, Chat, VideoCall } from '@mui/icons-material';
import { Helmet } from 'react-helmet-async';

const MessagesPage: React.FC = () => {
  return (
    <>
      <Helmet>
        <title>Messages - DevSocial</title>
      </Helmet>
      
      <Container maxWidth="lg">
        <Box sx={{ mb: 3 }}>
          <Typography variant="h4" sx={{ fontWeight: 600, mb: 1 }}>
            Messages
          </Typography>
          <Typography variant="body1" color="text.secondary">
            Connect and communicate with fellow developers
          </Typography>
        </Box>

        <Card>
          <CardContent sx={{ textAlign: 'center', py: 6 }}>
            <Message sx={{ fontSize: 80, color: 'primary.main', mb: 2 }} />
            <Typography variant="h5" sx={{ fontWeight: 600, mb: 2 }}>
              Real-time Messaging Coming Soon
            </Typography>
            <Typography variant="body1" sx={{ mb: 3, maxWidth: 500, mx: 'auto' }}>
              🚧 The messaging system will include powerful communication features:
            </Typography>

            <Box component="ul" sx={{ mb: 3, textAlign: 'left', maxWidth: 400, mx: 'auto' }}>
              <li>Real-time chat with WebSocket</li>
              <li>Direct messages and group chats</li>
              <li>File and code sharing</li>
              <li>Message threading and reactions</li>
              <li>Online presence indicators</li>
              <li>Push notifications</li>
            </Box>

            <Box sx={{ display: 'flex', gap: 2, justifyContent: 'center', flexWrap: 'wrap' }}>
              <Button
                variant="contained"
                startIcon={<Chat />}
                disabled
              >
                Start Chat (Coming Soon)
              </Button>
              <Button
                variant="outlined"
                startIcon={<VideoCall />}
                disabled
              >
                Video Call (Future)
              </Button>
            </Box>
          </CardContent>
        </Card>
      </Container>
    </>
  );
};

export default MessagesPage;