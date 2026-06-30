package com.weatherfetch.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Location {
    private String name;
    private String country;
    private String admin1; // State or province
    private double latitude;
    private double longitude;
    private String timezone;
    
    public String getDisplayName() {
        if (admin1 != null && !admin1.isEmpty() && country != null && !country.isEmpty()) {
            return name + ", " + admin1 + ", " + country;
        } else if (country != null && !country.isEmpty()) {
            return name + ", " + country;
        }
        return name;
    }
}
