package org.systems;

import java.util.ArrayList;
import java.util.List;
import org.components.SpriteComponent;
import org.components.SpriteRenderer;
import org.components.SpriteRenderingInfo;
import org.jade.ecs.Entity;
import org.jade.ecs.System;
import org.jade.scenes.SceneManager;
import org.lwjgl.system.CallbackI.S;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RenderSystem implements System {

  List<SpriteRenderer> spriteRenderers = new ArrayList<>(5);

  private static final Logger logger = LoggerFactory.getLogger(RenderSystem.class);

  @Override
  public void update(double dt, List<Entity> entities) {

    for (Entity entity : entities) {

      SpriteComponent spriteComponent = entity.getComponent(SpriteComponent.class);
      if (spriteComponent == null) {
        logger.info("no sprite component");
        continue;
      }

      logger.info("found sprite component");

      SpriteRenderingInfo renderingInfo = entity.getComponent(SpriteRenderingInfo.class);

      if (renderingInfo == null) {
        logger.info("add sprite rendering info");
        entity.addComponent(new SpriteRenderingInfo());

        boolean added = false;
        for (SpriteRenderer spriteRenderer : spriteRenderers) {
          if (spriteRenderer.addSpriteComponent(spriteComponent)) {
            added = true;
            break;
          }
        }

        if (!added) {
          SpriteRenderer freshRenderer = new SpriteRenderer();
          freshRenderer.addSpriteComponent(spriteComponent);
          spriteRenderers.add(freshRenderer);
        }

      } else {
        logger.info("sprite rendering info already present");

      }
    }
      /*

      hypothesis assign and do not destroy an entity

      if entity has a sprite to render
          if entity has no renderer info
             create renderer info and add to entity as component
                 sprite renderer index
                 sprite renderer quad index
              add entity to a renderer - renderer must have the texture required

      if entity is dirty
          find renderer

            from entity get
              position and
              sprite info = texcoord + texture -> not really changing



       */
  }

  @Override
  public void render(List<Entity> entities) {
    System.super.render(entities);
  }
}
