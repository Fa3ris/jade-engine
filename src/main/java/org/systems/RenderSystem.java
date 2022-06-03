package org.systems;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_ONE;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;

import java.util.ArrayList;
import java.util.List;
import org.components.SpriteComponent;
import org.components.SpriteRenderer;
import org.jade.ecs.Entity;
import org.jade.ecs.System;
import org.jade.render.camera.Camera;
import org.jade.render.pool.ResourcePool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RenderSystem implements System {

  private final List<SpriteRenderer> spriteRenderers = new ArrayList<>(5);

  private static final Logger logger = LoggerFactory.getLogger(RenderSystem.class);

  private ResourcePool pool;

  public void setPool(ResourcePool pool) {
    this.pool = pool;
  }

  @Override
  public void update(double dt, List<Entity> entities) {

    for (Entity entity : entities) {

      SpriteComponent spriteComponent = entity.getComponent(SpriteComponent.class);
      if (spriteComponent == null) {
        logger.info("no sprite component - no need to add");
        continue;
      }

      logger.info("found sprite component");

      if (spriteComponent.getSpriteRendererIndex() >= 0) {
        logger.info("sprite component already assigned to sprite renderer {}", spriteComponent.getSpriteRendererIndex());

        logger.info("sprite component check if is dirty");

        if (spriteComponent.isDirty()) {
          logger.error("sprite component is dirty");
          SpriteRenderer spriteRenderer = spriteRenderers.get(spriteComponent.getSpriteRendererIndex());
          spriteRenderer.updateSpriteComponent(spriteComponent);
          spriteComponent.clean();
        }
      } else {

        logger.info("register sprite component");

        boolean added = false;
        for (int i = 0; i < spriteRenderers.size(); i++) {
          if (spriteRenderers.get(i).addSpriteComponent(spriteComponent)) {
            added = true;
            spriteComponent.setSpriteRendererIndex(i);
            break;
          }
        }

        if (!added) {
          SpriteRenderer freshRenderer = new SpriteRenderer();
          // position textCoord texUnit
          freshRenderer.setVertexAttributeSizes(new int[]{3, 2, 1});
          freshRenderer.setShader(pool.getShader("shaders/ecs/vertexShader.glsl",
              "shaders/ecs/fragmentShader.glsl"));
          freshRenderer.start();
          added = freshRenderer.addSpriteComponent(spriteComponent);
          spriteRenderers.add(freshRenderer);
          spriteComponent.setSpriteRendererIndex(spriteRenderers.size() - 1);
        }

        logger.info("sprite component added {}", added);
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
    glEnable(GL_BLEND);
    glBlendFunc(
        GL_ONE, // source alpha = what will be drawn
        GL_ONE_MINUS_SRC_ALPHA // destination alpha = what is already drawn
    );
    for (SpriteRenderer spriteRenderer : spriteRenderers) {
      spriteRenderer.render();
    }
    glDisable(GL_BLEND);
  }

  public void setCamera(Camera camera) {
    for (SpriteRenderer spriteRenderer : spriteRenderers) {
      spriteRenderer.setCamera(camera);
    }
  }

  public void setFOV(double angleDegree) {
    for (SpriteRenderer spriteRenderer : spriteRenderers) {
      spriteRenderer.setFOV(angleDegree);
    }
  }
}
