package com.weatherfetch.service;

import com.weatherfetch.api.OpenMeteoApiClient;
import com.weatherfetch.model.Location;
import com.weatherfetch.model.WeatherData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
public class WeatherService {
    
    private final OpenMeteoApiClient apiClient;
    
    private final Map<String, CacheEntry<List<Location>>> locationCache = new ConcurrentHashMap<>();
    private final Map<String, CacheEntry<WeatherData>> weatherCache = new ConcurrentHashMap<>();
    
    private static final Duration LOCATION_CACHE_TTL = Duration.ofHours(24);
    private static final Duration WEATHER_CACHE_TTL = Duration.ofMinutes(15);
    
    public CompletableFuture<List<Location>> searchLocationAsync(String query) {
        return CompletableFuture.supplyAsync(() -> {
            String cacheKey = query.toLowerCase().trim();
            CacheEntry<List<Location>> entry = locationCache.get(cacheKey);
            
            if (entry != null && !entry.isExpired()) {
                log.info("Returning location search results from cache for query: {}", query);
                return entry.getValue();
            }
            
            try {
                List<Location> locations = apiClient.searchLocation(query);
                locationCache.put(cacheKey, new CacheEntry<>(locations, LOCATION_CACHE_TTL));
                return locations;
            } catch (Exception e) {
                log.error("Failed to search location: {}", query, e);
                throw new RuntimeException("Failed to search location", e);
            }
        });
    }
    
    public CompletableFuture<WeatherData> getWeatherDataAsync(Location location) {
        return CompletableFuture.supplyAsync(() -> {
            String cacheKey = location.getLatitude() + "," + location.getLongitude();
            CacheEntry<WeatherData> entry = weatherCache.get(cacheKey);
            
            if (entry != null && !entry.isExpired()) {
                log.info("Returning weather data from cache for location: {}", location.getName());
                return entry.getValue();
            }
            
            try {
                WeatherData weatherData = apiClient.getWeatherData(location);
                weatherCache.put(cacheKey, new CacheEntry<>(weatherData, WEATHER_CACHE_TTL));
                return weatherData;
            } catch (Exception e) {
                log.error("Failed to fetch weather data for location: {}", location.getName(), e);
                throw new RuntimeException("Failed to fetch weather data", e);
            }
        });
    }
    
    public void clearCache() {
        locationCache.clear();
        weatherCache.clear();
    }
    
    private static class CacheEntry<T> {
        private final T value;
        private final Instant expiryTime;
        
        public CacheEntry(T value, Duration ttl) {
            this.value = value;
            this.expiryTime = Instant.now().plus(ttl);
        }
        
        public T getValue() {
            return value;
        }
        
        public boolean isExpired() {
            return Instant.now().isAfter(expiryTime);
        }
    }
}
