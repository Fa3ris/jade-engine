package org.jade.ecs;

import imgui.ImGui;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Entity {

  private static final Logger logger = LoggerFactory.getLogger(Entity.class);

  private final Map<String, Component> components = new HashMap<>();

  private String name = "dummy";

  // return Optional ?
  public <T extends Component> T getComponent(Class<T> clazz) {
    Component c =  components.get(clazz.getName());
    if (c != null && clazz.isAssignableFrom(c.getClass())) {
      return clazz.cast(c);
    }
    return null;
  }

  public <T extends Component> T removeComponent(Class<T> clazz) {
    Component c = components.remove(clazz.getName());
    if (c != null && clazz.isAssignableFrom(c.getClass())) {
      return clazz.cast(c);
    }
    return null;
  }

  // pbl can have only 1 component of a given type
  public void addComponent(Component c) {
    logger.info("add component {}", c.getClass().getName());
    components.put(c.getClass().getName(), c);
    c.entity = this;
  }

  @Deprecated
  public void update(double dt) {
    for (Component component : components.values()) {
      component.update(dt);
    }
  }

  @Deprecated
  public void start() {
    for (Component component : components.values()) {
      component.start();
    }
  }

  public void imGui() {
    ImGui.text(String.format("entity name: %s", name));
    for (Component component : components.values()) {
      component.imGui();
    }

    String json = Json.serializeEntity(this);
    java.lang.System.out.println(json);

    Entity deserialized = Json.deserializeAsEntity(json);
    java.lang.System.out.println(deserialized);

  }
}
