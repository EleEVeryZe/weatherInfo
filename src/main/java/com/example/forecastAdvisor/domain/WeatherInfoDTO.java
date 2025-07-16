package com.example.forecastAdvisor.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeatherInfoDTO {
    private LocationInfoDTO locationInfoDTO;
    private HourlyData hourly;
    private String latitude;
    private String longitude;

    public String getTempetureOf(LocalDateTime desiredDateTime) {
        int i = 0;
        for (; i < hourly.getTime().size(); i++) {
            var savedTime = hourly.getTime().get(i);

            if (
                    savedTime.getYear() == desiredDateTime.getYear() &&
                    savedTime.getHour() == desiredDateTime.getHour() &&
                    savedTime.getMonth() == desiredDateTime.getMonth() &&
                    savedTime.getDayOfMonth() == desiredDateTime.getDayOfMonth())
                break;
        }

        return i == hourly.getTime().size() ? "N/A" : String.valueOf(hourly.getTemperature2m().get(i));
    }

    public String getHighestTempeture() {
        return Collections.max(hourly.getTemperature2m()).toString();
    }

    public String getLowestTempeture() {
        return Collections.min(hourly.getTemperature2m()).toString();
    }

    public Map<String, String> getExtendedForecastOf(LocalDateTime desiredDay) {
        Map<String, String> extendedForecasts = new LinkedHashMap<>();
        for (int i = 0; i < hourly.getTime().size(); i++) {
            var savedTime = hourly.getTime().get(i);
            if (savedTime.isAfter(desiredDay) && savedTime.getDayOfMonth() == desiredDay.getDayOfMonth())
                extendedForecasts.put(savedTime.toString(), hourly.getTemperature2m().get(i).toString());
        }
        return extendedForecasts;
    }
}
