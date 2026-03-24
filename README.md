I am building a full-stack microservices project called Investment Helper.

Project overview:
The app helps users manage and analyze investments such as stocks and portfolios. It will include features like user management, portfolio tracking, and later analytics / ML components.

Architecture:
I am using a microservices architecture with Spring Boot and Spring Cloud.

Backend:
- api-gateway
    - Spring Cloud Gateway (WebFlux)
    - entry point for client requests
    - routes requests to backend services
- discovery-server
    - Eureka Server for service discovery
- user-service
    - handles user-related REST endpoints

Planned services:
- portfolio-service
- analytics-service

Frontend:
- React web app located in /frontend/react-app

Communication:
- Gateway routes requests to services
- Services register themselves in Eureka
- lb:// is used for load-balanced routing when enabled

Tech stack:
- Java 21
- Spring Boot 4
- Spring Cloud (Gateway, Eureka, LoadBalancer)
- Maven
- React + Node.js
- Git monorepo structure

Project structure:
investment-helper-microservices
/backend
/api-gateway
/discovery-server
/user-service
/frontend
/react-app

Current status:
- Eureka is working
- Gateway routing is working
- Microservices are registered correctly
- Frontend is created but not yet integrated with backend

What I want help with:
I will ask specific questions about implementation, architecture, integration, bugs, and best practices for this project.