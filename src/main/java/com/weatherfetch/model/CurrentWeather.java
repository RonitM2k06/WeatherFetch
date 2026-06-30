package com.weatherfetch.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CurrentWeather {
    private double temperature;
    private double feelsLike;
    private double humidity; // %
    private double pressure; // hPa
    private double windSpeed; // km/h or mph
    private int windDirection; // degrees
    private double visibility; // km
    private double uvIndex;
    private int cloudCover; // %
    private int weatherCode; // WMO weather code
    private boolean isDay;
}
