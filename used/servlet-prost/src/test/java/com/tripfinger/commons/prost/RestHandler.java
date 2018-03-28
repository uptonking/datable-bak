package com.tripfinger.commons.prost;

import com.tripfinger.commons.prost.annotations.Guard;
import com.tripfinger.commons.prost.annotations.RestMethod;
import com.tripfinger.commons.prost.model.HttpMethod;
import com.tripfinger.commons.prost.model.HttpResponse;

public class RestHandler {

  @RestMethod("/apple")
  public static HttpResponse getFruits() {
    return new HttpResponse(200, "Apple", null);
  }

  @Guard
  @RestMethod("/apple2")
  public static HttpResponse getFruits2() {
    return new HttpResponse(200, "Apple", null);
  }

  @RestMethod("/hello/:name")
  public static HttpResponse sayHello(String name) {
    HttpResponse response = new HttpResponse();
    response.body = "Hello, " + name;
    return response;
  }

  @RestMethod(method = HttpMethod.POST, value = "/bye/:name")
  public static HttpResponse sayBye(String name, String body) {
    HttpResponse response = new HttpResponse();
    Integer times = Integer.parseInt(body);
    response.body = "Bye, ";
    for (int i = 1; i < times; i++) {
      response.body += "bye, ";
    }
    response.body = response.body + name;
    return response;
  }

}
