/**
 * DevSocial Web Application Entry Point
 * 
 * Main entry file that renders the React application with all providers
 * and global configurations.
 */

import React from 'react';
import ReactDOM from 'react-dom/client';
import { Provider as ReduxProvider } from 'react-redux';
import { PersistGate } from 'redux-persist/integration/react';

import App from './App';
import { store, persistor } from './store/store';
import LoadingScreen from './components/common/LoadingScreen';

// Import global styles
import './styles/global.css';

/**
 * Render the application
 */
ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <ReduxProvider store={store}>
      <PersistGate 
        loading={<LoadingScreen message="Initializing application..." />} 
        persistor={persistor}
      >
        <App />
      </PersistGate>
    </ReduxProvider>
  </React.StrictMode>
);