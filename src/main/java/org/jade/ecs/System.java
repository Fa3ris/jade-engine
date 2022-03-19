package org.jade.ecs;

import java.util.List;

public interface System {

  default void update(double dt, List<Entity> entities) {}
  default void render(List<Entity> entities) {}
}
