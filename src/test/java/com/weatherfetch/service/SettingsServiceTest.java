package com.weatherfetch.service;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SettingsServiceTest {

    private static final Path SETTINGS_FILE = Paths.get("settings.properties");
    private SettingsService settingsService;

    @BeforeEach
    public void setup() throws IOException {
        // Ensure clean state before each test
        Files.deleteIfExists(SETTINGS_FILE);
        settingsService = new SettingsService();
    }
    
    @AfterAll
    public static void tearDown() throws IOException {
        Files.deleteIfExists(SETTINGS_FILE);
    }

    @Test
    public void testDefaultSettings() {
        assertEquals(SettingsService.Unit.METRIC, settingsService.getUnit());
        assertEquals(SettingsService.Theme.LIGHT, settingsService.getTheme());
    }

    @Test
    public void testSaveAndLoadSettings() {
        settingsService.setUnit(SettingsService.Unit.IMPERIAL);
        settingsService.setTheme(SettingsService.Theme.DARK);

        // Verify it was updated in current instance
        assertEquals(SettingsService.Unit.IMPERIAL, settingsService.getUnit());
        assertEquals(SettingsService.Theme.DARK, settingsService.getTheme());

        // Create new instance to force load from file
        SettingsService newSettingsService = new SettingsService();
        assertEquals(SettingsService.Unit.IMPERIAL, newSettingsService.getUnit());
        assertEquals(SettingsService.Theme.DARK, newSettingsService.getTheme());
    }
}
