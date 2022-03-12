package org.jade.scenes;

import org.jade.render.camera.Camera;
import org.jade.render.pool.ResourcePool;

public interface Scene {

  void update(double dt);
  void render();

  void load();
  void unload();

  void setCamera(Camera camera);
  void setFOV(double angleDegree);

  void setPool(ResourcePool pool);

  void setChangeSceneCallback(ChangeSceneCallback changeSceneCallback);
}
