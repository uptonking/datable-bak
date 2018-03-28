package com.tripfinger.commons.prost;

import com.tripfinger.commons.prost.model.Authorizer;
import com.tripfinger.commons.prost.model.HttpMethod;
import com.tripfinger.commons.prost.model.HttpResponse;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;

public class RequestHandlerTest {

  protected static RequestHandler requestHandler = new RequestHandler();

  @Before
  public void setUp() {
    RequestHandler.restHandlers = new HashMap<>();
    RequestHandler.authorizer = null;
    RequestHandler.guardedMethods = new HashSet<>();
  }

  @Test
  public void testRequestHandler() {

    requestHandler.setRestHandler(RestHandler.class);

    Map<String, String> paramaters = new HashMap<>();
    List<String> pathElements = Arrays.asList("apple");
    HttpResponse response = requestHandler.handleRequest(pathElements, paramaters);
    assertEquals(200, response.status);
    assertEquals(String.format("{\"status\": %d, \"message\": \"%s\", \"id\": \"null\"}", 200, "Apple"), response.body);

    pathElements = Arrays.asList("hello", "Boy");
    response = requestHandler.handleRequest(pathElements, paramaters);
    assertEquals(200, response.status);
    assertEquals("Hello, Boy", response.body);

    pathElements = Arrays.asList("bye", "Mama");
    response = requestHandler.handleRequest(pathElements, "3");
    assertEquals(200, response.status);
    assertEquals("Bye, bye, bye, Mama", response.body);
  }

  private static class SimpleAuthorizer implements Authorizer {

    public boolean isAuthorized() {
      return false;
    }
  }

  @Test
  public void testHttpMethodGuards() {

    requestHandler.setRestHandler(RestHandler.class);
    requestHandler.setAuthorizer(new SimpleAuthorizer());
    requestHandler.addMethodGuard(HttpMethod.GET);

    Map<String, String> paramaters = new HashMap<>();
    List<String> pathElements = Arrays.asList("hello", "Boy");
    HttpResponse response = requestHandler.handleRequest(pathElements, paramaters);
    assertEquals(401, response.status);
  }

  @Test
  public void testMethodGuards() {

    requestHandler.setRestHandler(RestHandler.class);
    requestHandler.setAuthorizer(new SimpleAuthorizer());

    Map<String, String> paramaters = new HashMap<>();
    List<String> pathElements = Arrays.asList("apple2");
    HttpResponse response = requestHandler.handleRequest(pathElements, paramaters);
    assertEquals(401, response.status);

  }

  @Test
  public void testClassGuards() {

    requestHandler.setRestHandler(GuardedRestHandler.class);
    requestHandler.setAuthorizer(new SimpleAuthorizer());

    Map<String, String> paramaters = new HashMap<>();
    List<String> pathElements = Arrays.asList("apple");
    HttpResponse response = requestHandler.handleRequest(pathElements, paramaters);
    assertEquals(200, response.status);

    pathElements = Arrays.asList("hello", "Boy");
    response = requestHandler.handleRequest(pathElements, paramaters);
    assertEquals(401, response.status);
  }
}
