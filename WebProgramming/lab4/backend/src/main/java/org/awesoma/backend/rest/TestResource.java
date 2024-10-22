package org.awesoma.backend.rest;

import jakarta.ejb.EJB;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.awesoma.backend.db.data.Result;
import org.awesoma.backend.db.data.User;
import org.awesoma.backend.db.exceptions.UsernameAlreadyExistsException;
import org.awesoma.backend.rest.services.DBService;
import org.awesoma.backend.rest.services.ResultService;

@Path("/test")
public class TestResource {

    @EJB
    private DBService DBService;

    @EJB
    private ResultService resultService;

    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll() {
        var usrs = DBService.getAllUsers();
        return Response.ok(usrs).build();
    }

    @POST
    @Path("/createTest")
    public Response createTestEntities() {
        try {
            // Create a user
            User user = new User();
            user.setUsername("testuser");
            user.setPassword("testpass");
            DBService.createUser(user);

            // Create a success
            Result result = new Result();
            result.setX(1.0);
            result.setY(2.0);
            result.setR(3.0);
            result.setResult(true);
            result.setOwner(user);
            resultService.createResult(result);

            return Response.ok("Entities created").build();

        } catch (UsernameAlreadyExistsException e) {
            // Handle the case where the username already exists
            return Response.status(Response.Status.CONFLICT)
                    .entity(e.getMessage())
                    .build();

        } catch (Exception e) {
            // Handle other unexpected exceptions
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("An unexpected error occurred: " + e.getMessage())
                    .build();
        }
    }
}
