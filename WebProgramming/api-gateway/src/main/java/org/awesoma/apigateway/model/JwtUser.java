package org.awesoma.apigateway.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public final class JwtUser {
    private final Long id;
    private final String username;
    @Setter
    private String accessToken = null;
    @Setter
    private String refreshToken = null;

    public JwtUser(Long id, String username, String accessToken, String refreshToken) {
        this.id = id;
        this.username = username;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public JwtUser(Long id, String username) {
        this.id = id;
        this.username = username;
    }

    public JwtUser(Long id, String username, String token) {
        this.id = id;
        this.username = username;
        this.accessToken = token;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (JwtUser) obj;
        return Objects.equals(this.id, that.id) &&
                Objects.equals(this.username, that.username) &&
                Objects.equals(this.accessToken, that.accessToken) &&
                Objects.equals(this.refreshToken, that.refreshToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, accessToken, refreshToken);
    }

    @Override
    public String toString() {
        return "JwtUser[" +
                "id=" + id + ", " +
                "username=" + username + ", " +
                "accessToken=" + accessToken + ", " +
                "refreshToken=" + refreshToken + ']';
    }

}