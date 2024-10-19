package org.awesoma.backend.ejb;

import jakarta.ejb.Stateless;

@Stateless
public class HelloWorldBean {

  public String sayHello() {
    return "Hello, World from EJB!";
  }
}
