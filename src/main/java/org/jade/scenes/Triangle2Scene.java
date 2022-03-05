package org.jade.scenes;

import org.jade.render.Triangles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Triangle2Scene extends AbstractScene {

  private static final Logger logger = LoggerFactory.getLogger(Triangle2Scene.class);

  private Triangles triangle2;

  @Override
  public void load() {

    float[] vertices = {
        -.5f, -.5f, 0f, // left
        0f, -.5f, 0f, // middle
        -.25f, .5f, 0f, // top-left
        .5f, -.5f, 0f, // right
        .25f, .5f, 0f, // top-right
    };

    int[] tri2Ind  = {
        1, 3, 4
    };

    triangle2 = new Triangles(vertices, tri2Ind);

  }

  @Override
  public void render() {
    triangle2.render();
  }

  @Override
  public void unload() {
    triangle2 = null;
  }
}
