/**
 * Redux Store Configuration
 * 
 * Centralized state management using Redux Toolkit with:
 * - Authentication state
 * - User preferences
 * - Application settings
 * - Persistence middleware
 */

import { configureStore } from '@reduxjs/toolkit';
import { persistStore, persistReducer } from 'redux-persist';
import storage from 'redux-persist/lib/storage';
import { combineReducers } from '@reduxjs/toolkit';

// Slice imports
import authSlice from './slices/authSlice';
import uiSlice from './slices/uiSlice';
import userSlice from './slices/userSlice';

/**
 * Persist configuration
 */
const persistConfig = {
  key: 'devsocial',
  storage,
  whitelist: ['auth', 'ui'], // Only persist auth and UI preferences
  blacklist: ['user'], // Don't persist user data (will be fetched fresh)
};

/**
 * Root reducer combining all slices
 */
const rootReducer = combineReducers({
  auth: authSlice,
  ui: uiSlice,
  user: userSlice,
});

/**
 * Persisted reducer
 */
const persistedReducer = persistReducer(persistConfig, rootReducer);

/**
 * Configure Redux store with middleware
 */
export const store = configureStore({
  reducer: persistedReducer,
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware({
      serializableCheck: {
        // Ignore redux-persist actions
        ignoredActions: [
          'persist/PERSIST',
          'persist/REHYDRATE',
          'persist/PAUSE',
          'persist/PURGE',
          'persist/REGISTER',
        ],
        ignoredPaths: ['register'],
      },
    }),
  devTools: __DEV__, // Enable Redux DevTools in development
});

/**
 * Create persistor for redux-persist
 */
export const persistor = persistStore(store);

/**
 * TypeScript types for the store
 */
export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;

/**
 * Typed hooks for use throughout the app
 */
export { useAppDispatch, useAppSelector } from './hooks';

export default store;