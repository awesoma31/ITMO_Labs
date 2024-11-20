package org.awesoma.auth.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.antlr.v4.runtime.Token;
import org.awesoma.auth.services.TokenService;

import java.io.Serializable;

@AllArgsConstructor
@Getter
public class BigUser implements Serializable {
    private User user;
    private TokenService.JwtUser jwtUser;
}
