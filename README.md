<div align="center">
  <img src="logo/placeholder.png" alt="WeatherFetch Logo" width="150" />
  
  # WeatherFetch
  **A beautiful, flagship Java desktop application for real-time global weather tracking.**

  [![Java](https://img.shields.io/badge/Java-21-blue.svg)](https://openjdk.java.net/)
  [![JavaFX](https://img.shields.io/badge/JavaFX-21-orange.svg)](https://openjfx.io/)
  [![Gradle](https://img.shields.io/badge/Gradle-8.7-02303A.svg?logo=gradle)](https://gradle.org/)
  [![License: MIT](https://img.shields.io/badge/License-MIT-green.svg)](https://opensource.org/licenses/MIT)
  [![Build Status](https://img.shields.io/badge/build-passing-brightgreen.svg)]()
</div>

<p align="center">
  <img src="banner/placeholder.png" alt="WeatherFetch Banner" width="100%" />
</p>

## 📖 Project Overview
WeatherFetch is a premium, flagship-tier Java desktop application built to showcase modern Java development. It leverages a Glassmorphism and Fluent UI design language via JavaFX to deliver a stunning experience. This app connects to real-world free APIs to fetch instantaneous weather data, interactive global weather maps, and complex environmental metrics anywhere in the world.

Whether you're a recruiter reviewing clean software architecture or a user tracking a storm, WeatherFetch delivers.

## ✨ Features
- 🌩️ **Real-time Weather:** Current conditions, temperature, humidity, UV index, wind speed, pressure, and visibility.
- 🔮 **Advanced Forecasting:** Horizontal hourly scrolls and detailed 7-day future predictions.
- 🗺️ **Interactive Radar Map:** A live, WebGL-powered interactive global weather map (via Windy) injected seamlessly into the Java environment.
- 💨 **Air Quality Index:** Color-coded dynamic text evaluating respiratory risks using European and US standards.
- 🌙 **Dark/Light Themes:** Seamless hot-swapping between responsive, fully styled themes.
- 📊 **Dynamic Charts:** Visualized temperature trends using native JavaFX charting capabilities.

## 📸 Screenshots
*(Placeholder for UI screenshots. Replace `screenshots/placeholder.png` with actual app images.)*
<p align="center">
  <img src="screenshots/placeholder.png" alt="Dashboard View" width="800" />
</p>

## 🏗️ Architecture
WeatherFetch follows **Clean Architecture** and **SOLID principles**, isolating the UI, domain models, and external APIs for supreme testability.

Read the [Detailed Architecture Guide](docs/Architecture.md).

## 🚀 Technology Stack
- **Core:** Java 21 LTS
- **UI Framework:** JavaFX 21 (FXML + CSS + WebView)
- **Build System:** Gradle (Kotlin DSL)
- **HTTP Client:** OkHttp3
- **JSON Serialization:** Jackson Databind
- **Logging:** SLF4J + Logback
- **Testing:** JUnit 5 + Mockito

## ⚙️ Installation & Running the Project

### Prerequisites
- JDK 21+ installed and configured on your system.
- Git (optional, for cloning).

### Running Locally
You don't need to manually install JavaFX or OkHttp; Gradle handles everything.

```bash
# Clone the repository
git clone https://github.com/yourusername/WeatherFetch.git
cd WeatherFetch

# Build the project
./gradlew build

# Run the application
./gradlew run
```

## 📁 Project Structure
Read the [Project Structure Guide](docs/ProjectStructure.md) for an in-depth breakdown of the directory organization.

## 🔌 API Information
WeatherFetch is entirely powered by free, NO-KEY required APIs.
- **Open-Meteo**: Core weather data, forecasting, geocoding, and AQI tracking.
- **Windy**: Weather Map Embeds.

Read the [API Documentation](docs/API.md) for endpoint specifics.

## 🔮 Future Improvements
- [ ] Implement system tray notifications for severe weather alerts.
- [ ] Add GPS auto-location detection.
- [ ] Integrate advanced historical weather data plotting.

## 🤝 Contributing
Contributions are always welcome! See [CONTRIBUTING.md](CONTRIBUTING.md) for ways to get started.
Please adhere to this project's [Code of Conduct](CODE_OF_CONDUCT.md).

## 🛡️ Security
If you discover any security related issues, please refer to our [Security Policy](SECURITY.md).

## 📝 License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
