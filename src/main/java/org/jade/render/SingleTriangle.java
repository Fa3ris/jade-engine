package org.jade.render;

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryStack.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.jade.render.shader.Shader;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

public class SingleTriangle {

  private final Shader shader;

  private final int vaoID;
  private final int vboID;
  private final int eboID;

  private final int indicesCount;

  public SingleTriangle() {

    vaoID = glGenVertexArrays();
    glBindVertexArray(vaoID);

    vboID = glGenBuffers();
    glBindBuffer(GL_ARRAY_BUFFER, vboID);

    try (MemoryStack ignored = stackPush()) { // pop called automatically via AutoCloseable
      // TODO understand Buffer NIO API
      FloatBuffer positionsBuffer = stackMallocFloat(3 * 3);
      positionsBuffer.put(-0.5f).put(-0.5f).put(0f); // bottom-left
      positionsBuffer.put(0.5f).put(-0.5f).put(0f); // bottom-right
      positionsBuffer.put(0f).put(0.5f).put(0f); // top
      positionsBuffer.flip();
      glBufferData(GL_ARRAY_BUFFER, positionsBuffer, GL_STATIC_DRAW);
    }

    // describe vertex attribute
    glVertexAttribPointer(
        0,
        3,
        GL_FLOAT,
        false,
        3 * Float.BYTES, // could use 0 if array is tightly packed, GL will compute automatically
        0);

    glEnableVertexAttribArray(0);

    eboID = glGenBuffers();
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);

    indicesCount = 3;

    try (MemoryStack ignored = stackPush()) { // pop called automatically via AutoCloseable
      IntBuffer indicesBuffer = stackMallocInt(indicesCount);
      // sens trigonométrique
      indicesBuffer.put(0).put(1).put(2);
      indicesBuffer.flip();
      glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);
    }

    glBindVertexArray(0);
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    glBindBuffer(GL_ARRAY_BUFFER, 0);

    shader = new Shader("shaders/triangle/vertexShader.glsl", "shaders/triangle/fragmentShader.glsl");

  }

  public void render() {

    final boolean useWireFrame = true;
    if (useWireFrame) { // WIREFRAME MODE
      glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
    }

    shader.use();
    glBindVertexArray(vaoID);

    glDrawElements(
        GL_TRIANGLES,
        indicesCount, // number of vertices
        GL_UNSIGNED_INT, // type of index values
        0); // where to start if index buffer object is bound

    if (useWireFrame) { // FILL MODE
      glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
    }
  }

  public void clean() {
    shader.delete();
    glDeleteBuffers(vboID);
    glDeleteBuffers(eboID);
    glDeleteVertexArrays(vaoID);
  }
}
