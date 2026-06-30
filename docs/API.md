# API Integration

WeatherFetch currently uses the [Open-Meteo API](https://open-meteo.com/).

## Why Open-Meteo?
- No API key required for non-commercial use.
- Comprehensive data (current weather, hourly, daily, air quality).
- Extremely fast response times.

## Endpoints Used
- **Geocoding:** `https://geocoding-api.open-meteo.com/v1/search`
- **Weather Forecast:** `https://api.open-meteo.com/v1/forecast`
- **Air Quality:** `https://air-quality-api.open-meteo.com/v1/air-quality`

## Extending to other APIs
The `WeatherService` interacts with `OpenMeteoApiClient`. To support a different API (like OpenWeatherMap):
1. Implement a new client (e.g., `OpenWeatherMapClient`).
2. Adapt the response to the domain models (`WeatherData`, `CurrentWeather`, etc.).
3. Update `WeatherService` to inject the new client.
