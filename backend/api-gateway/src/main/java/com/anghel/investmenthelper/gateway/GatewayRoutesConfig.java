//package com.anghel.investmenthelper.gateway;
//
//import org.springframework.cloud.gateway.route.RouteLocator;
//import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class GatewayRoutesConfig {
//
//    @Bean
//    public RouteLocator customRoutes(RouteLocatorBuilder builder) {
//        return builder.routes()
//                .route("user-service-route", r -> r
//                        .path("/api/users/**")
//                        .uri("http://localhost:8081"))
//                .build();
//    }
//}