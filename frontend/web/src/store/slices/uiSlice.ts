/**
 * UI Redux Slice
 * 
 * Manages UI state including:
 * - Theme preferences
 * - Sidebar state
 * - Modal states
 * - Loading states
 * - Notification preferences
 */

import { createSlice, PayloadAction } from '@reduxjs/toolkit';

export interface UIState {
  // Theme
  darkMode: boolean;
  
  // Layout
  sidebarOpen: boolean;
  sidebarCollapsed: boolean;
  
  // Modals
  modals: {
    createPost: boolean;
    userProfile: boolean;
    settings: boolean;
  };
  
  // Loading states
  globalLoading: boolean;
  
  // Notifications
  notifications: {
    sound: boolean;
    desktop: boolean;
    email: boolean;
  };
  
  // Preferences
  language: string;
  timezone: string;
}

const initialState: UIState = {
  darkMode: false,
  sidebarOpen: true,
  sidebarCollapsed: false,
  modals: {
    createPost: false,
    userProfile: false,
    settings: false,
  },
  globalLoading: false,
  notifications: {
    sound: true,
    desktop: true,
    email: true,
  },
  language: 'en',
  timezone: Intl.DateTimeFormat().resolvedOptions().timeZone,
};

const uiSlice = createSlice({
  name: 'ui',
  initialState,
  reducers: {
    toggleDarkMode: (state) => {
      state.darkMode = !state.darkMode;
    },
    
    setDarkMode: (state, action: PayloadAction<boolean>) => {
      state.darkMode = action.payload;
    },
    
    toggleSidebar: (state) => {
      state.sidebarOpen = !state.sidebarOpen;
    },
    
    setSidebarOpen: (state, action: PayloadAction<boolean>) => {
      state.sidebarOpen = action.payload;
    },
    
    toggleSidebarCollapsed: (state) => {
      state.sidebarCollapsed = !state.sidebarCollapsed;
    },
    
    openModal: (state, action: PayloadAction<keyof UIState['modals']>) => {
      state.modals[action.payload] = true;
    },
    
    closeModal: (state, action: PayloadAction<keyof UIState['modals']>) => {
      state.modals[action.payload] = false;
    },
    
    closeAllModals: (state) => {
      Object.keys(state.modals).forEach((key) => {
        state.modals[key as keyof UIState['modals']] = false;
      });
    },
    
    setGlobalLoading: (state, action: PayloadAction<boolean>) => {
      state.globalLoading = action.payload;
    },
    
    updateNotificationSettings: (state, action: PayloadAction<Partial<UIState['notifications']>>) => {
      state.notifications = { ...state.notifications, ...action.payload };
    },
    
    setLanguage: (state, action: PayloadAction<string>) => {
      state.language = action.payload;
    },
    
    setTimezone: (state, action: PayloadAction<string>) => {
      state.timezone = action.payload;
    },
  },
});

export const {
  toggleDarkMode,
  setDarkMode,
  toggleSidebar,
  setSidebarOpen,
  toggleSidebarCollapsed,
  openModal,
  closeModal,
  closeAllModals,
  setGlobalLoading,
  updateNotificationSettings,
  setLanguage,
  setTimezone,
} = uiSlice.actions;

// Selectors
export const selectUI = (state: { ui: UIState }) => state.ui;
export const selectDarkMode = (state: { ui: UIState }) => state.ui.darkMode;
export const selectSidebarOpen = (state: { ui: UIState }) => state.ui.sidebarOpen;
export const selectModals = (state: { ui: UIState }) => state.ui.modals;
export const selectGlobalLoading = (state: { ui: UIState }) => state.ui.globalLoading;

export default uiSlice.reducer;