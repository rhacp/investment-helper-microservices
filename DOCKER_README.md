# Docker setup

This project can be started end-to-end with:

```bash
docker compose up --build
```

## Required `.env` values

Create a root `.env` file from `.env.example` and set at least:

- `POSTGRES_USER`
- `POSTGRES_PASSWORD`
- `POSTGRES_DB`
- `AUTH_DB_NAME`
- `USER_DB_NAME`
- `PORTFOLIO_DB_NAME`
- `MARKET_DATA_DB_NAME`
- `PREDICTION_DB_NAME`
- `ANALYTICS_DB_NAME`
- `FMP_API_KEY`
- `BOOTSTRAP_ADMIN_PASSWORD`
- `JWT_ISSUER` if you want to override the default issuer
- `JWT_PUBLIC_KEY_PATH` and `JWT_PRIVATE_KEY_PATH` only if you need non-default key locations
- `FRONTEND_API_BASE_URL` for the React build, usually `http://localhost:8080`

## Start the app

1. Copy `.env.example` to `.env`.
2. Fill in the values you need.
3. Run `docker compose up --build`.

PostgreSQL stays internal to Docker. The init scripts create one database per microservice on first startup.

## Open these URLs

- Frontend: `http://localhost:3000`
- API Gateway: `http://localhost:8080`
- Eureka dashboard: `http://localhost:8761`

## Stop and clean up

Stop containers:

```bash
docker compose down
```

Stop containers and remove persisted volumes:

```bash
docker compose down -v
```

## Notes

- Internal services use Docker service names plus Eureka registration inside the shared Docker network.
- Only the React app, API Gateway, and Eureka dashboard are exposed on the host.
- Prediction models are persisted in the `prediction-models` Docker volume.
