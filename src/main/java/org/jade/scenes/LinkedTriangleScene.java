package org.jade.scenes;

import org.jade.render.Triangles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LinkedTriangleScene extends AbstractScene {

  private static final Logger logger = LoggerFactory.getLogger(LinkedTriangleScene.class);

  private Triangles linkedTriangle;

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

    // send data from vertex to fragment shader
    linkedTriangle = new Triangles(vertices, tri2Ind,
        "shaders/link/vertexShader.glsl",
        "shaders/link/fragmentShader.glsl");

  }

  @Override
  public void render() {
    linkedTriangle.render();
  }

  @Override
  public void unload() {
    linkedTriangle = null;
  }
}
