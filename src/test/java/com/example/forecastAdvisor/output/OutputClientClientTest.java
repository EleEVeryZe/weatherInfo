package com.example.forecastAdvisor.output;

import com.example.forecastAdvisor.domain.LocationInfoDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@SpringBootTest
@TestPropertySource("classpath:application.properties")
public class OutputClientClientTest {

    @Autowired OutputClient addressInfoSUT;

    @Test
    void shouldRetrieveCoord() {
        var coords = addressInfoSUT.fetchLocationInfo("30662");
        assertThat(coords.collectList().block().size(), is(greaterThan(0)));
        assertThat(coords.collectList().block().get(0), is(not(nullValue())));
    }

    @Test
    void shouldRetrieveWeatherInfo() {
        var weatherInfo = addressInfoSUT.fetchWeatherInfo(LocationInfoDTO.builder().lat("-19.9208").lon("-43.9378").build());
        assertThat(weatherInfo, is(not(nullValue())));
    }
}
