package org.jade.render;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.jade.render.shader.Shader;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GradientTriangle {

  private static final Logger logger = LoggerFactory.getLogger(GradientTriangle.class);

  private final int vaoID;
  private final int vboID;
  private final int eboID;

  private final Shader shader;

  private final int indicesCount;

  public GradientTriangle() {
    /* ########################### */
    /* vertex attributes loading */
    /* ########################### */

    // need one VAO
    vaoID = GL30.glGenVertexArrays();

    GL30.glBindVertexArray(vaoID);

    // TODO understand Buffer NIO API
    FloatBuffer positionsBuffer = MemoryUtil.memAllocFloat(3 * 4); // need 12 floats
    positionsBuffer.put(-0.5f).put(0.5f).put(0f); //v0
    positionsBuffer.put(-0.5f).put(-0.5f).put(0f); //v1
    positionsBuffer.put(0.5f).put(-0.5f).put(0f); //v2
    positionsBuffer.put(0.5f).put(0.5f).put(0f); //v3

    positionsBuffer.flip();  // ~ change from write-mode to read-mode

    // need one VBO to store in one of the attribute list of the VAO
    vboID = GL30.glGenBuffers();

    // set global GL_ARRAY_BUFFER to be vboID
    GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, vboID);

    // put data into the VBO
    // more exactly put data into the currently bound GL_ARRAY_BUFFER object
    GL30.glBufferData(GL30.GL_ARRAY_BUFFER, positionsBuffer, GL30.GL_STATIC_DRAW);

    // can free memory because
    // When using OpenGL's glBufferData() we now can free the buffer, because OpenGL read everything from it.
    MemoryUtil.memFree(positionsBuffer);

    // attribute list index to bind the VBO to
    // must be 0 because attribute 0 must be enabled
    // TODO see why
    final int attributeListIndex = 0;

    // define properties of one of the attribute list of the VAO
    // and bind the currently bound VBO
    /*
     * Each attribute which is stated in the Vertex Array Objects state vector
     * may refer to a different Vertex Buffer Object.
     * This reference is stored when glVertexAttribPointer is called.
     * Then the buffer which is currently bound to the target ARRAY_BUFFER
     * is associated to the attribute and the name (value) of the object is stored
     * in the state vector of the VAO.
     * The ARRAY_BUFFER binding is a global state.
     * */
    GL30.glVertexAttribPointer(
        attributeListIndex, // which attribute list
        3, // number of values per vertex
        GL30.GL_FLOAT, // type of value
        false,
        0, // offset between to vertices. > 0 if contain intermediary values, why do that ?
        0); // where to begin

    // enable the ith attribute of the currently bound Vertex Array Object
    GL30.glEnableVertexAttribArray(0);

    // unbind VBO - not really necessary
    GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, 0);

    indicesCount = 3*2;
    IntBuffer indicesBuffer = MemoryUtil.memAllocInt(indicesCount);
    // sens trigonométrique
    indicesBuffer.put(0).put(1).put(3); //top left triangle (v0, v1, v3)
    indicesBuffer.put(3).put(1).put(2); //bottom right triangle (v3, v1, v2)

    indicesBuffer.flip();

    // element/index buffer object
    // warning DO NOT unbind EBO while VAO is still bound else VAO loses EBO !!!
    eboID = GL30.glGenBuffers();

    // bind index buffer to the currently bound VAO
    GL30.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, eboID);

    GL30.glBufferData(GL30.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL30.GL_STATIC_DRAW);

    MemoryUtil.memFree(indicesBuffer);

    // unbind VAO
    GL30.glBindVertexArray(0);

    // can unbind EBO after unbinding VAO
    GL30.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, 0);

    /* ########################### */
    /* END vertex attributes loading */
    /* ########################### */

    shader = new Shader("shaders/vertexShader.glsl", "shaders/fragmentShader.glsl");
    shader.bindAttribute(attributeListIndex, "position");
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
