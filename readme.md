# Spring Security Lab

## Overview

This project is a comprehensive Spring Security implementation that provides authentication and authorization services for web applications. It consists of multiple interconnected sub-projects that work together to deliver a complete security solution based on OAuth2 standards.

## Sub-Projects

The project is organized into the following specialized modules:

1. Basic Security Server
   Provides fundamental authentication mechanisms
   Implements basic security configurations
2. Authorization Server
   Manages OAuth2 authorization flows
   Handles token generation and validation
   Provides client registration and management
   Implements various grant types for secure access delegation
3. OAuth2 Client
   Demonstrates OAuth2 client implementation
   Handles authentication flows from the client perspective
   Manages user authentication requests and token exchanges
4. Resource Server
   Protects web application resources
   Validates access tokens for authorized access
   Implements fine-grained access control policies
   Secures REST APIs and other protected resources
5. OAuth2 REST Client
   Provides programmatic access to secured REST APIs
   Implements OAuth2 client credentials and authorization code flows
   Handles automatic token acquisition and refresh
   Enables service-to-service communication with secured resources
   Supports bearer token authentication for REST calls

## Getting Started

### Prerequisites

Java 17 or higher</br>
Maven or Gradle</br>
Spring Boot 3.x or higher</br>
Spring Security 6.x or higher</br>

## Configuration

Each sub-project contains its own configuration files in src/main/resources

### Security Endpoints

BaseUrl/.well-known/openid-configuration

### OAuth2 REST Client Configuration

The OAuth2 REST Client requires the following configuration properties:

```
auth-server:  
  login-client-id: apiBanking-client  # for authorization_code grant type flow
  register-client-id: apiBanking-apis  # for client_credential grant type flow

```

## Reference

Spring Website - https://spring.io/

Spring Documents - https://docs.spring.io/

Spring Projects website - https://spring.io/projects

OAuth2 Website - https://oauth.net/2/
