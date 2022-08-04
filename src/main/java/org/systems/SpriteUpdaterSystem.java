package org.systems;

import java.util.List;
import org.components.SpriteComponent;
import org.jade.ecs.Entity;
import org.jade.ecs.System;

public class SpriteUpdaterSystem implements System {


  @Override
  public void update(double dt, List<Entity> entities) {
    for (Entity entity : entities) {
      SpriteComponent spriteComponent = entity.getComponent(SpriteComponent.class);

      if (spriteComponent != null) {

      }

    }
  }
}
