package org.jade.scenes;

import org.jade.render.camera.Camera;

public abstract class AbstractScene implements Scene {

  protected ChangeSceneCallback changeSceneCallback;

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
  }

  @Override
  public void updateCamera(Camera camera) {

  }

  @Override
  public void setChangeSceneCallback(ChangeSceneCallback changeSceneCallback) {
    this.changeSceneCallback = changeSceneCallback;
  }
}
