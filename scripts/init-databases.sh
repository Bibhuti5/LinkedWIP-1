#!/bin/bash
set -e

# Create multiple databases for different services
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    -- Create databases for each microservice
    CREATE DATABASE devsocial_auth;
    CREATE DATABASE devsocial_user;
    CREATE DATABASE devsocial_post;
    CREATE DATABASE devsocial_message;
    
    -- Grant privileges to the user
    GRANT ALL PRIVILEGES ON DATABASE devsocial_auth TO $POSTGRES_USER;
    GRANT ALL PRIVILEGES ON DATABASE devsocial_user TO $POSTGRES_USER;
    GRANT ALL PRIVILEGES ON DATABASE devsocial_post TO $POSTGRES_USER;
    GRANT ALL PRIVILEGES ON DATABASE devsocial_message TO $POSTGRES_USER;
    
    -- Create extensions if needed
    \c devsocial_auth;
    CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
    
    \c devsocial_user;
    CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
    
    \c devsocial_post;
    CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
    
    \c devsocial_message;
    CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
EOSQL

echo "Multiple databases created successfully!"