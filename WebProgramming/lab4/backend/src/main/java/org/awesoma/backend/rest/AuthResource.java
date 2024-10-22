package org.awesoma.backend.rest;

import jakarta.ejb.EJB;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.awesoma.backend.rest.json.Credentials;
import org.awesoma.backend.rest.services.AuthService;
import org.awesoma.backend.util.AuthResponse;

@Path("/auth")
public class AuthResource {
    @EJB
    private AuthService authService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAuth() {
        return Response.ok("auth works!").build();
    }

    @Path("/reg")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response register(Credentials credentials) {
        AuthResponse r = authService.register(credentials.getUsername(), credentials.getPassword());
        if (r.success()) {
            //todo token in cookie
            return Response.ok(
                            tokenJSON(r.accessToken(), r.refreshToken())
                    )
                    .build();
        } else {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(r.error())
                    .build();
        }
    }

    @Path("/refresh")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response refresh(JsonObject json) {
        var refreshToken = json.getString("refreshToken");
        //todo ref token validation
        AuthResponse r = authService.newToken(refreshToken);

        if (r.success()) {
            JsonObject responseJson = Json.createObjectBuilder()
                    .add("accessToken", r.accessToken())
                    .add("refreshToken", r.refreshToken())  // Optionally return a new refresh token if required
                    .build();

            return Response.ok(responseJson).build();
        } else {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(r.error())
                    .build();
        }
    }

    @Path("/login")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response login(Credentials credentials) {
        AuthResponse r = authService.login(credentials.getUsername(), credentials.getPassword());
        if (r.success()) {
            JsonObject a = Json.createObjectBuilder()
                    .add("accessToken", r.accessToken())
                    .add("refreshToken", r.refreshToken())
                    .build();

            return Response.ok(a).build();
        } else {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(r.error())
                    .build();
        }
    }

    private String tokenJSON(String accessToken, String refreshToken) {
        return Json.createObjectBuilder()
                .add("accessToken", accessToken)
                .add("refreshToken", refreshToken)
                .build().toString();
    }
}
