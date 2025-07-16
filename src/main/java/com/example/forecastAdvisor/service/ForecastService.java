package com.example.forecastAdvisor.service;
import com.example.forecastAdvisor.controller.response.CachedWeatherResponse;
import reactor.core.publisher.Mono;

public interface ForecastService {
    Mono<CachedWeatherResponse> getCachedWeatherForecast(String zipCode);
}
