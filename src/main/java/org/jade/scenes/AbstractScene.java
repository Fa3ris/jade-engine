package org.jade.scenes;

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
  public void setChangeSceneCallback(ChangeSceneCallback changeSceneCallback) {
    this.changeSceneCallback = changeSceneCallback;
  }
}
