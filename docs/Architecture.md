# Architecture

WeatherFetch is built using a clean architecture pattern to ensure maintainability, scalability, and testability.

## High-Level Components

1. **UI Layer (`com.weatherfetch.controller`, `src/main/resources/fxml`)**
   - Built with JavaFX.
   - FXML files define the layout, utilizing modern CSS for styling.
   - Controllers handle user interactions and bind data from the Services to the UI.

2. **Service Layer (`com.weatherfetch.service`)**
   - Contains the core business logic.
   - `WeatherService`: Orchestrates fetching weather data, caching responses, and handling asynchronous execution via `CompletableFuture`.
   - `SettingsService`: Manages user preferences (theme, units).

3. **API Layer (`com.weatherfetch.api`)**
   - `OpenMeteoApiClient`: A dedicated client to interface with the Open-Meteo REST API.
   - Handles network timeouts, JSON parsing (via Jackson), and error recovery.

4. **Model Layer (`com.weatherfetch.model`)**
   - Pure POJOs (using Lombok/Records) representing the domain entities: `CurrentWeather`, `Location`, `AirQuality`, `DailyForecast`, `HourlyForecast`.

## Concurrency

- Network calls are executed on background threads using a custom ExecutorService or `CompletableFuture.supplyAsync`.
- UI updates are strictly dispatched to the JavaFX Application Thread using `Platform.runLater()`.
