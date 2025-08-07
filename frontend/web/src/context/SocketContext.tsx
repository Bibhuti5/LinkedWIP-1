/**
 * WebSocket Context Provider
 * 
 * Provides WebSocket connection and real-time messaging capabilities
 * throughout the application using STOMP over WebSocket.
 */

import React, { createContext, useContext, useEffect, useRef, ReactNode } from 'react';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { useAuth } from './AuthContext';

/**
 * WebSocket context interface
 */
interface SocketContextType {
  client: Client | null;
  isConnected: boolean;
  subscribe: (destination: string, callback: (message: any) => void) => () => void;
  publish: (destination: string, body: any) => void;
}

/**
 * Create WebSocket context
 */
const SocketContext = createContext<SocketContextType | undefined>(undefined);

/**
 * WebSocket context provider props
 */
interface SocketProviderProps {
  children: ReactNode;
}

/**
 * WebSocket Context Provider Component
 * 
 * Manages WebSocket connection lifecycle and provides messaging methods.
 * Automatically connects when user is authenticated and disconnects on logout.
 */
export const SocketProvider: React.FC<SocketProviderProps> = ({ children }) => {
  const { isAuthenticated, user } = useAuth();
  const clientRef = useRef<Client | null>(null);
  const [isConnected, setIsConnected] = React.useState(false);

  /**
   * Initialize WebSocket connection
   */
  const connect = () => {
    if (!isAuthenticated || clientRef.current?.connected) {
      return;
    }

    const client = new Client({
      webSocketFactory: () => new SockJS('/ws'),
      connectHeaders: {
        // Add authentication headers if needed
      },
      debug: (str) => {
        if (__DEV__) {
          console.log('STOMP Debug:', str);
        }
      },
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
    });

    client.onConnect = () => {
      console.log('WebSocket connected');
      setIsConnected(true);
      
      // Subscribe to user-specific notifications
      if (user?.id) {
        client.subscribe(`/user/${user.id}/notifications`, (message) => {
          const notification = JSON.parse(message.body);
          console.log('Received notification:', notification);
          // Handle notification (toast, update state, etc.)
        });
      }
    };

    client.onDisconnect = () => {
      console.log('WebSocket disconnected');
      setIsConnected(false);
    };

    client.onStompError = (frame) => {
      console.error('WebSocket error:', frame);
      setIsConnected(false);
    };

    client.activate();
    clientRef.current = client;
  };

  /**
   * Disconnect WebSocket
   */
  const disconnect = () => {
    if (clientRef.current) {
      clientRef.current.deactivate();
      clientRef.current = null;
      setIsConnected(false);
    }
  };

  /**
   * Subscribe to a destination
   */
  const subscribe = (destination: string, callback: (message: any) => void) => {
    if (!clientRef.current?.connected) {
      console.warn('WebSocket not connected');
      return () => {};
    }

    const subscription = clientRef.current.subscribe(destination, (message) => {
      try {
        const parsedMessage = JSON.parse(message.body);
        callback(parsedMessage);
      } catch (error) {
        console.error('Error parsing WebSocket message:', error);
      }
    });

    return () => subscription.unsubscribe();
  };

  /**
   * Publish a message to a destination
   */
  const publish = (destination: string, body: any) => {
    if (!clientRef.current?.connected) {
      console.warn('WebSocket not connected');
      return;
    }

    clientRef.current.publish({
      destination,
      body: JSON.stringify(body),
    });
  };

  /**
   * Handle authentication changes
   */
  useEffect(() => {
    if (isAuthenticated && user) {
      connect();
    } else {
      disconnect();
    }

    return () => {
      disconnect();
    };
  }, [isAuthenticated, user]);

  /**
   * Context value
   */
  const contextValue: SocketContextType = {
    client: clientRef.current,
    isConnected,
    subscribe,
    publish,
  };

  return (
    <SocketContext.Provider value={contextValue}>
      {children}
    </SocketContext.Provider>
  );
};

/**
 * Custom hook to use WebSocket context
 * 
 * @returns WebSocket context value
 * @throws Error if used outside SocketProvider
 */
export const useSocket = (): SocketContextType => {
  const context = useContext(SocketContext);
  
  if (context === undefined) {
    throw new Error('useSocket must be used within a SocketProvider');
  }
  
  return context;
};

export default SocketContext;