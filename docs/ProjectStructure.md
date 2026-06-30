# Project Structure

The repository follows standard Maven/Gradle layout conventions for Java applications.

```
WeatherFetch/
├── assets/                     # GitHub repository assets (images, banners)
├── docs/                       # Comprehensive documentation
├── gradle/                     # Gradle wrapper files
├── src/
│   ├── main/
│   │   ├── java/com/weatherfetch/
│   │   │   ├── api/            # API Clients and DTOs (e.g. OpenMeteoApiClient)
│   │   │   ├── controller/     # JavaFX UI Controllers (MainController)
│   │   │   ├── model/          # Domain models (WeatherData, AirQuality, etc.)
│   │   │   ├── service/        # Core business logic (WeatherService, SettingsService)
│   │   │   ├── util/           # Utility classes (IconUtils, JSON mappers)
│   │   │   ├── App.java        # JavaFX Application Main Class
│   │   │   └── Main.java       # JVM Entry Point wrapper
│   │   └── resources/
│   │       ├── css/            # JavaFX CSS stylesheets
│   │       ├── fxml/           # UI layout configurations
│   │       └── icons/          # Application icons and vectors
│   └── test/
│       ├── java/com/weatherfetch/
│       │   └── service/        # Unit tests for business logic
├── .gitignore                  # Ignored file patterns
├── build.gradle.kts            # Gradle build configuration
└── settings.gradle.kts         # Gradle project settings
```
