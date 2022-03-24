package org.jade.scenes;

import static imgui.ImGui.begin;
import static imgui.ImGui.button;
import static imgui.ImGui.end;
import static imgui.ImGui.text;

import org.jade.ecs.Entity;
import org.jade.render.ColoredQuadRenderer;
import org.jade.render.camera.Camera;
import org.jade.render.pool.ResourcePool;

public abstract class AbstractScene implements Scene {

  protected ChangeSceneCallback changeSceneCallback;

  protected ColoredQuadRenderer coloredVertexRenderer;

  protected ResourcePool pool;

  protected Entity activeEntity;

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

  private boolean showText = true;

  @Override
  public void imGui() {
    // actual logic

    begin("DEFAULT imgui window");
    if (button("toggle text")) {
      showText = !showText;
    }

    if (showText) {
      text("Hello, Dear IMGui");
    }
    // actual logic END
    end();
  }
}
