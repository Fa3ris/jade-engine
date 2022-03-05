package org.jade.scenes;

import org.jade.render.Triangles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ColoredTriangleScene extends AbstractScene {

  private static final Logger logger = LoggerFactory.getLogger(ColoredTriangleScene.class);

  private Triangles coloredTriangle;

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

    float[] coloredVertices = {
        // positions         // colors
        0.5f, -0.5f, 0.0f,  1.0f, 0.0f, 0.0f,   // bottom right
        -0.5f, -0.5f, 0.0f,  0.0f, 1.0f, 0.0f,   // bottom left
        0.0f,  0.5f, 0.0f,  0.0f, 0.0f, 1.0f    // top
    };

    coloredTriangle = new Triangles(coloredVertices, tri1Ind,
        "shaders/color/vertexShader.glsl",
        "shaders/color/fragmentShader.glsl");

    coloredTriangle.configVertexAttribute(0, 3, 6*Float.BYTES, 0);
    coloredTriangle.configVertexAttribute(1, 3, 6*Float.BYTES, 3*Float.BYTES);
  }

  @Override
  public void render() {
    coloredTriangle.render();
  }

  @Override
  public void unload() {
    coloredTriangle.clean();
    coloredTriangle = null;
  }
}
