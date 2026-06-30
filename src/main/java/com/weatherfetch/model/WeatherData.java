package com.weatherfetch.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeatherData {
    private Location location;
    private CurrentWeather currentWeather;
    private List<DailyForecast> dailyForecasts;
    private List<HourlyForecast> hourlyForecasts;
    private AirQuality airQuality;
}
