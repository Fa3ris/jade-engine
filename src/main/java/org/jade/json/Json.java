package org.jade.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public abstract class Json {

  private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

  public static String toJson(Object o) {
    return gson.toJson(o);
  }
}
