package com.weatherfetch.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AirQuality {
    private int aqi; // European AQI or US AQI
    private double pm25;
    private double pm10;
    private double carbonMonoxide;
    private double nitrogenDioxide;
    private double sulphurDioxide;
    private double ozone;

    public String getHealthRecommendation() {
        if (aqi <= 50) return "Air quality is considered satisfactory, and air pollution poses little or no risk.";
        if (aqi <= 100) return "Air quality is acceptable; however, for some pollutants there may be a moderate health concern for a very small number of people who are unusually sensitive to air pollution.";
        if (aqi <= 150) return "Members of sensitive groups may experience health effects. The general public is not likely to be affected.";
        if (aqi <= 200) return "Everyone may begin to experience health effects; members of sensitive groups may experience more serious health effects.";
        if (aqi <= 300) return "Health warnings of emergency conditions. The entire population is more likely to be affected.";
        return "Health alert: everyone may experience more serious health effects.";
    }
}
