package org.jade.scenes;

import org.jade.render.Triangles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Triangle1Scene extends AbstractScene {

  private static final Logger logger = LoggerFactory.getLogger(Triangle1Scene.class);

  private Triangles triangle1;

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

    triangle1 = new Triangles(vertices, tri1Ind);

  }

  @Override
  public void render() {
    triangle1.render();
  }

  @Override
  public void unload() {
    triangle1.clean();
    triangle1 = null;
  }
}
