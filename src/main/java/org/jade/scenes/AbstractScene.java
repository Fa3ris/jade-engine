package org.jade.scenes;

import org.components.SpriteRenderer;
import org.jade.ecs.ECS;
import org.systems.RenderSystem;
import org.jade.render.ColoredQuadRenderer;
import org.jade.render.camera.Camera;
import org.jade.render.pool.ResourcePool;

public abstract class AbstractScene implements Scene {

  protected ChangeSceneCallback changeSceneCallback;

  protected ColoredQuadRenderer coloredVertexRenderer;

  protected ResourcePool pool;

  @Override
  public void update(double dt) {
  }

  @Override
  public void unload() {
  }

  @Override
  public void load() {
  }

  @Override
  public void render() {
    if (coloredVertexRenderer != null) {
      coloredVertexRenderer.render();
    }
  }

  @Override
  public void setCamera(Camera camera) {

  }

  @Override
  public void setFOV(double angleDegree) {

  }

  @Override
  public void setPool(ResourcePool pool) { this.pool = pool; }


  @Override
  public void setChangeSceneCallback(ChangeSceneCallback changeSceneCallback) {
    this.changeSceneCallback = changeSceneCallback;
  }
}
