package org.jade.scenes;

import org.jade.render.Triangles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TwoTrianglesScene extends AbstractScene {

  private static final Logger logger = LoggerFactory.getLogger(TwoTrianglesScene.class);

  private Triangles twoTriangles;

  @Override
  public void load() {

    float[] vertices = {
        -.5f, -.5f, 0f, // left
        0f, -.5f, 0f, // middle
        -.25f, .5f, 0f, // top-left
        .5f, -.5f, 0f, // right
        .25f, .5f, 0f, // top-right
    };

    int[] indices = {
        0, 1, 2,
        1, 3, 4
    };twoTriangles = new Triangles(vertices, indices);

  }

  @Override
  public void render() {
    twoTriangles.render();
  }

  @Override
  public void unload() {
    twoTriangles.clean();
    twoTriangles = null;
  }
}
