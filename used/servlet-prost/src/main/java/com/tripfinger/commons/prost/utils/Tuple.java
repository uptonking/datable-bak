package com.tripfinger.commons.prost.utils;

public class Tuple<X, Y> {
  public X x;
  public Y y;

  public Tuple() {}

  public Tuple(X x, Y y) {
    this.x = x;
    this.y = y;
  }
}
