package org.jade.render;

import static org.lwjgl.opengl.GL30.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL30.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL30.GL_FILL;
import static org.lwjgl.opengl.GL30.GL_FLOAT;
import static org.lwjgl.opengl.GL30.GL_FRONT_AND_BACK;
import static org.lwjgl.opengl.GL30.GL_LINE;
import static org.lwjgl.opengl.GL30.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL30.GL_TRIANGLES;
import static org.lwjgl.opengl.GL30.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL30.glBindBuffer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glBufferData;
import static org.lwjgl.opengl.GL30.glDeleteBuffers;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glDrawElements;
import static org.lwjgl.opengl.GL30.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glGenBuffers;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.opengl.GL30.glPolygonMode;
import static org.lwjgl.opengl.GL30.glVertexAttribPointer;
import static org.lwjgl.system.MemoryStack.stackMallocFloat;
import static org.lwjgl.system.MemoryStack.stackMallocInt;
import static org.lwjgl.system.MemoryStack.stackPush;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.jade.render.shader.Shader;
import org.lwjgl.system.MemoryStack;

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

    final boolean useWireFrame = false;
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
