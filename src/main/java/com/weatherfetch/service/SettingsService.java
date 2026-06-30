package com.weatherfetch.service;

import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

@Slf4j
public class SettingsService {

    public enum Unit {
        METRIC, IMPERIAL
    }
    
    public enum Theme {
        LIGHT, DARK
    }

    private static final String SETTINGS_FILE = "settings.properties";
    private static final String KEY_UNIT = "unit";
    private static final String KEY_THEME = "theme";

    private final Properties properties;
    private final Path settingsPath;
    
    private Unit currentUnit;
    private Theme currentTheme;

    public SettingsService() {
        this.properties = new Properties();
        this.settingsPath = Paths.get(SETTINGS_FILE);
        loadSettings();
    }

    private void loadSettings() {
        if (Files.exists(settingsPath)) {
            try (FileInputStream fis = new FileInputStream(settingsPath.toFile())) {
                properties.load(fis);
            } catch (IOException e) {
                log.error("Failed to load settings from {}", SETTINGS_FILE, e);
            }
        }
        
        // Parse Unit
        String unitStr = properties.getProperty(KEY_UNIT, Unit.METRIC.name());
        try {
            currentUnit = Unit.valueOf(unitStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            currentUnit = Unit.METRIC;
        }

        // Parse Theme
        String themeStr = properties.getProperty(KEY_THEME, Theme.LIGHT.name());
        try {
            currentTheme = Theme.valueOf(themeStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            currentTheme = Theme.LIGHT;
        }
    }

    public void saveSettings() {
        properties.setProperty(KEY_UNIT, currentUnit.name());
        properties.setProperty(KEY_THEME, currentTheme.name());
        
        try (FileOutputStream fos = new FileOutputStream(settingsPath.toFile())) {
            properties.store(fos, "WeatherFetch User Settings");
            log.info("Settings saved successfully.");
        } catch (IOException e) {
            log.error("Failed to save settings to {}", SETTINGS_FILE, e);
        }
    }

    public Unit getUnit() {
        return currentUnit;
    }

    public void setUnit(Unit unit) {
        this.currentUnit = unit;
        saveSettings();
    }

    public Theme getTheme() {
        return currentTheme;
    }

    public void setTheme(Theme theme) {
        this.currentTheme = theme;
        saveSettings();
    }
}
