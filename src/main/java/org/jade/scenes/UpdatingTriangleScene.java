package org.jade.scenes;

import org.jade.render.UpdatingTriangles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdatingTriangleScene extends AbstractScene {

  private static final Logger logger = LoggerFactory.getLogger(UpdatingTriangleScene.class);

  private UpdatingTriangles updatingTriangles;

  @Override
  public void load() {

    float[] vertices = {
        -.5f, -.5f, 0f, // left
        0f, -.5f, 0f, // middle
        -.25f, .5f, 0f, // top-left
        .5f, -.5f, 0f, // right
        .25f, .5f, 0f, // top-right
    };

    int[] tri1Ind  = {
        0, 1, 2,
    };

    updatingTriangles = new UpdatingTriangles(vertices, tri1Ind,
        "shaders/uniform/vertexShader.glsl",
        "shaders/uniform/fragmentShader.glsl");

  }

  @Override
  public void update(double dt) {
    updatingTriangles.update(dt);
  }

  @Override
  public void render() {
    updatingTriangles.render();
  }

  @Override
  public void unload() {
    updatingTriangles.clean();
    updatingTriangles = null;
  }
}
