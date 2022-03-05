package org.jade.scenes;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

import org.jade.render.TexturedQuad;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CircularRotationTexturedQuadScene extends AbstractScene {

  private static final Logger logger = LoggerFactory.getLogger(CircularRotationTexturedQuadScene.class);

  private TexturedQuad texturedQuad;

  private Matrix4f circularRotation;

  @Override
  public void load() {

    texturedQuad = new TexturedQuad();

    circularRotation = new Matrix4f();

    circularRotation.identity()
        .translate(new Vector3f(
            .5f * (float) Math.cos(glfwGetTime()),
            .5f * (float) Math.sin(glfwGetTime()),
            0f));

    texturedQuad.applyTransform(circularRotation);
  }

  @Override
  public void update(double dt) {

    circularRotation.identity()
        .translate(new Vector3f(
            .5f * (float) Math.cos(glfwGetTime()),
            .5f * (float) Math.sin(glfwGetTime()),
            0f));

    texturedQuad.applyTransform(circularRotation);
  }

  @Override
  public void render() {
    texturedQuad.render();
  }

  @Override
  public void unload() {
    texturedQuad = null;
  }
}
