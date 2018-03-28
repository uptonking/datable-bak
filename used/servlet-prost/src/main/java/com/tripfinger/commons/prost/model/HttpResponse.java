package com.tripfinger.commons.prost.model;

public class HttpResponse {

  public int status = 200;
  public String body;
  public String contentType = "application/json";

  public HttpResponse() {}

  public HttpResponse(Integer status, String body) {
    this.status = status;
    this.body = body;
  }

    public HttpResponse(Integer status, String message, String id) {
    this.status = status;
    this.body = String.format("{\"status\": %d, \"message\": \"%s\", \"id\": \"%s\"}", status, message, id);
  }

  public static class ResponseObject {
    public int status;
    public String message;
    public String id;
  }
}
