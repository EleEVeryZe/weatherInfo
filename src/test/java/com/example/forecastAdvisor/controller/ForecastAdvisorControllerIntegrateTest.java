package com.example.forecastAdvisor.controller;

import com.example.forecastAdvisor.ForecastAdvisorApplication;
import com.example.forecastAdvisor.domain.LocationInfoDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.example.forecastAdvisor.testUtil.Utils.buildWeather;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = ForecastAdvisorApplication.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ForecastAdvisorControllerIntegrateTest {
    @Autowired
    private WebTestClient webTestClient;

    LocationInfoDTO location = LocationInfoDTO.builder()
            .lat("10.4923")
            .lon("42.4341")
            .displayName("Xique-Xique Bahia")
            .build();

    protected static WireMockServer wireMockServer;
    protected static final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeAll
    public static void setUpWireMock() {
        wireMockServer = new WireMockServer(8082);
        wireMockServer.start();
        WireMock.configureFor("localhost", 8082);
        objectMapper.registerModule(new JavaTimeModule());
    }

    @AfterEach
    public void resetWireMock() {
        wireMockServer.resetAll();
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
    @DisplayName("Should handle error of 4xx")
    @Order(1)
    void shouldHandleErrorOnApiCall4xxFamily() {
        wireMockServer.stubFor(
                WireMock.get(urlPathEqualTo("/search"))
                        .willReturn(
                                WireMock.aResponse()
                                        .withStatus(BAD_REQUEST.value())));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/weather-info")
                        .queryParam("zip-code", "30662")
                        .build())
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody()
                .consumeWith(System.out::println);
    }

    @Test()
    @DisplayName("Should handle error of 5xx")
    @Order(1)
    void shouldHandleErrorOnApiCall5xxFamily() {
        wireMockServer.stubFor(
                WireMock.get(urlPathEqualTo("/search"))
                        .willReturn(
                                WireMock.aResponse()
                                        .withStatus(INTERNAL_SERVER_ERROR.value())));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/weather-info")
                        .queryParam("zip-code", "30662")
                        .build())
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody()
                .consumeWith(System.out::println);
    }


    @Test()
    @DisplayName("Should retrieve weather information")
    @Order(2)
    void shouldRetrieveWeatherInfo() throws Exception {
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

    @Test()
    @DisplayName("Should retrieve cached weather information")
    @Order(3)
    void shouldRetrieveWeatherInfoCached() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/weather-info")
                        .queryParam("zip-code", "30662")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.cached").isEqualTo(true)
                .consumeWith(System.out::println);
    }
}
