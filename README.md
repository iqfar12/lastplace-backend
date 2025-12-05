# Last Place Backend - Spring Boot Application

## Overview

This is the backend service for Last Place, built with Spring Boot. It provides REST APIs for venue management, authentication, and Google Calendar integration.

## Requirements

- Java 17 or higher
- Maven 3.8+
- MySQL 8.0+

## Setup

### 1. Database Configuration

Create MySQL database:
\`\`\`sql
CREATE DATABASE lastplace;
\`\`\`

### 2. Application Properties

Edit `src/main/resources/application.properties`:

```properties
# Database
spring.datasource.url=jdbc:mysql://localhost:3306/lastplace
spring.datasource.username=your_username
spring.datasource.password=your_password

# JWT
jwt.secret=YourSuperSecretKeyForJWTTokenGenerationMustBeLongEnough
jwt.expiration=86400000

# CORS
cors.allowed-origins=http://localhost:3000
