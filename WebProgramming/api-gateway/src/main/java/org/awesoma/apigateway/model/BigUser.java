package org.awesoma.apigateway.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@AllArgsConstructor
@Getter
public class BigUser implements Serializable {
    private User user;
    private JwtUser jwtUser;
}
