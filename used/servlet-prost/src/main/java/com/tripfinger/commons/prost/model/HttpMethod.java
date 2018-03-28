package com.tripfinger.commons.prost.model;

public enum HttpMethod {
  GET("GET"),
  POST("POST"),
  DELETE("DELETE");

  public final String value;
  HttpMethod(final String newValue) {
    value = newValue;
  }
}
