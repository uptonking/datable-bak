package com.tripfinger.commons.prost.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class StreamUtils {

  public static byte[] readBytesFromInputStream(InputStream input) {
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();

    int nRead;
    byte[] data = new byte[16384];

    try {
      while ((nRead = input.read(data, 0, data.length)) != -1) {
        buffer.write(data, 0, nRead);
      }

      buffer.flush();
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }

    return buffer.toByteArray();
  }

  public static String readStringFromInputStream(InputStream input) {
    Scanner s = new Scanner(input, StandardCharsets.UTF_8.name()).useDelimiter("\\A");
    return s.hasNext() ? s.next() : "";
  }
}
