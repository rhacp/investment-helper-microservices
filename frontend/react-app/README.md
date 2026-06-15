# Investment Helper Frontend

React + TypeScript frontend for the Investment Helper microservices platform.

## Run Locally

```bash
npm install
cp .env.example .env
npm start
```

The app calls the API Gateway through `REACT_APP_API_BASE_URL`, defaulting to `http://localhost:8080`.

## Main Routes

- `/login` and `/register` for authentication.
- `/dashboard` for global portfolio overview.
- `/portfolios` and `/portfolios/:portfolioId` for portfolio and holding management.
- `/stocks` for market data and historical price charts.
- `/predictions` for ML predictions and confidence analytics.
- `/analytics` for stock and portfolio risk dashboards.
- `/profile` for authenticated user profile updates.
