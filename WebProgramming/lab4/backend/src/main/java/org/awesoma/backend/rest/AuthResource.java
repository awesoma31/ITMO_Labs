package org.awesoma.backend.rest;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/auth")
public class AuthResource {
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response getAuth() {
    return Response.ok("auth").build();
  }
}
