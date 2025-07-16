package com.example.forecastAdvisor.controller;

import com.example.forecastAdvisor.controller.response.CachedWeatherResponse;
import com.example.forecastAdvisor.service.ForecastService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class ForecastAdvisorController {
    @Autowired
    ForecastService forecastService;

    @GetMapping("/weather-info")
    Mono<CachedWeatherResponse> getWeatherInfo(@RequestParam("zip-code") String zipCode) {
        return forecastService.getCachedWeatherForecast(zipCode);
    }
}
