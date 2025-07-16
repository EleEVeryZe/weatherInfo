package com.example.forecastAdvisor.output;

import com.example.forecastAdvisor.domain.LocationInfoDTO;
import com.example.forecastAdvisor.domain.WeatherInfoDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OutputClient {
    Flux<LocationInfoDTO> fetchLocationInfo(String zipCode);
    Mono<WeatherInfoDTO> fetchWeatherInfo(LocationInfoDTO locationInfoDTO);
}
