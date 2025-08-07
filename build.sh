#!/bin/bash

# DevSocial Platform Build Script
# This script helps with building, testing, and running the application

set -e  # Exit on any error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Functions
print_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check if Maven is installed
check_maven() {
    if ! command -v mvn &> /dev/null; then
        print_error "Maven is not installed. Please install Maven 3.8+ to continue."
        exit 1
    fi
    print_info "Maven version: $(mvn -v | head -n 1)"
}

# Check if Docker is installed
check_docker() {
    if ! command -v docker &> /dev/null; then
        print_warning "Docker is not installed. Some features may not work."
        return 1
    fi
    print_info "Docker version: $(docker --version)"
    return 0
}

# Clean all services
clean_all() {
    print_info "Cleaning all services..."
    mvn clean
    print_success "Clean completed"
}

# Build all services
build_all() {
    print_info "Building all services..."
    mvn clean compile
    print_success "Build completed"
}

# Run tests
test_all() {
    print_info "Running tests for all services..."
    mvn test
    print_success "Tests completed"
}

# Package all services
package_all() {
    print_info "Packaging all services..."
    mvn clean package -DskipTests
    print_success "Packaging completed"
}

# Run specific service
run_service() {
    local service=$1
    if [ -z "$service" ]; then
        print_error "Service name is required"
        echo "Available services: backend/auth-service, backend/user-service, backend/post-service, backend/message-service, backend/media-service, backend/gateway"
        exit 1
    fi

    if [ ! -d "$service" ]; then
        print_error "Service '$service' not found"
        exit 1
    fi

    print_info "Starting $service..."
    cd "$service"
    mvn spring-boot:run
}

# Setup development environment
setup_dev() {
    print_info "Setting up development environment..."
    
    # Copy environment file if it doesn't exist
    if [ ! -f ".env" ]; then
        cp .env.example .env
        print_warning "Created .env file from .env.example. Please update with your actual values."
    fi
    
    # Build all services
    build_all
    
    print_success "Development environment setup completed"
}

# Docker operations
docker_build() {
    if ! check_docker; then
        exit 1
    fi
    
    print_info "Building Docker images..."
    docker-compose build
    print_success "Docker images built successfully"
}

docker_up() {
    if ! check_docker; then
        exit 1
    fi
    
    print_info "Starting services with Docker Compose..."
    docker-compose up -d
    print_success "Services started. Check logs with: docker-compose logs -f"
}

docker_down() {
    if ! check_docker; then
        exit 1
    fi
    
    print_info "Stopping Docker services..."
    docker-compose down
    print_success "Services stopped"
}

docker_logs() {
    if ! check_docker; then
        exit 1
    fi
    
    local service=$1
    if [ -z "$service" ]; then
        docker-compose logs -f
    else
        docker-compose logs -f "$service"
    fi
}

# Database operations
setup_db() {
    print_info "Setting up databases..."
    
    # Check if PostgreSQL is running
    if ! pg_isready -h localhost -p 5432 &> /dev/null; then
        print_warning "PostgreSQL is not running. Starting with Docker..."
        docker run -d \
            --name devsocial-postgres \
            -e POSTGRES_DB=devsocial \
            -e POSTGRES_USER=devsocial \
            -e POSTGRES_PASSWORD=devsocial123 \
            -p 5432:5432 \
            postgres:13
        
        # Wait for PostgreSQL to start
        sleep 10
    fi
    
    # Create databases
    createdb -h localhost -U devsocial devsocial_auth 2>/dev/null || true
    createdb -h localhost -U devsocial devsocial_user 2>/dev/null || true
    createdb -h localhost -U devsocial devsocial_post 2>/dev/null || true
    createdb -h localhost -U devsocial devsocial_message 2>/dev/null || true
    
    print_success "Databases setup completed"
}

# Show help
show_help() {
    echo "DevSocial Platform Build Script"
    echo ""
    echo "Usage: $0 [command] [options]"
    echo ""
    echo "Commands:"
    echo "  clean              Clean all services"
    echo "  build              Build all services"
    echo "  test               Run tests for all services"
    echo "  package            Package all services"
    echo "  run <service>      Run specific service"
    echo "  setup-dev          Setup development environment"
    echo "  setup-db           Setup databases"
    echo ""
    echo "Docker Commands:"
    echo "  docker-build       Build Docker images"
    echo "  docker-up          Start services with Docker"
    echo "  docker-down        Stop Docker services"
    echo "  docker-logs [svc]  Show Docker logs"
    echo ""
    echo "Examples:"
    echo "  $0 build                    # Build all services"
    echo "  $0 run auth-service         # Run authentication service"
    echo "  $0 docker-up               # Start all services with Docker"
    echo "  $0 docker-logs auth-service # Show logs for auth service"
    echo ""
}

# Main script logic
main() {
    # Check prerequisites
    check_maven
    
    case "$1" in
        "clean")
            clean_all
            ;;
        "build")
            build_all
            ;;
        "test")
            test_all
            ;;
        "package")
            package_all
            ;;
        "run")
            run_service "$2"
            ;;
        "setup-dev")
            setup_dev
            ;;
        "setup-db")
            setup_db
            ;;
        "docker-build")
            docker_build
            ;;
        "docker-up")
            docker_up
            ;;
        "docker-down")
            docker_down
            ;;
        "docker-logs")
            docker_logs "$2"
            ;;
        "help"|"-h"|"--help"|"")
            show_help
            ;;
        *)
            print_error "Unknown command: $1"
            show_help
            exit 1
            ;;
    esac
}

# Run main function with all arguments
main "$@"