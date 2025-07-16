# ForecastAdvisor 🌦️

A small Spring Boot project that provides weather forecast information based on a given ZIP code, using reactive WebClient and external APIs.

## 📦 Features

- Fetches coordinates by ZIP code using an external location API.
- Fetches weather forecast data using coordinates.
- Caches responses for performance optimization.
- Uses WebClient and Project Reactor (`Flux`, `Mono`) for non-blocking I/O.
- Includes integration tests with WireMock.

## 🛠️ Tech Stack

- Java 17+
- Spring Boot
- WebFlux (`WebClient`)
- Caffeine (optional cache)
- WireMock (for integration tests)
- JUnit 5

## 🚀 Getting Started

### Prerequisites

- Java 17+
- Maven

### Clone the project

```bash
git clone https://github.com/EleEVeryZe/weatherInfo.git
cd forecast-advisor
./mvnw clean install
./mvnw test
