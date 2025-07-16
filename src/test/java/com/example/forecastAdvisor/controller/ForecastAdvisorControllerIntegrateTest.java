package com.example.forecastAdvisor.controller;

import com.example.forecastAdvisor.ForecastAdvisorApplication;
import com.example.forecastAdvisor.domain.HourlyData;
import com.example.forecastAdvisor.domain.LocationInfoDTO;
import com.example.forecastAdvisor.domain.WeatherInfoDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.example.forecastAdvisor.testUtil.Utils.buildWeather;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = ForecastAdvisorApplication.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ForecastAdvisorControllerIntegrateTest{

    @Autowired
    private WebTestClient webTestClient;

    protected static WireMockServer wireMockServer;
    protected static final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeAll
    public static void setUpWireMock() {
        wireMockServer = new WireMockServer(8082);
        wireMockServer.start();
        WireMock.configureFor("localhost", 8082);
        objectMapper.registerModule(new JavaTimeModule());
    }

    public static String parseObjectToStringJson(Object anObject) throws JsonProcessingException {
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        ObjectWriter ow = objectMapper.writer().withDefaultPrettyPrinter();
        return ow.writeValueAsString(anObject);
    }

    @AfterAll
    public static void shutDown() {
        wireMockServer.shutdown();
    }

    @Test()
    @DisplayName("Should retrieve weather information")
    void shouldRetrieveWeatherInfo() throws Exception {
        LocationInfoDTO location = LocationInfoDTO.builder()
                .lat("10.4923")
                .lon("42.4341")
                .displayName("Xique-Xique Bahia")
                .build();

        wireMockServer.stubFor(
                WireMock.get(urlPathEqualTo("/search"))
                        .willReturn(
                                WireMock.aResponse()
                                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                        .withBody(parseObjectToStringJson(location))
                                        .withStatus(OK.value())));

        wireMockServer.stubFor(
                WireMock.get(urlPathEqualTo("/v1/forecast"))
                        .willReturn(
                                WireMock.aResponse()
                                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                        .withBody(parseObjectToStringJson(buildWeather()))
                                        .withStatus(OK.value())));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/weather-info")
                        .queryParam("zip-code", "30662")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody()
                .consumeWith(System.out::println);
    }
}
