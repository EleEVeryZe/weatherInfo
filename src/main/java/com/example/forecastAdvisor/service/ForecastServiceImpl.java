package com.example.forecastAdvisor.service;

import com.example.forecastAdvisor.controller.response.CachedWeatherResponse;
import com.example.forecastAdvisor.controller.response.WeatherResponse;
import com.example.forecastAdvisor.output.OutputClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class ForecastServiceImpl implements ForecastService {
    private OutputClient adressAndLocationClient;
    private CacheManager cacheManager;

    public ForecastServiceImpl(OutputClient adressAndLocationClient, CacheManager cacheManager) {
        this.cacheManager = cacheManager;
        this.adressAndLocationClient = adressAndLocationClient;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Mono<CachedWeatherResponse> getCachedWeatherForecast(String zipCode) {
        Cache cache = cacheManager.getCache("forecast");

        if (cache != null) {
            var rawCached = (List<WeatherResponse>) cache.get(zipCode, Object.class);
            if (rawCached != null)
                return Mono.just(new CachedWeatherResponse(rawCached, true));
        }

        var coords = adressAndLocationClient.fetchLocationInfo(zipCode);
        var forecastListMono = coords.flatMap(coord ->
                adressAndLocationClient.fetchWeatherInfo(coord)
                        .map(wInf ->
                                WeatherResponse.convertDtoToResponse(wInf, coord.getDisplayName())
                        )
        )
        .collectList()
        .doOnNext(fcst -> {
            if (cache != null)
                cache.put(zipCode, fcst);
        });

        return forecastListMono.map(list -> new CachedWeatherResponse(list, false));
    }
}
