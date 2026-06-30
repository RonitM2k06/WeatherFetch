# Developer Guide

## Project Setup
Import the project into your IDE (IntelliJ IDEA, Eclipse, or VS Code) as a Gradle project. The IDE should automatically resolve dependencies defined in `build.gradle.kts`.

## Code Guidelines
- **SOLID Principles:** Ensure classes have a single responsibility.
- **Null Safety:** Use Optional where appropriate or explicitly mark nullable fields.
- **Asynchrony:** All network and disk I/O must be performed asynchronously to keep the JavaFX UI thread responsive. Use `CompletableFuture`.

## Testing
We use JUnit 5 and Mockito.
To run tests:
```bash
./gradlew test
```
Tests are located in `src/test/java`. We aim for a minimum of 80% coverage.

## UI Modifications
- FXML files are in `src/main/resources/fxml`.
- Use SceneBuilder for drag-and-drop FXML editing, but refine the XML manually for clean code.
- CSS is located in `src/main/resources/css/style.css`.
