package org.awesoma.backend.rest;

import jakarta.ejb.EJB;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.awesoma.backend.ejb.HelloWorldBean;

@Path("/hello")
public class HelloWorldEndpoint {

  @EJB
  private HelloWorldBean helloWorldBean;

  @GET
  @Produces(MediaType.TEXT_PLAIN)
  public String sayHello() {
    return helloWorldBean.sayHello();
  }
}
