package org.jade.ecs;

import java.util.HashMap;
import java.util.Map;
import org.joml.Vector3f;

public class Entity {

  // which renderer has the correct texture of the sprite ?
  int spriteRendererId = -1;

  // at which index rebuffer the data ?
  // when create
  int vboIndex = -1;

  boolean needToRender;

  String spriteName;

  // TODO extract to other component - unrelated to the sprite itself
  private float topPos, leftPos, rightPos, bottomPos;


  Vector3f topLeft() {
    return new Vector3f();
  }

  Vector3f bottomRight() {
    return new Vector3f();
  }

  private final Map<String, Component> components = new HashMap<>();

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
    components.put(c.getClass().getName(), c);
    c.entity = this;
  }

  public void update(double dt) {
    for (Component component : components.values()) {
      component.update(dt);
    }
  }

  public void start() {
    for (Component component : components.values()) {
      component.start();
    }
  }

}
