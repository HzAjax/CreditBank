package ru.volodin.gateway.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RoutingConfiguration {

    @Value("${gateway.remote.dealUrl}")
    private String dealUrl;

    @Value("${gateway.remote.statementUrl}")
    private String statementUrl;

    @Bean
    public RouteLocator myRoutes(RouteLocatorBuilder builder) {
        return builder.routes()

                .route("deal", r -> r
                        .path("/deal/**")
                        .filters(f -> f
                                .circuitBreaker(c -> c
                                        .setName("dealCircuitBreaker")
                                        .setFallbackUri("forward:/fallback/deal")))
                        .uri(dealUrl))

                .route("statement", r -> r
                        .path("/statement/**")
                        .filters(f -> f
                                .circuitBreaker(c -> c
                                        .setName("statementCircuitBreaker")
                                        .setFallbackUri("forward:/fallback/statement")))
                        .uri(statementUrl))

                .build();
    }
}
