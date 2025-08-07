/**
 * Dashboard Page Component
 * 
 * Main dashboard with:
 * - Welcome message and quick stats
 * - Activity feed with posts
 * - Trending content sidebar
 * - Quick actions
 * - Recent notifications
 */

import React, { useEffect } from 'react';
import {
  Box,
  Grid,
  Card,
  CardContent,
  Typography,
  Avatar,
  Chip,
  Button,
  List,
  ListItem,
  ListItemAvatar,
  ListItemText,
  Divider,
  Paper,
  LinearProgress,
  IconButton,
} from '@mui/material';
import {
  TrendingUp,
  People,
  Article,
  Message,
  Add,
  GitHub,
  Favorite,
  Comment,
  Share,
  MoreVert,
} from '@mui/icons-material';
import { styled } from '@mui/material/styles';
import { motion } from 'framer-motion';
import { Helmet } from 'react-helmet-async';
import { useNavigate } from 'react-router-dom';

import { useAuth } from '@/context/AuthContext';

/**
 * Styled components
 */
const StatsCard = styled(Card)(({ theme }) => ({
  background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
  color: 'white',
  '& .MuiCardContent-root': {
    padding: theme.spacing(2),
  },
}));

const ActivityCard = styled(Card)(({ theme }) => ({
  marginBottom: theme.spacing(2),
  '&:hover': {
    boxShadow: theme.shadows[4],
    transform: 'translateY(-2px)',
    transition: 'all 0.2s ease-in-out',
  },
}));

const TrendingChip = styled(Chip)(({ theme }) => ({
  background: 'linear-gradient(45deg, #ff6b6b, #ee5a52)',
  color: 'white',
  fontWeight: 600,
  '& .MuiChip-icon': {
    color: 'white',
  },
}));

/**
 * Mock data for demonstration
 */
const mockStats = [
  { label: 'Followers', value: 1234, icon: People, color: '#4caf50' },
  { label: 'Posts', value: 56, icon: Article, color: '#2196f3' },
  { label: 'Messages', value: 89, icon: Message, color: '#ff9800' },
  { label: 'Views', value: 12500, icon: TrendingUp, color: '#9c27b0' },
];

const mockPosts = [
  {
    id: 1,
    author: {
      name: 'Sarah Chen',
      username: 'sarahdev',
      avatar: '',
    },
    content: 'Just deployed my new React microservices architecture! 🚀 Built with TypeScript, Docker, and Kubernetes. The performance improvements are incredible!',
    timestamp: '2 hours ago',
    likes: 24,
    comments: 8,
    shares: 3,
    tags: ['React', 'TypeScript', 'Microservices'],
    hasImage: true,
  },
  {
    id: 2,
    author: {
      name: 'Alex Kumar',
      username: 'alexcodes',
      avatar: '',
    },
    content: 'Working on an open-source GraphQL API with real-time subscriptions. Anyone interested in contributing?',
    timestamp: '4 hours ago',
    likes: 18,
    comments: 12,
    shares: 5,
    tags: ['GraphQL', 'Open Source', 'API'],
    hasImage: false,
  },
  {
    id: 3,
    author: {
      name: 'Maria Rodriguez',
      username: 'mariatech',
      avatar: '',
    },
    content: 'Sharing my experience migrating from monolith to microservices. Blog post with detailed architecture diagrams coming soon!',
    timestamp: '6 hours ago',
    likes: 31,
    comments: 15,
    shares: 8,
    tags: ['Architecture', 'Microservices', 'DevOps'],
    hasImage: true,
  },
];

const mockTrending = [
  { title: 'React 18 Best Practices', engagement: 156 },
  { title: 'Kubernetes Security Guide', engagement: 143 },
  { title: 'TypeScript Advanced Types', engagement: 128 },
  { title: 'Docker Multi-stage Builds', engagement: 97 },
  { title: 'GraphQL Performance Tips', engagement: 89 },
];

/**
 * Dashboard Page Component
 */
const DashboardPage: React.FC = () => {
  const { user } = useAuth();
  const navigate = useNavigate();

  /**
   * Format number with K/M suffix
   */
  const formatNumber = (num: number) => {
    if (num >= 1000000) return (num / 1000000).toFixed(1) + 'M';
    if (num >= 1000) return (num / 1000).toFixed(1) + 'K';
    return num.toString();
  };

  return (
    <>
      <Helmet>
        <title>Dashboard - DevSocial</title>
        <meta name="description" content="Your developer community dashboard" />
      </Helmet>

      <Box>
        {/* Welcome Section */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5 }}
        >
          <Paper sx={{ p: 3, mb: 3, background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)', color: 'white' }}>
            <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
              <Avatar
                src={user?.profilePictureUrl}
                sx={{ width: 60, height: 60, mr: 2 }}
              >
                {user?.firstName?.charAt(0)}
              </Avatar>
              <Box>
                <Typography variant="h4" sx={{ fontWeight: 700, mb: 0.5 }}>
                  Welcome back, {user?.firstName}! 👋
                </Typography>
                <Typography variant="body1" sx={{ opacity: 0.9 }}>
                  Ready to connect with the developer community?
                </Typography>
              </Box>
            </Box>
            
            <Box sx={{ display: 'flex', gap: 2, flexWrap: 'wrap' }}>
              <Button
                variant="contained"
                startIcon={<Add />}
                onClick={() => navigate('/create-post')}
                sx={{
                  bgcolor: 'rgba(255,255,255,0.2)',
                  '&:hover': { bgcolor: 'rgba(255,255,255,0.3)' },
                }}
              >
                Create Post
              </Button>
              <Button
                variant="outlined"
                startIcon={<GitHub />}
                sx={{
                  color: 'white',
                  borderColor: 'rgba(255,255,255,0.5)',
                  '&:hover': { borderColor: 'white', bgcolor: 'rgba(255,255,255,0.1)' },
                }}
              >
                Sync GitHub
              </Button>
            </Box>
          </Paper>
        </motion.div>

        <Grid container spacing={3}>
          {/* Main Content */}
          <Grid item xs={12} md={8}>
            {/* Quick Stats */}
            <Grid container spacing={2} sx={{ mb: 3 }}>
              {mockStats.map((stat, index) => {
                const Icon = stat.icon;
                return (
                  <Grid item xs={6} md={3} key={stat.label}>
                    <motion.div
                      initial={{ opacity: 0, y: 20 }}
                      animate={{ opacity: 1, y: 0 }}
                      transition={{ duration: 0.5, delay: index * 0.1 }}
                    >
                      <Card>
                        <CardContent>
                          <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
                            <Icon sx={{ color: stat.color, mr: 1 }} />
                            <Typography variant="h6" sx={{ fontWeight: 600 }}>
                              {formatNumber(stat.value)}
                            </Typography>
                          </Box>
                          <Typography variant="body2" color="text.secondary">
                            {stat.label}
                          </Typography>
                        </CardContent>
                      </Card>
                    </motion.div>
                  </Grid>
                );
              })}
            </Grid>

            {/* Activity Feed */}
            <Typography variant="h5" sx={{ mb: 2, fontWeight: 600 }}>
              Recent Activity
            </Typography>

            {mockPosts.map((post, index) => (
              <motion.div
                key={post.id}
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ duration: 0.5, delay: index * 0.1 }}
              >
                <ActivityCard>
                  <CardContent>
                    {/* Post Header */}
                    <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                      <Avatar src={post.author.avatar} sx={{ mr: 2 }}>
                        {post.author.name.charAt(0)}
                      </Avatar>
                      <Box sx={{ flexGrow: 1 }}>
                        <Typography variant="subtitle1" sx={{ fontWeight: 600 }}>
                          {post.author.name}
                        </Typography>
                        <Typography variant="body2" color="text.secondary">
                          @{post.author.username} • {post.timestamp}
                        </Typography>
                      </Box>
                      <IconButton>
                        <MoreVert />
                      </IconButton>
                    </Box>

                    {/* Post Content */}
                    <Typography variant="body1" sx={{ mb: 2, lineHeight: 1.6 }}>
                      {post.content}
                    </Typography>

                    {/* Post Image Placeholder */}
                    {post.hasImage && (
                      <Box
                        sx={{
                          height: 200,
                          bgcolor: 'grey.100',
                          borderRadius: 2,
                          mb: 2,
                          display: 'flex',
                          alignItems: 'center',
                          justifyContent: 'center',
                        }}
                      >
                        <Typography color="text.secondary">
                          📷 Project Screenshot
                        </Typography>
                      </Box>
                    )}

                    {/* Tags */}
                    <Box sx={{ mb: 2 }}>
                      {post.tags.map((tag) => (
                        <Chip
                          key={tag}
                          label={tag}
                          size="small"
                          sx={{ mr: 1, mb: 1 }}
                        />
                      ))}
                    </Box>

                    {/* Post Actions */}
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                      <Button
                        startIcon={<Favorite />}
                        size="small"
                        sx={{ color: 'text.secondary' }}
                      >
                        {post.likes}
                      </Button>
                      <Button
                        startIcon={<Comment />}
                        size="small"
                        sx={{ color: 'text.secondary' }}
                      >
                        {post.comments}
                      </Button>
                      <Button
                        startIcon={<Share />}
                        size="small"
                        sx={{ color: 'text.secondary' }}
                      >
                        {post.shares}
                      </Button>
                    </Box>
                  </CardContent>
                </ActivityCard>
              </motion.div>
            ))}
          </Grid>

          {/* Sidebar */}
          <Grid item xs={12} md={4}>
            {/* Trending Topics */}
            <Card sx={{ mb: 3 }}>
              <CardContent>
                <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                  <TrendingChip
                    icon={<TrendingUp />}
                    label="Trending"
                    size="small"
                  />
                </Box>
                
                <List dense>
                  {mockTrending.map((item, index) => (
                    <React.Fragment key={index}>
                      <ListItem
                        sx={{
                          px: 0,
                          cursor: 'pointer',
                          '&:hover': { bgcolor: 'action.hover' },
                        }}
                      >
                        <ListItemText
                          primary={item.title}
                          secondary={`${item.engagement} interactions`}
                        />
                      </ListItem>
                      {index < mockTrending.length - 1 && <Divider />}
                    </React.Fragment>
                  ))}
                </List>
              </CardContent>
            </Card>

            {/* GitHub Activity */}
            <Card sx={{ mb: 3 }}>
              <CardContent>
                <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                  <GitHub sx={{ mr: 1 }} />
                  <Typography variant="h6" sx={{ fontWeight: 600 }}>
                    GitHub Activity
                  </Typography>
                </Box>
                
                <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
                  Connect your GitHub account to see your latest contributions
                </Typography>
                
                <Button
                  variant="outlined"
                  fullWidth
                  startIcon={<GitHub />}
                  onClick={() => navigate('/settings')}
                >
                  Connect GitHub
                </Button>
              </CardContent>
            </Card>

            {/* Quick Actions */}
            <Card>
              <CardContent>
                <Typography variant="h6" sx={{ mb: 2, fontWeight: 600 }}>
                  Quick Actions
                </Typography>
                
                <Box sx={{ display: 'flex', flexDirection: 'column', gap: 1 }}>
                  <Button
                    variant="outlined"
                    fullWidth
                    onClick={() => navigate('/create-post')}
                  >
                    Create New Post
                  </Button>
                  <Button
                    variant="outlined"
                    fullWidth
                    onClick={() => navigate('/messages')}
                  >
                    Send Message
                  </Button>
                  <Button
                    variant="outlined"
                    fullWidth
                    onClick={() => navigate('/profile')}
                  >
                    Edit Profile
                  </Button>
                </Box>
              </CardContent>
            </Card>
          </Grid>
        </Grid>
      </Box>
    </>
  );
};

export default DashboardPage;