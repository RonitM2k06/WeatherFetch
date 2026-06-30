package com.weatherfetch.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class HourlyForecast {
    private LocalDateTime time;
    private double temperature;
    private double humidity;
    private double windSpeed;
    private int weatherCode;
    private double precipitationProbability;
}
