package org.awesoma.backend.rest.services;

import com.sun.istack.NotNull;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import org.apache.commons.codec.digest.DigestUtils;
import org.awesoma.backend.db.data.User;
import org.awesoma.backend.util.AuthResponse;

import java.util.Optional;

@Stateless
public class AuthService {
    @EJB
    private TokenService tokenService;
    @EJB
    private DBService DBService;

    private static String encode(String s) {
        return DigestUtils.sha256Hex(s);
    }

    public AuthResponse login(@NotNull String username, @NotNull String password) {
        final Optional<User> optionalUser = DBService.findByUsername(username);
        if (optionalUser.isPresent()) {
            if (optionalUser.get().getPassword().equals(encode(password))) {
                //todo refresh token impl
                return new AuthResponse(
                        true,
                        tokenService.generateAccessToken(username),
                        tokenService.generateRefreshToken(username),
                        ""
                );
            } else {
                return new AuthResponse(false, "", "", "Wrong password");
            }
        } else {
            return new AuthResponse(false, "", "", "User not found");
        }
    }

    public AuthResponse register(@NotNull String username, @NotNull String password) {
        if (DBService.userExists(username)) {
            return new AuthResponse(false, "", "", "User already exists");
        } else {
            DBService.createUser(new User(username, encode(password)));
            return new AuthResponse(
                    true,
                    tokenService.generateAccessToken(username),
                    tokenService.generateRefreshToken(username),
                    ""
            );
        }
    }

    public AuthResponse newToken(String subject) {
        var tok = tokenService.generateAccessToken(subject);
        var refresh_tok = tokenService.generateRefreshToken(subject);
        return new AuthResponse(true, tok, refresh_tok, "");
    }

    public Optional<String> getUsernameByToken(String token) {
        return tokenService.verify(token);
    }
}
