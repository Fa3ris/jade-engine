package org.jade.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jade.ecs.Component;

public abstract class Json {

  private static final Gson gson = new GsonBuilder()
      .registerTypeAdapter(Component.class, new ComponentTypeAdapter())
      .setPrettyPrinting()
      .create();

  /**
   * consider component as a Component so that ComponentTypeAdapter is used
   * @param component
   * @return json representation
   */
  public static String serializeComponent(Component component) {
    return gson.toJson(component, Component.class);
  }

  /**
   * consider component as a Component so that ComponentTypeAdapter is used
   * @param json
   * @return the actual instance of the Component
   */
  public static Component deserializeAsComponent(String json) {
    return gson.fromJson(json, Component.class);
  }

  public static String toJson(Object o) {
    return gson.toJson(o);
  }

  public static <T> T fromJson(String json, Class<T> clazz) {
    return gson.fromJson(json, clazz);
  }
}
