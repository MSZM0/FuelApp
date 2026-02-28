# FuelApp

Android application for quickly calculating car trip costs and splitting expenses among passengers.
<p align="center"><img width="422" height="955" alt="Screenshot 2026-02-28 014252" src="https://github.com/user-attachments/assets/448176db-63e1-40ab-bd29-329391a54f4e" /></p>

## Features
- **Automatic price fetching:** Fetches current fuel prices (PB95, PB98, Diesel, LPG) from a mock API endpoint.
- **Emergency mode:** Option to manually enter the price in case of no internet connection.
- **Dynamic calculations:** Instant calculation of total cost and cost per person.
- **Material Design 3:** Modern interface with dark and light mode support.

## Technologies
- **Kotlin** + **Jetpack Compose** (User interface)
- **Retrofit 2** & **Gson** (Network communication and JSON handling)
- **Coroutines** (Asynchronous operations)

## How to run
1. Clone the repository:
   ```bash
   git clone https://github.com/MSZM0/FuelApp.git
   ```
2. Open the project in **Android Studio**.
3. Wait for the Gradle sync to finish.
4. Run the application on an emulator or a physical device (Android 7.0+).
