/**
 * User Redux Slice
 * 
 * Manages user-related state that's separate from auth:
 * - User profiles
 * - Following/followers
 * - User preferences
 * - Social connections
 */

import { createSlice, PayloadAction } from '@reduxjs/toolkit';

export interface UserProfile {
  id: number;
  username: string;
  firstName: string;
  lastName: string;
  email: string;
  profilePictureUrl?: string;
  bio?: string;
  location?: string;
  website?: string;
  githubUsername?: string;
  followersCount: number;
  followingCount: number;
  postsCount: number;
  isFollowing?: boolean;
  isVerified: boolean;
  createdAt: string;
}

export interface UserState {
  profiles: Record<number, UserProfile>;
  following: number[];
  followers: number[];
  suggestions: number[];
  isLoading: boolean;
  error: string | null;
}

const initialState: UserState = {
  profiles: {},
  following: [],
  followers: [],
  suggestions: [],
  isLoading: false,
  error: null,
};

const userSlice = createSlice({
  name: 'user',
  initialState,
  reducers: {
    setLoading: (state, action: PayloadAction<boolean>) => {
      state.isLoading = action.payload;
    },
    
    setError: (state, action: PayloadAction<string | null>) => {
      state.error = action.payload;
    },
    
    addUserProfile: (state, action: PayloadAction<UserProfile>) => {
      state.profiles[action.payload.id] = action.payload;
    },
    
    addUserProfiles: (state, action: PayloadAction<UserProfile[]>) => {
      action.payload.forEach((profile) => {
        state.profiles[profile.id] = profile;
      });
    },
    
    updateUserProfile: (state, action: PayloadAction<{ id: number; data: Partial<UserProfile> }>) => {
      const { id, data } = action.payload;
      if (state.profiles[id]) {
        state.profiles[id] = { ...state.profiles[id], ...data };
      }
    },
    
    setFollowing: (state, action: PayloadAction<number[]>) => {
      state.following = action.payload;
    },
    
    setFollowers: (state, action: PayloadAction<number[]>) => {
      state.followers = action.payload;
    },
    
    addFollowing: (state, action: PayloadAction<number>) => {
      if (!state.following.includes(action.payload)) {
        state.following.push(action.payload);
      }
      // Update profile if it exists
      if (state.profiles[action.payload]) {
        state.profiles[action.payload].isFollowing = true;
      }
    },
    
    removeFollowing: (state, action: PayloadAction<number>) => {
      state.following = state.following.filter(id => id !== action.payload);
      // Update profile if it exists
      if (state.profiles[action.payload]) {
        state.profiles[action.payload].isFollowing = false;
      }
    },
    
    setSuggestions: (state, action: PayloadAction<number[]>) => {
      state.suggestions = action.payload;
    },
    
    clearUserData: (state) => {
      state.profiles = {};
      state.following = [];
      state.followers = [];
      state.suggestions = [];
      state.error = null;
    },
  },
});

export const {
  setLoading,
  setError,
  addUserProfile,
  addUserProfiles,
  updateUserProfile,
  setFollowing,
  setFollowers,
  addFollowing,
  removeFollowing,
  setSuggestions,
  clearUserData,
} = userSlice.actions;

// Selectors
export const selectUserState = (state: { user: UserState }) => state.user;
export const selectUserProfiles = (state: { user: UserState }) => state.user.profiles;
export const selectUserProfile = (id: number) => (state: { user: UserState }) => state.user.profiles[id];
export const selectFollowing = (state: { user: UserState }) => state.user.following;
export const selectFollowers = (state: { user: UserState }) => state.user.followers;
export const selectSuggestions = (state: { user: UserState }) => state.user.suggestions;
export const selectUserLoading = (state: { user: UserState }) => state.user.isLoading;
export const selectUserError = (state: { user: UserState }) => state.user.error;

export default userSlice.reducer;