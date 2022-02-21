package org.jade.render;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

import java.nio.FloatBuffer;
import org.lwjgl.system.MemoryStack;
import org.slf4j.LoggerFactory;

public class UpdatingTriangles extends Triangles {

  public UpdatingTriangles(float[] vertices, int[] indices, String vertexPath, String fragmentPath) {
    super(vertices, indices, vertexPath, fragmentPath);
  }

  public void update(double dt) {
    float green = (float) (Math.sin(glfwGetTime()) / 2f) + .5f;
    try (MemoryStack ignored = MemoryStack.stackPush()) {
      FloatBuffer buffer = MemoryStack.stackMallocFloat(4);
      buffer.put(new float[]{0f, green, 0f, 1f});
      buffer.flip();
      shader.setUniform4v("ourColor", buffer);
    }

  }
}
