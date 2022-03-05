package org.jade.scenes;

import org.jade.render.camera.Camera;

public interface Scene {

  void update(double dt);
  void render();

  void load();
  void unload();

  void setCamera(Camera camera);
  void setFOV(double angleDegree);

  void setChangeSceneCallback(ChangeSceneCallback changeSceneCallback);
}
