package com.gembaadvantage.apollo.gatewayservice;

import java.util.concurrent.TimeUnit;

import com.gembaadvantage.apollo.gatewayservice.controller.FallbackResponse;
import org.junit.*;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.matchers.Times;
import org.mockserver.model.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.MockServerContainer;

import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import com.carrotsearch.junitbenchmarks.BenchmarkRule;

import static org.mockserver.model.HttpResponse.response;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@RunWith(SpringRunner.class)
public class CircuitBreakerTests {
    private static final Logger LOGGER = LoggerFactory.getLogger(CircuitBreakerTests.class);

    @Rule
    public TestRule benchmarkRun = new BenchmarkRule();

    @ClassRule
    public static MockServerContainer mockServer = new MockServerContainer();

    @Autowired
    private TestRestTemplate template;
    private int i = 0;

    @BeforeClass
    public static void init() {
        System.setProperty("spring.cloud.gateway.routes[0].id", "test-service");
        System.setProperty("spring.cloud.gateway.routes[0].uri", mockServer.getEndpoint());
        System.setProperty("spring.cloud.gateway.routes[0].predicates[0]", "Path=/test/**");
        System.setProperty("spring.cloud.gateway.routes[0].filters[0].name", "CircuitBreaker");
        System.setProperty("spring.cloud.gateway.routes[0].filters[0].args.name", "backendA");
        System.setProperty("spring.cloud.gateway.routes[0].filters[0].args.fallbackUri", "forward:/fallback/test");
        MockServerClient client = new MockServerClient(mockServer.getContainerIpAddress(), mockServer.getServerPort());
        client.when(HttpRequest.request()
                .withPath("/1"))
                .respond(response()
                        .withBody("{\"msgCode\":\"1\",\"msg\":\"1000000\"}")
                        .withHeader("Content-Type", "application/json"));
        client.when(HttpRequest.request()
                .withPath("/2"), Times.exactly(5))
                .respond(response()
                        .withBody("{\"msgCode\":\"2\",\"msg\":\"2000000\"}")
                        .withDelay(TimeUnit.MILLISECONDS, 5000)
                        .withHeader("Content-Type", "application/json"));
        client.when(HttpRequest.request()
                .withPath("/2"))
                .respond(response()
                        .withBody("{\"msgCode\":\"2\",\"msg\":\"2100000\"}")
                        .withHeader("Content-Type", "application/json"));
    }

    @Test
    @BenchmarkOptions(warmupRounds = 0, concurrency = 1, benchmarkRounds = 200)
    public void testTestService() {
        int gen = 1 + (i++ % 2);
        ResponseEntity r = template.exchange("/account/{id}", HttpMethod.GET, null, FallbackResponse.class, gen);
        LOGGER.info("{}. Received: status->{}, payload->{}, call->{}", i, r.getStatusCodeValue(), r.getBody(), gen);
    }
}
