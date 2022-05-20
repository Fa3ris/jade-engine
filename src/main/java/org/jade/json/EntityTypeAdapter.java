package org.jade.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Map;
import org.jade.ecs.Component;
import org.jade.ecs.Entity;

public final class EntityTypeAdapter implements JsonSerializer<Entity>, JsonDeserializer<Entity> {

  private static final String componentsFieldName = "components";
  private static final String nameFieldName = "name";
  private static final String idFieldName = "id";

  public static final EntityTypeAdapter instance = new EntityTypeAdapter();
  private EntityTypeAdapter() {}

  @Override
  public JsonElement serialize(Entity src, Type typeOfSrc, JsonSerializationContext context) {
    try {
      Field componentsField = Entity.class.getDeclaredField(componentsFieldName);
      Field nameField = Entity.class.getDeclaredField(nameFieldName);
      Field idField = Entity.class.getDeclaredField(idFieldName);
      componentsField.setAccessible(true);
      nameField.setAccessible(true);
      idField.setAccessible(true);
      JsonObject jsonObject = new JsonObject();
      jsonObject.add(nameFieldName, new JsonPrimitive((String) nameField.get(src)));
      jsonObject.add(idFieldName, new JsonPrimitive((long) idField.get(src)));
      Map<String, Component> map = (Map<String, Component>) componentsField.get(src);
      JsonArray componentsArray = new JsonArray();
      for (Component value : map.values()) {
        componentsArray.add(context.serialize(value, Component.class));
      }
      jsonObject.add(componentsFieldName, componentsArray);
      componentsField.setAccessible(false);
      nameField.setAccessible(false);
      idField.setAccessible(false);
      return jsonObject;
    } catch (NoSuchFieldException | IllegalAccessException e) {
      throw new RuntimeException("cannot serialize entity", e);
    }
  }

  @Override
  public Entity deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    Field componentsField;
    Field nameField;
    Field idField;
    try {
      componentsField = Entity.class.getDeclaredField(componentsFieldName);
      nameField = Entity.class.getDeclaredField(nameFieldName);
      idField = Entity.class.getDeclaredField(idFieldName);
      componentsField.setAccessible(true);
      nameField.setAccessible(true);
      idField.setAccessible(true);
    } catch (NoSuchFieldException e) {
      throw new RuntimeException("cannot deserialize Entity", e);
    }

    Entity entity = new Entity();
    JsonObject obj = json.getAsJsonObject();
    try {
      idField.set(entity, obj.get(idFieldName).getAsLong());
      nameField.set(entity, obj.get(nameFieldName).getAsString());
      for (JsonElement jsonElement : obj.get(componentsFieldName).getAsJsonArray()) {
        Component c = context.deserialize(jsonElement, Component.class);
        entity.addComponent(c);
      }
    } catch (IllegalAccessException e) {
      throw new RuntimeException("cannot deserialize Entity", e);
    }
    componentsField.setAccessible(false);
    nameField.setAccessible(false);
    idField.setAccessible(false);
    return entity;
  }
}
