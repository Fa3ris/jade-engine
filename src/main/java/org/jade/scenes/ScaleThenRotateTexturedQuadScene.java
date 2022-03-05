package org.jade.scenes;

import org.jade.render.TexturedQuad;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScaleThenRotateTexturedQuadScene extends AbstractScene {

  private static final Logger logger = LoggerFactory.getLogger(ScaleThenRotateTexturedQuadScene.class);

  private TexturedQuad texturedQuad;

  @Override
  public void load() {

    texturedQuad = new TexturedQuad();

    Matrix4f scaleThenRotate = new Matrix4f().identity() // transformations are applied in reverse order
        // the matrix operation on the vector is v' = M*v
        .rotate((float) Math.toRadians(90.0f), new Vector3f(0f, 0f, 1f)) // rotate around z-axis
        .scale(new Vector3f(.5f, .5f, .5f));

    texturedQuad.applyTransform(scaleThenRotate);
  }

  @Override
  public void update(double dt) {
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
