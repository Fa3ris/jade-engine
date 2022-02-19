package org.jade.render;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.jade.render.shader.Shader;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

public class SingleTriangle {

  private final Shader shader;

  private final int vaoID;
  private final int vboID;
  private final int eboID;

  private final int indicesCount;

  public SingleTriangle() {

    vaoID = GL30.glGenVertexArrays();
    GL30.glBindVertexArray(vaoID);

    vboID = GL30.glGenBuffers();
    GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, vboID);

    // TODO understand Buffer NIO API
    FloatBuffer positionsBuffer = MemoryUtil.memAllocFloat(3 * 3);
    positionsBuffer.put(-0.5f).put(-0.5f).put(0f); // bottom-left
    positionsBuffer.put(0.5f).put(-0.5f).put(0f); // bottom-right
    positionsBuffer.put(0f).put(0.5f).put(0f); // top

    positionsBuffer.flip();

    GL30.glBufferData(GL30.GL_ARRAY_BUFFER, positionsBuffer, GL30.GL_STATIC_DRAW);

    MemoryUtil.memFree(positionsBuffer);

    // describe vertex attribute
    GL30.glVertexAttribPointer(
        0,
        3,
        GL30.GL_FLOAT,
        false,
        3 * Float.BYTES, // could use 0 if array is tightly packed, GL will compute automatically
        0);

    GL30.glEnableVertexAttribArray(0);

    eboID = GL30.glGenBuffers();
    GL30.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, eboID);

    indicesCount = 3;
    IntBuffer indicesBuffer = MemoryUtil.memAllocInt(indicesCount);
    // sens trigonométrique
    indicesBuffer.put(0).put(1).put(2);
    indicesBuffer.flip();

    GL30.glBufferData(GL30.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL30.GL_STATIC_DRAW);

    MemoryUtil.memFree(indicesBuffer);

    GL30.glBindVertexArray(0);
    GL30.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, 0);
    GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, 0);

    shader = new Shader("shaders/triangle/vertexShader.glsl", "shaders/triangle/fragmentShader.glsl");

  }

  public void render() {

    final boolean useWireFrame = true;
    if (useWireFrame) { // WIREFRAME MODE
      GL30.glPolygonMode(GL30.GL_FRONT_AND_BACK, GL30.GL_LINE);
    }

    shader.use();
    GL30.glBindVertexArray(vaoID);

    GL30.glDrawElements(
        GL30.GL_TRIANGLES,
        indicesCount, // number of vertices
        GL30.GL_UNSIGNED_INT, // type of index values
        0); // where to start if index buffer object is bound

    if (useWireFrame) { // FILL MODE
      GL30.glPolygonMode(GL30.GL_FRONT_AND_BACK, GL30.GL_FILL);
    }
  }

  public void clean() {
    shader.delete();
    GL30.glDeleteBuffers(vboID);
    GL30.glDeleteBuffers(eboID);
    GL30.glDeleteVertexArrays(vaoID);
  }
}
