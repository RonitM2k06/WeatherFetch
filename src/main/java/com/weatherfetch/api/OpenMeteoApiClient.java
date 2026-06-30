package com.weatherfetch.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.weatherfetch.model.*;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class OpenMeteoApiClient {
    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;
    private static final String WEATHER_URL = "https://api.open-meteo.com/v1/forecast";
    private static final String GEOCODING_URL = "https://geocoding-api.open-meteo.com/v1/search";
    private static final String AIR_QUALITY_URL = "https://air-quality-api.open-meteo.com/v1/air-quality";

    public OpenMeteoApiClient(OkHttpClient httpClient, ObjectMapper objectMapper) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    public List<Location> searchLocation(String query) throws IOException {
        String encodedQuery = java.net.URLEncoder.encode(query, java.nio.charset.StandardCharsets.UTF_8);
        String url = GEOCODING_URL + "?name=" + encodedQuery + "&count=10&language=en&format=json";
        log.info("Geocoding URL: {}", url);
        Request request = new Request.Builder().url(url).build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            String responseBody = response.body().string();
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode results = root.path("results");
            List<Location> locations = new ArrayList<>();
            if (results.isArray()) {
                for (JsonNode node : results) {
                    locations.add(Location.builder()
                            .name(node.path("name").asText())
                            .country(node.path("country").asText(""))
                            .admin1(node.path("admin1").asText(""))
                            .latitude(node.path("latitude").asDouble())
                            .longitude(node.path("longitude").asDouble())
                            .timezone(node.path("timezone").asText("auto"))
                            .build());
                }
            }
            return locations;
        }
    }

    public WeatherData getWeatherData(Location location) throws IOException {
        String url = WEATHER_URL + "?latitude=" + location.getLatitude() + "&longitude=" + location.getLongitude()
                + "&current=temperature_2m,relative_humidity_2m,apparent_temperature,is_day,precipitation,weather_code,cloud_cover,pressure_msl,wind_speed_10m,wind_direction_10m"
                + "&hourly=temperature_2m,relative_humidity_2m,apparent_temperature,precipitation_probability,precipitation,weather_code,pressure_msl,visibility,wind_speed_10m,wind_direction_10m,uv_index"
                + "&daily=weather_code,temperature_2m_max,temperature_2m_min,apparent_temperature_max,apparent_temperature_min,sunrise,sunset,uv_index_max,precipitation_sum,precipitation_probability_max,wind_speed_10m_max,wind_direction_10m_dominant"
                + "&timezone=auto";
        
        log.info("Weather URL: {}", url);
        Request request = new Request.Builder().url(url).build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            JsonNode root = objectMapper.readTree(response.body().string());
            JsonNode current = root.path("current");
            JsonNode daily = root.path("daily");
            JsonNode hourly = root.path("hourly");

            CurrentWeather currentWeather = CurrentWeather.builder()
                    .temperature(current.path("temperature_2m").asDouble())
                    .feelsLike(current.path("apparent_temperature").asDouble())
                    .humidity(current.path("relative_humidity_2m").asDouble())
                    .isDay(current.path("is_day").asInt() == 1)
                    .weatherCode(current.path("weather_code").asInt())
                    .cloudCover(current.path("cloud_cover").asInt())
                    .pressure(current.path("pressure_msl").asDouble())
                    .windSpeed(current.path("wind_speed_10m").asDouble())
                    .windDirection(current.path("wind_direction_10m").asInt())
                    .visibility(hourly.path("visibility").path(0).asDouble() / 1000.0) // Visibility is in hourly array, approx using first hour
                    .uvIndex(hourly.path("uv_index").path(0).asDouble())
                    .build();

            List<DailyForecast> dailyForecasts = new ArrayList<>();
            JsonNode timeArray = daily.path("time");
            for (int i = 0; i < timeArray.size(); i++) {
                dailyForecasts.add(DailyForecast.builder()
                        .date(LocalDate.parse(timeArray.path(i).asText()))
                        .maxTemp(daily.path("temperature_2m_max").path(i).asDouble())
                        .minTemp(daily.path("temperature_2m_min").path(i).asDouble())
                        .weatherCode(daily.path("weather_code").path(i).asInt())
                        .precipitationProbability(daily.path("precipitation_probability_max").path(i).asDouble())
                        .precipitationSum(daily.path("precipitation_sum").path(i).asDouble())
                        .sunrise(daily.path("sunrise").path(i).asText())
                        .sunset(daily.path("sunset").path(i).asText())
                        .build());
            }

            List<HourlyForecast> hourlyForecasts = new ArrayList<>();
            JsonNode hourlyTimeArray = hourly.path("time");
            // Only take first 24 hours
            int limit = Math.min(24, hourlyTimeArray.size());
            for (int i = 0; i < limit; i++) {
                hourlyForecasts.add(HourlyForecast.builder()
                        .time(LocalDateTime.parse(hourlyTimeArray.path(i).asText()))
                        .temperature(hourly.path("temperature_2m").path(i).asDouble())
                        .humidity(hourly.path("relative_humidity_2m").path(i).asDouble())
                        .windSpeed(hourly.path("wind_speed_10m").path(i).asDouble())
                        .weatherCode(hourly.path("weather_code").path(i).asInt())
                        .precipitationProbability(hourly.path("precipitation_probability").path(i).asDouble())
                        .build());
            }

            AirQuality airQuality = fetchAirQuality(location.getLatitude(), location.getLongitude());

            return WeatherData.builder()
                    .location(location)
                    .currentWeather(currentWeather)
                    .dailyForecasts(dailyForecasts)
                    .hourlyForecasts(hourlyForecasts)
                    .airQuality(airQuality)
                    .build();
        }
    }

    private AirQuality fetchAirQuality(double lat, double lon) {
        String url = AIR_QUALITY_URL + "?latitude=" + lat + "&longitude=" + lon 
                + "&current=european_aqi,us_aqi,pm10,pm2_5,carbon_monoxide,nitrogen_dioxide,sulphur_dioxide,ozone&timezone=auto";
        Request request = new Request.Builder().url(url).build();
        try (Response response = httpClient.newCall(request).execute()) {
            if (response.isSuccessful()) {
                JsonNode root = objectMapper.readTree(response.body().string());
                JsonNode current = root.path("current");
                return AirQuality.builder()
                        .aqi(current.path("us_aqi").asInt())
                        .pm25(current.path("pm2_5").asDouble())
                        .pm10(current.path("pm10").asDouble())
                        .carbonMonoxide(current.path("carbon_monoxide").asDouble())
                        .nitrogenDioxide(current.path("nitrogen_dioxide").asDouble())
                        .sulphurDioxide(current.path("sulphur_dioxide").asDouble())
                        .ozone(current.path("ozone").asDouble())
                        .build();
            }
        } catch (Exception e) {
            log.error("Error fetching air quality: {}", e.getMessage());
        }
        return AirQuality.builder().build(); // empty if failed
    }
}
