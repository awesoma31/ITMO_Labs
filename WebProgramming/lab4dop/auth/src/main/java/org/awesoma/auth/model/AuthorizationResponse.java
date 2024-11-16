package org.awesoma.auth.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthorizationResponse {
    private String accessToken;
    private String refreshToken;
    private long userId;
    private String username;

}
