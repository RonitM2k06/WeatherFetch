package com.weatherfetch;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        okhttp3.OkHttpClient httpClient = new okhttp3.OkHttpClient();
        com.weatherfetch.api.OpenMeteoApiClient apiClient = new com.weatherfetch.api.OpenMeteoApiClient(httpClient, mapper);
        com.weatherfetch.service.WeatherService weatherService = new com.weatherfetch.service.WeatherService(apiClient);
        com.weatherfetch.service.SettingsService settingsService = new com.weatherfetch.service.SettingsService();

        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/fxml/main.fxml"));
        try {
            Parent root = fxmlLoader.load();
            com.weatherfetch.controller.MainController controller = fxmlLoader.getController();
            controller.setServices(weatherService, settingsService);
            
            Scene scene = new Scene(root, 1000, 700);
            stage.setTitle("WeatherFetch");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            System.err.println("Failed to load /fxml/main.fxml");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
