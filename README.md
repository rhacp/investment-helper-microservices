# Investment Helper

Investment Helper is a full-stack microservices application designed to help users manage, analyze, and track investment portfolios.

The platform allows users to maintain portfolios of financial assets, monitor market performance, generate investment analytics, and leverage machine learning models for stock price prediction.

The project was developed using a modern microservices architecture based on Spring Boot and Spring Cloud, with a React frontend application.

---

# Features

Current features include:

* User registration and authentication
* JWT-based security
* Portfolio management
* Stock and market data synchronization
* Historical market data storage
* Portfolio valuation
* Stock analytics
* Portfolio analytics
* Machine learning model training
* Stock price prediction
* Service discovery and centralized routing

Future improvements may include:

* ETF support
* Cryptocurrency support
* Dividend tracking
* Portfolio rebalancing
* Advanced risk metrics
* Enhanced prediction models
* Mobile application

---

# Architecture

The application follows a microservices architecture.

```text
Client
   |
   v
API Gateway
   |
   +-------------------+
   |                   |
   v                   v
User Service      Portfolio Service
                       |
                       v
                Market Data Service
                       |
                       v
                Prediction Service
                       |
                       v
                Analytics Service

```

Service discovery is handled through Eureka, while Spring Cloud Gateway acts as the single entry point for client requests.

---

# Backend

## Technologies

* Java 21
* Spring Boot
* Spring Cloud Gateway
* Spring Cloud Eureka
* Spring Security
* JWT Authentication
* Spring Data JPA
* PostgreSQL
* OpenFeign
* Maven

## Microservices

### API Gateway

Responsibilities:

* Single entry point for all client requests
* Request routing
* Authentication enforcement
* Security integration

---

### Discovery Server

Responsibilities:

* Service registration
* Service discovery
* Dynamic service lookup

Implemented using:

* Netflix Eureka Server

---

### Auth Service

Responsibilities:

* User registration
* User authentication
* JWT generation
* User role management

Features:

* RS256 JWT signing
* Role-based authorization
* Internal service endpoints

---

### User Service

Responsibilities:

* User profile management
* User information retrieval
* User updates
* User deactivation

---

### Portfolio Service

Responsibilities:

* Portfolio management
* Holding management
* Portfolio valuation

Features:

* Multiple portfolios per user
* Multiple holdings per portfolio
* Automatic stock synchronization
* Real-time portfolio calculations

---

### Market Data Service

Responsibilities:

* Stock management
* Historical market data storage
* Daily market synchronization

Features:

* Financial Modeling Prep integration
* Historical price storage
* Internal pricing APIs
* Automatic stock onboarding

---

### Prediction Service

Responsibilities:

* Machine learning model training
* Prediction generation
* Model management

Features:

* Tribuo-based classification models
* Model versioning
* Training history tracking
* Prediction analytics

---

### Analytics Service

Responsibilities:

* Stock analytics
* Portfolio analytics
* Performance metrics
* Risk calculations

Metrics include:

* Total Return
* Volatility
* Annualized Volatility
* Sharpe Ratio
* Maximum Drawdown

---

## API Documentation

The repository contains:

* OpenAPI (Swagger) specification
* Postman collection
* Postman environment

Location:

```text
/backend
```

These files can be imported directly into Swagger-compatible tools and Postman for testing and exploration.

---

# Frontend

Frontend documentation will be added as development progresses.

Planned technologies:

* React
* TypeScript
* React Router
* Axios
* Material UI

Planned features:

* Authentication screens
* Portfolio dashboard
* Holdings management
* Analytics dashboards
* Prediction visualization
* Responsive design

---

# Running the Project

## Backend

Start services in the following order:

1. discovery-server
2. api-gateway
3. auth-service
4. user-service
5. portfolio-service
6. market-data-service
7. prediction-service
8. analytics-service

## Frontend

Frontend setup instructions will be added once the React application is integrated with the backend.

---

# Project Status

Current status:

* Backend implementation completed
* Microservices integrated
* Authentication implemented
* Portfolio functionality implemented
* Market data synchronization implemented
* Analytics implemented
* Machine learning prediction implemented
* Frontend development in progress
