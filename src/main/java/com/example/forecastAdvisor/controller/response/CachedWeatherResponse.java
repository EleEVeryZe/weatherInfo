package com.example.forecastAdvisor.controller.response;

import java.util.List;

public record CachedWeatherResponse(List<WeatherResponse> data, boolean cached) {}

