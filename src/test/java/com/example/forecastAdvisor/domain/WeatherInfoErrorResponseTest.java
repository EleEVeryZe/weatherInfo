package com.example.forecastAdvisor.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static com.example.forecastAdvisor.testUtil.Utils.buildWeather;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@SpringBootTest
public class WeatherInfoErrorResponseTest {
    WeatherInfoDTO mockedApiResponse = buildWeather();

    @Test
    @DisplayName("Should inform tempeture based on desired LocalDateTime")
    void test_getTempetureOf() {
        assertThat(mockedApiResponse.getTempetureOf(LocalDateTime.parse("2025-06-23T12:00")), is(equalTo("35.0")));
        assertThat(mockedApiResponse.getTempetureOf(LocalDateTime.parse("2027-06-23T12:00")), is(equalTo("N/A")));
    }

    @Test
    @DisplayName("Should get the lowest and highest tempeture")
    void test_getHighestTempeture() {
        assertThat(mockedApiResponse.getHighestTempeture(), is(equalTo("35.7")));
        assertThat(mockedApiResponse.getLowestTempeture(), is(equalTo("18.8")));
    }
}

