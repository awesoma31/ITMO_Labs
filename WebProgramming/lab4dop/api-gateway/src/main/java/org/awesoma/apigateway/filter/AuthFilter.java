package org.awesoma.apigateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.awesoma.apigateway.exceptions.GatewayAuthorizationException;
import org.awesoma.apigateway.model.UserPojo;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Component
public class AuthFilter extends AbstractGatewayFilterFactory<AuthFilter.Config> {

    private final RouteValidator routeValidator;
    private final WebClient.Builder webClientBuilder;

    public AuthFilter(RouteValidator routeValidator, WebClient.Builder webClientBuilder) {
        super(Config.class);

        this.routeValidator = routeValidator;
        this.webClientBuilder = webClientBuilder;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return this::filterRequest;
    }

    private Mono<Void> filterRequest(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (routeValidator.isSecured.test(exchange.getRequest())) {
            log.info("Request on secured endpoint: {} {}", exchange.getRequest().getMethod(), exchange.getRequest().getURI());
            if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            var authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                var token = authHeader.substring(7);

                return webClientBuilder.build().get()
                        .uri("lb://auth/auth/authorize")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .retrieve()
                        .onStatus(status -> status.equals(HttpStatus.FORBIDDEN), resp -> {
                            log.error("Forbidden auth server response: {}", resp.toString());
                            return Mono.error(new GatewayAuthorizationException("Access is forbidden"));
                        })
                        .onStatus(status -> status.equals(HttpStatus.UNAUTHORIZED), resp -> {
                            log.error("Unauthorized auth server response: {}", resp.toString());
                            return Mono.error(new GatewayAuthorizationException("Access is forbidden, unauthorized"));
                        })
                        .bodyToMono(UserPojo.class)
                        .flatMap(userPojo -> exchange.getRequest().getBody()
                                .collectList()
                                .flatMap(dataBuffers -> {
                                    StringBuilder bodyBuilder = readRequestBody(dataBuffers);

                                    try {
                                        String originalBody = bodyBuilder.toString();
                                        if (userPojo == null || userPojo.getId() == null || userPojo.getUsername() == null) {
                                            log.error("NULL fields in body: {}", userPojo);
                                            return Mono.error(new GatewayAuthorizationException("NULL fields in body"));
                                        }

                                        String modifiedBody = addFieldsToOriginalJson(originalBody, userPojo);

                                        byte[] newBytes = modifiedBody.getBytes(StandardCharsets.UTF_8);
                                        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(newBytes);

                                        ServerHttpRequest mutatedRequest = new ServerHttpRequestDecorator(exchange.getRequest()) {
                                            @NonNull
                                            @Override
                                            public Flux<DataBuffer> getBody() {
                                                return Flux.just(buffer);
                                            }

                                            @NonNull
                                            @Override
                                            public HttpHeaders getHeaders() {
                                                HttpHeaders headers = new HttpHeaders();
                                                headers.putAll(super.getHeaders());
                                                headers.setContentLength(newBytes.length);
                                                headers.setContentType(MediaType.APPLICATION_JSON);
                                                return headers;
                                            }
                                        };

                                        return chain.filter(exchange.mutate().request(mutatedRequest).build());

                                    } catch (Exception e) {
                                        return Mono.error(e);
                                    }
                                }))
                        .onErrorResume(e -> {
                            log.error("Error during authorization: {}", e.getMessage());
                            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                            return exchange.getResponse().setComplete();
                        });
            } else {
                throw new GatewayAuthorizationException("No Bearer provided");
            }

        }

        return chain.filter(exchange);
    }

    private static StringBuilder readRequestBody(List<DataBuffer> dataBuffers) {
        StringBuilder bodyBuilder = new StringBuilder();
        dataBuffers.forEach(buffer -> {
            byte[] bytes = new byte[buffer.readableByteCount()];
            buffer.read(bytes);
            DataBufferUtils.release(buffer);
            bodyBuilder.append(new String(bytes, StandardCharsets.UTF_8));
        });
        return bodyBuilder;
    }

    private static String addFieldsToOriginalJson(String originalBody, UserPojo pojo) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode originalNode;

        if (originalBody.isBlank()) {
            originalNode = objectMapper.createObjectNode();
        } else {
            originalNode = objectMapper.readTree(originalBody);
        }

        ObjectNode updatedNode = (ObjectNode) originalNode;

        updatedNode.put("id", pojo.getId());
        updatedNode.put("username", pojo.getUsername());

        return objectMapper.writeValueAsString(updatedNode);
    }

    public static class Config {
    }
}