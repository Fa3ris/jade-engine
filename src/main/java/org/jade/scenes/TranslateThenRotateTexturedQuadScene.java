package org.jade.scenes;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

import org.jade.render.TexturedQuad;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TranslateThenRotateTexturedQuadScene extends AbstractScene {

  private static final Logger logger = LoggerFactory.getLogger(TranslateThenRotateTexturedQuadScene.class);

  private TexturedQuad texturedQuad;

  private Matrix4f translateThenRotate;

  @Override
  public void load() {

    texturedQuad = new TexturedQuad();

    translateThenRotate = new Matrix4f();

    translateThenRotate.identity()
        .rotate((float) glfwGetTime(), new Vector3f(0f, 0f, 1f))
        .translate(new Vector3f(.5f, -.5f, 0f));

    texturedQuad.applyTransform(translateThenRotate);
  }

  @Override
  public void update(double dt) {

    translateThenRotate.identity()
        .rotate((float) glfwGetTime(), new Vector3f(0f, 0f, 1f))
        .translate(new Vector3f(.5f, -.5f, 0f));

    texturedQuad.applyTransform(translateThenRotate);
  }

  @Override
  public void render() {
    texturedQuad.render();
  }

  @Override
  public void unload() {
    texturedQuad.clean();
    texturedQuad = null;
    translateThenRotate = null;
  }
}
