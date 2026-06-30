# Installation Guide

## Prerequisites
- Java 21 or higher installed on your system.
- An active internet connection.

## Building from Source

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/WeatherFetch.git
   cd WeatherFetch
   ```

2. Build the project using Gradle:
   ```bash
   ./gradlew build
   ```
   *Note: On Windows, use `gradlew.bat build`.*

## Running the Application

To start the application directly via Gradle:
```bash
./gradlew run
```

## Creating a Distribution

To create a standalone distribution containing the executable and libraries:
```bash
./gradlew installDist
```
The distribution will be located in `build/install/WeatherFetch/`. You can run the application via the startup scripts in the `bin/` directory.
