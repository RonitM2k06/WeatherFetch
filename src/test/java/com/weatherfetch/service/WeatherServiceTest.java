package com.weatherfetch.service;

import com.weatherfetch.api.OpenMeteoApiClient;
import com.weatherfetch.model.Location;
import com.weatherfetch.model.WeatherData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WeatherServiceTest {

    @Mock
    private OpenMeteoApiClient mockApiClient;

    private WeatherService weatherService;

    @BeforeEach
    public void setup() {
        weatherService = new WeatherService(mockApiClient);
    }

    @Test
    public void testSearchLocationAsync_Success() throws Exception {
        Location loc = Location.builder().name("London").build();
        when(mockApiClient.searchLocation("London")).thenReturn(List.of(loc));

        CompletableFuture<List<Location>> future = weatherService.searchLocationAsync("London");
        List<Location> result = future.get();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("London", result.get(0).getName());
        verify(mockApiClient, times(1)).searchLocation("London");
    }

    @Test
    public void testSearchLocationAsync_CacheHits() throws Exception {
        Location loc = Location.builder().name("Paris").build();
        when(mockApiClient.searchLocation("Paris")).thenReturn(List.of(loc));

        // First call
        weatherService.searchLocationAsync("Paris").get();
        // Second call should hit cache
        weatherService.searchLocationAsync("Paris").get();

        verify(mockApiClient, times(1)).searchLocation("Paris");
    }

    @Test
    public void testGetWeatherDataAsync_Success() throws Exception {
        Location loc = Location.builder().name("London").latitude(51.5).longitude(-0.1).build();
        WeatherData mockData = WeatherData.builder().location(loc).build();
        
        when(mockApiClient.getWeatherData(loc)).thenReturn(mockData);

        CompletableFuture<WeatherData> future = weatherService.getWeatherDataAsync(loc);
        WeatherData result = future.get();

        assertNotNull(result);
        assertEquals(loc, result.getLocation());
        verify(mockApiClient, times(1)).getWeatherData(loc);
    }
    
    @Test
    public void testSearchLocationAsync_Error() throws Exception {
        when(mockApiClient.searchLocation(anyString())).thenThrow(new IOException("API Error"));

        CompletableFuture<List<Location>> future = weatherService.searchLocationAsync("ErrorCity");

        ExecutionException exception = assertThrows(ExecutionException.class, future::get);
        assertTrue(exception.getCause() instanceof RuntimeException);
        assertEquals("Failed to search location", exception.getCause().getMessage());
    }
}
