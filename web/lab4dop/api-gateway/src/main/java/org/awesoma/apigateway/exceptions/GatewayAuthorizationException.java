package org.awesoma.apigateway.exceptions;

public class GatewayAuthorizationException extends RuntimeException {
    public GatewayAuthorizationException(String message) {
        super(message);
    }
}
