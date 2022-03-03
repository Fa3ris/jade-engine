package org.jade.scenes;

public interface Scene {

  void update(double dt);
  void render();

  void load();
  void unload();

  void setChangeSceneCallback(ChangeSceneCallback changeSceneCallback);
}
