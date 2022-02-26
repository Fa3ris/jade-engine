package org.jade.render;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

public class UpdatingTriangles extends Triangles {

  public UpdatingTriangles(float[] vertices, int[] indices, String vertexPath, String fragmentPath) {
    super(vertices, indices, vertexPath, fragmentPath);
  }

  public void update(double dt) {
    float green = (float) (Math.sin(glfwGetTime()) / 2f) + .5f;
    shader.setUniform4fv("ourColor", new float[]{0f, green, 0f, 1f});
  }
}
