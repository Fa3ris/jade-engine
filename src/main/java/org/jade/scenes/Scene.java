package org.jade.scenes;

import org.jade.render.camera.Camera;

public interface Scene {

  void update(double dt);
  void render();

  void load();
  void unload();

  void updateCamera(Camera camera);

  void setChangeSceneCallback(ChangeSceneCallback changeSceneCallback);
}
