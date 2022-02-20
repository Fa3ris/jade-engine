package org.jade.render;

import static org.lwjgl.opengl.GL11.GL_FILL;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_FRONT_AND_BACK;
import static org.lwjgl.opengl.GL11.GL_LINE;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11.glPolygonMode;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.system.MemoryStack.stackMallocFloat;
import static org.lwjgl.system.MemoryStack.stackMallocInt;
import static org.lwjgl.system.MemoryStack.stackPush;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.jade.render.shader.Shader;
import org.lwjgl.system.MemoryStack;

public class Triangles {

  private final Shader shader;

  private final int vaoID;
  private final int vboID;
  private final int eboID;

  private final int indicesCount;

  public Triangles(float[] vertices, int[] indices) {

    this(vertices, indices, "shaders/triangle/vertexShader.glsl", "shaders/triangle/fragmentShader.glsl");

  }

  public Triangles(float[] vertices, int[] indices, String vertexPath, String fragmentPath) {
    indicesCount = indices.length;

    vaoID = glGenVertexArrays();
    glBindVertexArray(vaoID);

    vboID = glGenBuffers();
    glBindBuffer(GL_ARRAY_BUFFER, vboID);

    try (MemoryStack ignored = stackPush()) { // pop called automatically via AutoCloseable
      FloatBuffer positionsBuffer = stackMallocFloat(vertices.length);
      positionsBuffer.put(vertices).flip();
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

    try (MemoryStack ignored = stackPush()) { // pop called automatically via AutoCloseable
      IntBuffer indicesBuffer = stackMallocInt(indicesCount);
      indicesBuffer.put(indices).flip();
      glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);
    }

    glBindVertexArray(0);
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    glBindBuffer(GL_ARRAY_BUFFER, 0);

    shader = new Shader(vertexPath, fragmentPath);

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
