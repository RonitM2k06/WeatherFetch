package com.weatherfetch.controller;

import com.weatherfetch.model.*;
import com.weatherfetch.service.WeatherService;
import com.weatherfetch.service.SettingsService;
import com.weatherfetch.util.IconUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class MainController {

    @FXML private StackPane rootPane;
    @FXML private TextField searchField;
    @FXML private Button searchBtn;
    @FXML private Button locationBtn;
    @FXML private Button themeToggleBtn;
    @FXML private StackPane loadingOverlay;
    
    // Views
    @FXML private ScrollPane dashboardView;
    @FXML private VBox settingsView;
    @FXML private VBox mapView;
    
    // Map Element
    @FXML private javafx.scene.web.WebView mapWebView;
    
    // Settings elements
    @FXML private ComboBox<String> themeComboBox;
    @FXML private ComboBox<String> unitComboBox;
    @FXML private Button saveSettingsBtn;
    
    // UI Elements
    @FXML private Label locationNameLabel;
    @FXML private Label currentDateLabel;
    @FXML private Label weatherIconLabel;
    @FXML private Label temperatureLabel;
    @FXML private Label weatherDescLabel;
    
    // Detail labels
    @FXML private Label humidityLabel;
    @FXML private Label windLabel;
    @FXML private Label pressureLabel;
    @FXML private Label uvLabel;
    @FXML private Label visibilityLabel;
    @FXML private Label feelsLikeLabel;
    
    // AQI
    @FXML private Label aqiValueLabel;
    @FXML private Label aqiDescLabel;
    
    // Forecasts
    @FXML private HBox hourlyForecastContainer;
    @FXML private VBox dailyForecastContainer;
    @FXML private LineChart<String, Number> temperatureChart;

    private WeatherService weatherService;
    private SettingsService settingsService;
    
    public void setServices(WeatherService weatherService, SettingsService settingsService) {
        this.weatherService = weatherService;
        this.settingsService = settingsService;
        applyTheme();
        
        // Initial Fetch
        performSearch("London"); // Default or from settings
    }

    @FXML private Button navDashboard;
    @FXML private Button navSearch;
    @FXML private Button navMap;
    @FXML private Button navSettings;

    @FXML
    public void initialize() {
        // Setup listeners and actions
        themeToggleBtn.setOnAction(e -> toggleTheme());
        searchBtn.setOnAction(e -> handleSearchAction());
        searchField.setOnAction(e -> handleSearchAction());
        
        // Navigation (view switching)
        navDashboard.setOnAction(e -> setActiveNav(navDashboard));
        navSearch.setOnAction(e -> {
            setActiveNav(navDashboard); // Keep dashboard visible for search
            searchField.requestFocus();
        });
        navMap.setOnAction(e -> setActiveNav(navMap));
        navSettings.setOnAction(e -> setActiveNav(navSettings));
        
        // Setup Settings
        themeComboBox.getItems().addAll("DARK", "LIGHT");
        unitComboBox.getItems().addAll("METRIC", "IMPERIAL");
        saveSettingsBtn.setOnAction(e -> saveSettings());
        
        showLoading(true);
    }

    private void setActiveNav(Button activeBtn) {
        navDashboard.getStyleClass().remove("nav-btn-active");
        navSearch.getStyleClass().remove("nav-btn-active");
        navMap.getStyleClass().remove("nav-btn-active");
        navSettings.getStyleClass().remove("nav-btn-active");
        activeBtn.getStyleClass().add("nav-btn-active");
        
        if (activeBtn == navDashboard) {
            dashboardView.setVisible(true);
            settingsView.setVisible(false);
            mapView.setVisible(false);
        } else if (activeBtn == navSettings) {
            dashboardView.setVisible(false);
            settingsView.setVisible(true);
            mapView.setVisible(false);
            if (settingsService != null) {
                themeComboBox.setValue(settingsService.getTheme().name());
                unitComboBox.setValue(settingsService.getUnit().name());
            }
        } else if (activeBtn == navMap) {
            dashboardView.setVisible(false);
            settingsView.setVisible(false);
            mapView.setVisible(true);
        } else {
            // Placeholder for others
            dashboardView.setVisible(false);
            settingsView.setVisible(false);
            mapView.setVisible(false);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Navigation");
            alert.setHeaderText(activeBtn.getText() + " View");
            alert.setContentText("This section is currently under development. Please use the Dashboard for now.");
            alert.show();
            setActiveNav(navDashboard); // Revert
        }
    }
    
    private void saveSettings() {
        if (settingsService != null) {
            settingsService.setTheme(SettingsService.Theme.valueOf(themeComboBox.getValue()));
            settingsService.setUnit(SettingsService.Unit.valueOf(unitComboBox.getValue()));
            applyTheme();
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Settings saved!");
            alert.show();
        }
    }

    private void applyTheme() {
        if (settingsService != null) {
            if (settingsService.getTheme() == SettingsService.Theme.LIGHT) {
                if (!rootPane.getStyleClass().contains("light-theme")) {
                    rootPane.getStyleClass().add("light-theme");
                }
                themeToggleBtn.setText("🌙 Dark Mode");
            } else {
                rootPane.getStyleClass().remove("light-theme");
                themeToggleBtn.setText("☀ Light Mode");
            }
        }
    }

    private void toggleTheme() {
        if (settingsService == null) return;
        
        if (settingsService.getTheme() == SettingsService.Theme.DARK) {
            settingsService.setTheme(SettingsService.Theme.LIGHT);
        } else {
            settingsService.setTheme(SettingsService.Theme.DARK);
        }
        applyTheme();
    }

    private void handleSearchAction() {
        String query = searchField.getText().trim();
        if (query.isEmpty()) return;
        performSearch(query);
    }

    private void performSearch(String query) {
        if (weatherService == null) return;
        
        showLoading(true);
        weatherService.searchLocationAsync(query).thenCompose(locations -> {
            if (locations.isEmpty()) {
                throw new RuntimeException("Location not found");
            }
            return weatherService.getWeatherDataAsync(locations.get(0));
        }).thenAccept(data -> {
            updateUI(data);
        }).exceptionally(ex -> {
            Platform.runLater(() -> {
                showLoading(false);
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error fetching weather");
                alert.setHeaderText(null);
                alert.setContentText("Could not fetch weather data: " + ex.getMessage());
                alert.showAndWait();
            });
            return null;
        });
    }

    public void showLoading(boolean show) {
        Platform.runLater(() -> loadingOverlay.setVisible(show));
    }

    public void updateUI(WeatherData data) {
        Platform.runLater(() -> {
            try {
                // Update Headers
                locationNameLabel.setText(data.getLocation().getDisplayName());
                currentDateLabel.setText(java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy", Locale.ENGLISH)));
                
                // Current Weather
                CurrentWeather cw = data.getCurrentWeather();
                temperatureLabel.setText(Math.round(cw.getTemperature()) + "°");
                weatherDescLabel.setText(IconUtils.getWeatherDescription(cw.getWeatherCode()));
                weatherIconLabel.setText(IconUtils.getWeatherIcon(cw.getWeatherCode(), cw.isDay()));
                
                // Details
                humidityLabel.setText(Math.round(cw.getHumidity()) + "%");
                windLabel.setText(Math.round(cw.getWindSpeed()) + " km/h");
                pressureLabel.setText(Math.round(cw.getPressure()) + " hPa");
                uvLabel.setText(String.format("%.1f", cw.getUvIndex()));
                visibilityLabel.setText(String.format("%.1f km", cw.getVisibility()));
                feelsLikeLabel.setText(Math.round(cw.getFeelsLike()) + "°");
                
                // AQI
                AirQuality aqi = data.getAirQuality();
                if (aqi != null && aqi.getAqi() > 0) {
                    aqiValueLabel.setText(String.valueOf(aqi.getAqi()));
                    aqiDescLabel.setText(aqi.getHealthRecommendation());
                    
                    int aqiVal = aqi.getAqi();
                    if (aqiVal <= 50) {
                        aqiValueLabel.setStyle("-fx-text-fill: #38ef7d;");
                    } else if (aqiVal <= 100) {
                        aqiValueLabel.setStyle("-fx-text-fill: #f1c40f;");
                    } else if (aqiVal <= 150) {
                        aqiValueLabel.setStyle("-fx-text-fill: #e67e22;");
                    } else {
                        aqiValueLabel.setStyle("-fx-text-fill: #e74c3c;");
                    }
                } else {
                    aqiValueLabel.setText("--");
                    aqiValueLabel.setStyle("-fx-text-fill: -text-color;");
                    aqiDescLabel.setText("Data unavailable");
                }
                
                // Map
                if (mapWebView != null) {
                    double lat = data.getLocation().getLatitude();
                    double lon = data.getLocation().getLongitude();
                    String windyUrl = String.format(Locale.US, 
                        "https://embed.windy.com/embed2.html?lat=%.4f&lon=%.4f&detailLat=%.4f&detailLon=%.4f&width=800&height=600&zoom=6&level=surface&overlay=wind&product=ecmwf&menu=&message=&marker=true&calendar=now&pressure=&type=map&location=coordinates&detail=&metricWind=km%%2Fh&metricTemp=%%C2%%B0C&radarRange=-1", 
                        lat, lon, lat, lon);
                    mapWebView.getEngine().load(windyUrl);
                }
                
                // Hourly
                hourlyForecastContainer.getChildren().clear();
                DateTimeFormatter hourlyFormatter = DateTimeFormatter.ofPattern("HH:mm");
                for (HourlyForecast hf : data.getHourlyForecasts()) {
                    VBox item = new VBox(5);
                    item.getStyleClass().add("hourly-item");
                    Label time = new Label(hf.getTime().format(hourlyFormatter));
                    time.getStyleClass().add("hourly-time");
                    Label icon = new Label(IconUtils.getWeatherIcon(hf.getWeatherCode(), true)); // Simplification
                    icon.getStyleClass().add("hourly-icon");
                    Label temp = new Label(Math.round(hf.getTemperature()) + "°");
                    temp.getStyleClass().add("hourly-temp");
                    item.getChildren().addAll(time, icon, temp);
                    hourlyForecastContainer.getChildren().add(item);
                }
                
                // Daily
                dailyForecastContainer.getChildren().clear();
                DateTimeFormatter dailyFormatter = DateTimeFormatter.ofPattern("EEE");
                XYChart.Series<String, Number> series = new XYChart.Series<>();
                series.setName("Max Temp");
                
                for (DailyForecast df : data.getDailyForecasts()) {
                    HBox item = new HBox(15);
                    item.setAlignment(Pos.CENTER_LEFT);
                    item.getStyleClass().add("daily-item");
                    
                    Label day = new Label(df.getDate().format(dailyFormatter));
                    day.getStyleClass().add("daily-day");
                    Label icon = new Label(IconUtils.getWeatherIcon(df.getWeatherCode(), true));
                    icon.getStyleClass().add("daily-icon");
                    
                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);
                    
                    Label minTemp = new Label(Math.round(df.getMinTemp()) + "°");
                    minTemp.getStyleClass().add("daily-temp");
                    minTemp.setStyle("-fx-text-fill: #b0bec5;");
                    Label maxTemp = new Label(Math.round(df.getMaxTemp()) + "°");
                    maxTemp.getStyleClass().add("daily-temp");
                    
                    item.getChildren().addAll(day, icon, spacer, minTemp, maxTemp);
                    dailyForecastContainer.getChildren().add(item);
                    
                    series.getData().add(new XYChart.Data<>(df.getDate().format(dailyFormatter), df.getMaxTemp()));
                }
                
                // Chart
                temperatureChart.getData().clear();
                temperatureChart.getData().add(series);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                showLoading(false);
            }
        });
    }
}
