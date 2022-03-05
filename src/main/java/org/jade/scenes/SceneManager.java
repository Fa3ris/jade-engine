package org.jade.scenes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jade.render.camera.Camera;

public class SceneManager implements ChangeSceneCallback {

  private Scene currentScene;

  private final Map<SceneType, Scene> sceneMap = new HashMap<>();

  public SceneManager() {
    changeScene(SceneType.GRADIENT_TRIANGLE);
  }

  private final List<Scene> scenes = new ArrayList<>(20);

  void addScene(Scene scene) {
    scenes.add(scene);
  }

  void init() {
    currentScene = scenes.get(0);
    currentScene.load();
  }

  public void changeScene(Class<Scene> clazz) {
    for (Scene scene : scenes) {
      if (scene.getClass().isAssignableFrom(clazz)) {
        break;
      }
    }
  }

  public void update(double dt) {
    currentScene.update(dt);
  }

  public void render() {
    currentScene.render();
  }

  public void updateCamera(Camera camera) {
    currentScene.updateCamera(camera);
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
