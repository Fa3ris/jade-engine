package org.jade.ecs;

import java.util.HashMap;
import java.util.Map;

public class Entity {

  private final Map<String, Component> components = new HashMap<>();

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
