/**
 * Main Application Layout Component
 * 
 * Provides the main layout structure for authenticated users including:
 * - Top navigation bar with user menu
 * - Sidebar navigation with main links
 * - Main content area
 * - Responsive design with mobile support
 */

import React from 'react';
import {
  Box,
  AppBar,
  Toolbar,
  Typography,
  IconButton,
  Avatar,
  Menu,
  MenuItem,
  Drawer,
  List,
  ListItem,
  ListItemButton,
  ListItemIcon,
  ListItemText,
  Divider,
  Badge,
  useTheme,
  useMediaQuery,
  Fab,
} from '@mui/material';
import {
  Menu as MenuIcon,
  Home,
  Person,
  Article,
  Message,
  Settings,
  Logout,
  Notifications,
  Search,
  Add,
  GitHub,
  DarkMode,
  LightMode,
} from '@mui/icons-material';
import { styled } from '@mui/material/styles';
import { useNavigate, useLocation } from 'react-router-dom';

import { useAuth } from '@/context/AuthContext';
import { useAppSelector, useAppDispatch } from '@/store/hooks';
import { selectSidebarOpen, selectDarkMode, toggleSidebar, toggleDarkMode } from '@/store/slices/uiSlice';

/**
 * Layout constants
 */
const DRAWER_WIDTH = 280;
const APPBAR_HEIGHT = 64;

/**
 * Styled components
 */
const StyledAppBar = styled(AppBar)(({ theme }) => ({
  zIndex: theme.zIndex.drawer + 1,
  backgroundColor: theme.palette.background.paper,
  color: theme.palette.text.primary,
  boxShadow: '0 1px 3px rgba(0,0,0,0.1)',
  borderBottom: `1px solid ${theme.palette.divider}`,
}));

const StyledDrawer = styled(Drawer)(({ theme }) => ({
  width: DRAWER_WIDTH,
  flexShrink: 0,
  '& .MuiDrawer-paper': {
    width: DRAWER_WIDTH,
    boxSizing: 'border-box',
    backgroundColor: theme.palette.background.paper,
    borderRight: `1px solid ${theme.palette.divider}`,
  },
}));

const MainContent = styled(Box)(({ theme }) => ({
  flexGrow: 1,
  padding: theme.spacing(3),
  marginTop: APPBAR_HEIGHT,
  minHeight: `calc(100vh - ${APPBAR_HEIGHT}px)`,
  backgroundColor: theme.palette.background.default,
}));

const LogoContainer = styled(Box)({
  display: 'flex',
  alignItems: 'center',
  gap: 8,
  cursor: 'pointer',
});

const CreatePostFab = styled(Fab)(({ theme }) => ({
  position: 'fixed',
  bottom: theme.spacing(2),
  right: theme.spacing(2),
  zIndex: theme.zIndex.speedDial,
}));

/**
 * Navigation items configuration
 */
const navigationItems = [
  { text: 'Dashboard', icon: Home, path: '/dashboard' },
  { text: 'Profile', icon: Person, path: '/profile' },
  { text: 'Posts', icon: Article, path: '/posts' },
  { text: 'Messages', icon: Message, path: '/messages' },
  { text: 'Settings', icon: Settings, path: '/settings' },
];

/**
 * Layout Component Props
 */
interface LayoutProps {
  children: React.ReactNode;
}

/**
 * Main Application Layout Component
 */
const Layout: React.FC<LayoutProps> = ({ children }) => {
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('md'));
  const navigate = useNavigate();
  const location = useLocation();
  
  const { user, logout } = useAuth();
  const dispatch = useAppDispatch();
  const sidebarOpen = useAppSelector(selectSidebarOpen);
  const darkMode = useAppSelector(selectDarkMode);

  const [userMenuAnchor, setUserMenuAnchor] = React.useState<null | HTMLElement>(null);

  /**
   * Handle user menu
   */
  const handleUserMenuOpen = (event: React.MouseEvent<HTMLElement>) => {
    setUserMenuAnchor(event.currentTarget);
  };

  const handleUserMenuClose = () => {
    setUserMenuAnchor(null);
  };

  /**
   * Handle navigation
   */
  const handleNavigation = (path: string) => {
    navigate(path);
    if (isMobile) {
      dispatch(toggleSidebar());
    }
  };

  /**
   * Handle logout
   */
  const handleLogout = async () => {
    handleUserMenuClose();
    await logout();
    navigate('/login');
  };

  /**
   * Handle theme toggle
   */
  const handleThemeToggle = () => {
    dispatch(toggleDarkMode());
  };

  /**
   * Handle sidebar toggle
   */
  const handleSidebarToggle = () => {
    dispatch(toggleSidebar());
  };

  /**
   * Check if current path is active
   */
  const isPathActive = (path: string) => {
    return location.pathname === path || location.pathname.startsWith(path + '/');
  };

  return (
    <Box sx={{ display: 'flex' }}>
      {/* Top Navigation Bar */}
      <StyledAppBar position="fixed">
        <Toolbar sx={{ height: APPBAR_HEIGHT }}>
          {/* Menu Button */}
          <IconButton
            edge="start"
            color="inherit"
            aria-label="menu"
            onClick={handleSidebarToggle}
            sx={{ mr: 2 }}
          >
            <MenuIcon />
          </IconButton>

          {/* Logo */}
          <LogoContainer onClick={() => navigate('/dashboard')} sx={{ flexGrow: 1 }}>
            <Box
              sx={{
                width: 32,
                height: 32,
                borderRadius: '50%',
                background: 'linear-gradient(45deg, #1976d2 30%, #42a5f5 90%)',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
              }}
            >
              <Typography variant="body2" sx={{ color: 'white', fontWeight: 'bold' }}>
                DS
              </Typography>
            </Box>
            <Typography variant="h6" sx={{ fontWeight: 700, color: 'primary.main' }}>
              DevSocial
            </Typography>
          </LogoContainer>

          {/* Search Button */}
          <IconButton color="inherit" sx={{ mr: 1 }}>
            <Search />
          </IconButton>

          {/* Notifications */}
          <IconButton color="inherit" sx={{ mr: 1 }}>
            <Badge badgeContent={3} color="error">
              <Notifications />
            </Badge>
          </IconButton>

          {/* Theme Toggle */}
          <IconButton color="inherit" onClick={handleThemeToggle} sx={{ mr: 1 }}>
            {darkMode ? <LightMode /> : <DarkMode />}
          </IconButton>

          {/* User Menu */}
          <IconButton onClick={handleUserMenuOpen} sx={{ p: 0 }}>
            <Avatar
              alt={user?.firstName}
              src={user?.profilePictureUrl}
              sx={{ width: 32, height: 32 }}
            >
              {user?.firstName?.charAt(0)}
            </Avatar>
          </IconButton>

          <Menu
            anchorEl={userMenuAnchor}
            open={Boolean(userMenuAnchor)}
            onClose={handleUserMenuClose}
            transformOrigin={{ horizontal: 'right', vertical: 'top' }}
            anchorOrigin={{ horizontal: 'right', vertical: 'bottom' }}
          >
            <MenuItem onClick={() => { handleUserMenuClose(); navigate('/profile'); }}>
              <ListItemIcon>
                <Person fontSize="small" />
              </ListItemIcon>
              Profile
            </MenuItem>
            <MenuItem onClick={() => { handleUserMenuClose(); navigate('/settings'); }}>
              <ListItemIcon>
                <Settings fontSize="small" />
              </ListItemIcon>
              Settings
            </MenuItem>
            <Divider />
            <MenuItem onClick={handleLogout}>
              <ListItemIcon>
                <Logout fontSize="small" />
              </ListItemIcon>
              Logout
            </MenuItem>
          </Menu>
        </Toolbar>
      </StyledAppBar>

      {/* Sidebar Navigation */}
      <StyledDrawer
        variant={isMobile ? 'temporary' : 'persistent'}
        open={sidebarOpen}
        onClose={handleSidebarToggle}
      >
        <Toolbar /> {/* Spacer for AppBar */}
        
        <Box sx={{ overflow: 'auto', p: 2 }}>
          {/* User Info */}
          <Box sx={{ mb: 3, textAlign: 'center' }}>
            <Avatar
              src={user?.profilePictureUrl}
              sx={{ width: 64, height: 64, mx: 'auto', mb: 1 }}
            >
              {user?.firstName?.charAt(0)}
            </Avatar>
            <Typography variant="h6" sx={{ fontWeight: 600 }}>
              {user?.firstName} {user?.lastName}
            </Typography>
            <Typography variant="body2" color="text.secondary">
              @{user?.username}
            </Typography>
            {user?.githubUsername && (
              <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center', mt: 1 }}>
                <GitHub sx={{ fontSize: 16, mr: 0.5 }} />
                <Typography variant="caption" color="text.secondary">
                  {user.githubUsername}
                </Typography>
              </Box>
            )}
          </Box>

          <Divider sx={{ mb: 2 }} />

          {/* Navigation Items */}
          <List>
            {navigationItems.map((item) => {
              const Icon = item.icon;
              const isActive = isPathActive(item.path);
              
              return (
                <ListItem key={item.text} disablePadding>
                  <ListItemButton
                    onClick={() => handleNavigation(item.path)}
                    selected={isActive}
                    sx={{
                      borderRadius: 2,
                      mb: 0.5,
                      '&.Mui-selected': {
                        backgroundColor: 'primary.main',
                        color: 'primary.contrastText',
                        '&:hover': {
                          backgroundColor: 'primary.dark',
                        },
                        '& .MuiListItemIcon-root': {
                          color: 'primary.contrastText',
                        },
                      },
                    }}
                  >
                    <ListItemIcon>
                      <Icon />
                    </ListItemIcon>
                    <ListItemText primary={item.text} />
                  </ListItemButton>
                </ListItem>
              );
            })}
          </List>
        </Box>
      </StyledDrawer>

      {/* Main Content Area */}
      <MainContent
        component="main"
        sx={{
          marginLeft: isMobile ? 0 : sidebarOpen ? `${DRAWER_WIDTH}px` : 0,
          transition: theme.transitions.create(['margin'], {
            easing: theme.transitions.easing.sharp,
            duration: theme.transitions.duration.leavingScreen,
          }),
        }}
      >
        {children}
      </MainContent>

      {/* Floating Action Button for Creating Posts */}
      <CreatePostFab
        color="primary"
        aria-label="create post"
        onClick={() => navigate('/create-post')}
      >
        <Add />
      </CreatePostFab>
    </Box>
  );
};

export default Layout;