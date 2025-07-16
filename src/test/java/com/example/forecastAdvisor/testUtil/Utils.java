package com.example.forecastAdvisor.testUtil;

import com.example.forecastAdvisor.domain.HourlyData;
import com.example.forecastAdvisor.domain.WeatherInfoDTO;

import java.time.LocalDateTime;
import java.util.List;

public class Utils {
    public static WeatherInfoDTO buildWeather() {
        return WeatherInfoDTO.builder().hourly(
                HourlyData.builder()
                        .temperature2m(List.of(35.0, 35.7, 18.8, 22.0, 23.5, 27.0))
                        .time(List.of(
                                        LocalDateTime.parse("2025-06-23T12:00"),
                                        LocalDateTime.parse("2025-06-16T12:00"),
                                        LocalDateTime.parse("2025-08-08T12:00"),
                                        LocalDateTime.parse("2025-09-08T12:00"),
                                        LocalDateTime.parse("2025-10-08T12:00"),
                                        LocalDateTime.parse("2025-11-08T12:00")
                                )
                        ).build()).build();
    }
}
