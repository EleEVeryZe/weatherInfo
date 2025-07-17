package com.example.forecastAdvisor.output;

import com.example.forecastAdvisor.domain.LocationInfoDTO;
import com.example.forecastAdvisor.domain.WeatherInfoDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Service
class AddressAndLocationClient implements OutputClient {
    @Value("${ZIP_TO_COORD_DOMAIN}")
    private String ZIP_TO_COORD_DOMAIN;

    @Value("${WEATHER_DOMAIN}")
    private String WEATHER_DOMAIN;

    @Override
    public Flux<LocationInfoDTO> fetchLocationInfo(String zipCode) {
        WebClient client = WebClient.create(ZIP_TO_COORD_DOMAIN);
        return client
                .get()
                .uri(
                        urlBuilder ->
                                urlBuilder
                                        .path("/search")
                                        .queryParam("postalcode", zipCode)
                                        .queryParam("polygon_geojson", "1")
                                        .queryParam("format", "jsonv2")
                                        .build()
                ).retrieve()
                .bodyToFlux(LocationInfoDTO.class);

    }

    @Override
    public Mono<WeatherInfoDTO> fetchWeatherInfo(LocationInfoDTO locationInfoDTO) {
        WebClient client = WebClient.create(WEATHER_DOMAIN);
        return Objects.requireNonNull(client
                        .get()
                        .uri(
                                urlBuilder ->
                                        urlBuilder
                                                .path("/v1/forecast")
                                                .queryParam("latitude", locationInfoDTO.getLat())
                                                .queryParam("longitude", locationInfoDTO.getLon())
                                                .queryParam("hourly", "temperature_2m")
                                                .build()
                        ).retrieve().bodyToMono(WeatherInfoDTO.class));
    }
}
