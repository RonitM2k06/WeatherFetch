package com.weatherfetch.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class DailyForecast {
    private LocalDate date;
    private double maxTemp;
    private double minTemp;
    private int weatherCode;
    private double precipitationProbability; // %
    private double precipitationSum; // mm
    private String sunrise;
    private String sunset;
}
