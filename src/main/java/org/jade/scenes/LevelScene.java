package org.jade.scenes;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_B;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;

import org.jade.KeyListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LevelScene extends AbstractScene {

  private static final Logger logger = LoggerFactory.getLogger(LevelScene.class);

  @Override
  public void update(double dt) {

    logger.trace("update level scene");

    if (KeyListener.isKeyPressed(GLFW_KEY_B)) {
      logger.info("transition to basic scene");
//      changeSceneCallback.changeScene(SceneType.BASIC);
    }
  }
}
