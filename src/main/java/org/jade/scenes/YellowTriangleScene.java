package org.jade.scenes;

import org.jade.render.Triangles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class YellowTriangleScene extends AbstractScene {

  private static final Logger logger = LoggerFactory.getLogger(YellowTriangleScene.class);

  private Triangles yellowTriangle;

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

    yellowTriangle = new Triangles(vertices, tri2Ind,
        "shaders/triangle/vertexShader.glsl",
        "shaders/triangle/fragmentShader-yellow.glsl");

  }

  @Override
  public void render() {
    yellowTriangle.render();
  }

  @Override
  public void unload() {
    yellowTriangle = null;
  }
}
