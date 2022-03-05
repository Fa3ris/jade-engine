package org.jade.scenes;

import java.util.HashMap;
import java.util.Map;

public class SceneManager implements ChangeSceneCallback {

  private Scene currentScene;

  private final Map<SceneType, Scene> sceneMap = new HashMap<>();

  public SceneManager() {
    changeScene(SceneType.GRADIENT_TRIANGLE);
  }

  public void update(double dt) {
    currentScene.update(dt);
  }

  public void render() {
    currentScene.render();
  }

  public void changeScene(SceneType scene) {
    if (currentScene != null) {
      currentScene.unload();
    }
    currentScene = sceneMap.get(scene);
    if (currentScene == null) {
      createScene(scene);
    }
    if (currentScene != null) {
      currentScene.load();
    }
  }

  private void createScene(SceneType scene) {
    switch (scene) {
      case BASIC:
        currentScene = new BasicScene();
        break;
      case LEVEL:
        currentScene = new LevelScene();
        break;
      case GRADIENT_TRIANGLE:
        currentScene = new GradientTriangleScene();
        break;
      default:
        throw new IllegalArgumentException(String.format("unknown scene %s", scene));
    }
    currentScene.setChangeSceneCallback(this);
    sceneMap.put(scene, currentScene);
  }
}
