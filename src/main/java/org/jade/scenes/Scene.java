package org.jade.scenes;

import org.jade.render.camera.Camera;
import org.jade.render.pool.ResourcePool;

public interface Scene {

  /**
   * commence à setup la scene
   * occasion de reset à l'état initial
   *
   * ex pour effacer tout le texte à afficher
   */
  void beginScene();

  void update(double dt);
  void render();

  void load();
  void unload();

  void setCamera(Camera camera);
  void setFOV(double angleDegree);

  void setPool(ResourcePool pool);

  void setChangeSceneCallback(ChangeSceneCallback changeSceneCallback);

  void imGui();

  void renderText();
}
