package com.example.forecastAdvisor.service;

import com.example.forecastAdvisor.domain.LocationInfoDTO;
import com.example.forecastAdvisor.output.OutputClient;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.hamcrest.MatcherAssert.assertThat;
import static com.example.forecastAdvisor.testUtil.Utils.buildWeather;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;


@SpringBootTest
public class ForecastServiceTest {
    @Mock
    private OutputClient adressAndLocationClientDOC;

    @Mock
    private CacheManager cacheManagerDOC;

    ForecastService forecastServiceSUT;

    @BeforeEach

    void setUp() {
        this.forecastServiceSUT = new ForecastServiceImpl(adressAndLocationClientDOC, cacheManagerDOC);
    }


    @DisplayName("Should retrieve the location coords from zip code and return Weather Forecasts")
    @Test
    void test_getWeatherForecast() {
        LocationInfoDTO mockedLocation = LocationInfoDTO.builder()
                .lat("10.4923")
                .lon("42.4341")
                .displayName("Xique-Xique Bahia")
                .build();

        when(adressAndLocationClientDOC.fetchLocationInfo(anyString())).thenReturn(Flux.just(mockedLocation));
        when(adressAndLocationClientDOC.fetchWeatherInfo(any())).thenReturn(Mono.just(buildWeather()));

        var cachedForecast = forecastServiceSUT.getCachedWeatherForecast("30662");
        assertThat(cachedForecast.block(), is(not(nullValue())));
        assertThat(cachedForecast.block().data().get(0).displayName(), equalTo(mockedLocation.getDisplayName()));
        assertThat(cachedForecast.block().data().get(0).minTempeture(), equalTo("18.8"));
        assertThat(cachedForecast.block().data().get(0).maxTempeture(), equalTo("35.7"));
    }
}
