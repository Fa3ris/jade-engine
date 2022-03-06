package org.jade.scenes;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_B;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_L;

import org.jade.KeyListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BasicScene extends AbstractScene {

  private static final Logger logger = LoggerFactory.getLogger(BasicScene.class);

  @Override
  public void update(double dt) {
    logger.trace("update basic scene");

    if (KeyListener.isKeyPressed(GLFW_KEY_L)) {
      logger.info("transition to level scene");
//      changeSceneCallback.changeScene(SceneType.LEVEL);
    }

  }
}
