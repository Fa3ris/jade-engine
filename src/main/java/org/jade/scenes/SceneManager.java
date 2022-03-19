package org.jade.scenes;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;

import java.util.ArrayList;
import java.util.List;
import org.jade.KeyListener;
import org.systems.RenderSystem;
import org.jade.render.camera.Camera;
import org.jade.render.pool.ResourcePool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SceneManager implements ChangeSceneCallback {

  private static final Logger logger = LoggerFactory.getLogger(SceneManager.class);

  private Scene currentScene;

  private int currentSceneIndex;
  private final List<Scene> scenes = new ArrayList<>(20);

  private final ResourcePool pool = new ResourcePool();

  private RenderSystem renderSystem;

  public SceneManager() {}

  void addScene(Scene scene) {
    scenes.add(scene);
  }

  void init() {
    currentScene = scenes.get(0);
    currentScene.setPool(pool);
    currentScene.load();
  }

  public void changeScene(Class<Scene> clazz) {
    for (Scene scene : scenes) {
      if (scene.getClass().isAssignableFrom(clazz)) {
        break;
      }
    }
  }

  public void nextScene() {
    int i = scenes.indexOf(currentScene);
    ++i;
    if (i == scenes.size()) {
      i = 0;
    }
    currentScene.unload();
    currentScene = scenes.get(i);
    currentScene.setPool(pool);
    currentScene.load();
  }

  boolean rightDown;
  boolean oldRightDown;
  boolean rightTransitioned;
  boolean oldRightTransitioned;
  private int rightCount;

  boolean leftDown;
  boolean oldLeftDown;
  boolean leftTransitioned;
  boolean oldLeftTransitioned;
  private int leftCount;

  public void update(double dt) {

    oldRightDown = rightDown;
    oldRightTransitioned = rightTransitioned;

    rightDown = KeyListener.isKeyPressed(GLFW_KEY_RIGHT);
    rightTransitioned = rightDown != oldRightDown;

    oldLeftDown = leftDown;
    oldLeftTransitioned = leftTransitioned;

    leftDown = KeyListener.isKeyPressed(GLFW_KEY_LEFT);
    leftTransitioned = leftDown != oldLeftDown;

    if (rightDown && rightTransitioned) {
      logger.info("right pressed {}", ++rightCount);
      nextScene();
    } else if (leftDown && leftTransitioned) {
      logger.info("left pressed {}", ++leftCount);
      previousScene();
    }

    currentScene.update(dt);


  }

  private void previousScene() {
    int i = scenes.indexOf(currentScene);
    --i;
    if (i < 0) {
      i = scenes.size() - 1;
    }
    currentScene.unload();
    currentScene = scenes.get(i);
    currentScene.setPool(pool);
    currentScene.load();
  }

  public void render() {
    currentScene.render();
  }

  public void updateCamera(Camera camera) {
    currentScene.setCamera(camera);
  }

  public void setFOV(double angleDegree) {
    currentScene.setFOV(angleDegree);
  }

  public void changeScene(SceneType scene) {
    if (currentScene != null) {
      currentScene.unload();
    }
//    currentScene = sceneMap.get(scene);
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
//    sceneMap.put(scene, currentScene);
  }

  public void delete() {
    pool.clearTextures();
  }
}
