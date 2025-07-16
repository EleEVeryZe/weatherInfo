package com.example.forecastAdvisor.controller.response;

import com.example.forecastAdvisor.domain.WeatherInfoDTO;

import java.time.LocalDateTime;
import java.util.Map;

public record WeatherResponse (
        String latitude,
        String longitude,
        String currentTempeture,
        String maxTempeture,
        String minTempeture,
        Map<String, String> extendedForecasts,
        String displayName

) {
    public static WeatherResponse convertDtoToResponse(WeatherInfoDTO weatherInfoDTO, String displayName) {
        return new WeatherResponse(
                weatherInfoDTO.getLatitude(),
                weatherInfoDTO.getLongitude(),
                weatherInfoDTO.getTempetureOf(LocalDateTime.now()),
                weatherInfoDTO.getHighestTempeture(),
                weatherInfoDTO.getLowestTempeture(),
                weatherInfoDTO.getExtendedForecastOf(LocalDateTime.now()),
                displayName
        );
    }
}
