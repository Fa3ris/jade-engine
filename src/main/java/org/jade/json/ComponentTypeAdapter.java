package org.jade.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import org.jade.ecs.Component;

public class ComponentTypeAdapter implements JsonDeserializer<Component>, JsonSerializer<Component> {

  private static final String typeKey = "type";
  private static final String propertiesKey = "properties";

  @Override
  public Component deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    String className = json.getAsJsonObject().get(typeKey).getAsString();
    Class<?> clazz;
    try {
      clazz = Class.forName(className);
    } catch (ClassNotFoundException e) {
      throw new JsonParseException(e);
    }
    return context.deserialize(json.getAsJsonObject().get(propertiesKey), clazz);
  }

  @Override
  public JsonElement serialize(Component src, Type typeOfSrc, JsonSerializationContext context) {
    JsonObject obj = new JsonObject();
    obj.add(typeKey,
        new JsonPrimitive(src.getClass().getCanonicalName()) // store actual class of the Component
    );
    obj.add(propertiesKey,
        context.serialize(src)  // will identify the correct class
    );
    return obj;
  }
}
