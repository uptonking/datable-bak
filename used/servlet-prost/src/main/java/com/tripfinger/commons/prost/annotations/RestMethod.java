package com.tripfinger.commons.prost.annotations;

import com.tripfinger.commons.prost.model.HttpMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RestMethod {
  String value();
  HttpMethod method() default HttpMethod.GET;
}
