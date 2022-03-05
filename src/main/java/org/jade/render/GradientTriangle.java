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

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.jade.render.shader.Shader;
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
    vaoID = glGenVertexArrays();

    glBindVertexArray(vaoID);

    // TODO understand Buffer NIO API
    FloatBuffer positionsBuffer = MemoryUtil.memAllocFloat(3 * 4); // need 12 floats
    positionsBuffer.put(-0.5f).put(0.5f).put(0f); //v0
    positionsBuffer.put(-0.5f).put(-0.5f).put(0f); //v1
    positionsBuffer.put(0.5f).put(-0.5f).put(0f); //v2
    positionsBuffer.put(0.5f).put(0.5f).put(0f); //v3

    positionsBuffer.flip();  // ~ change from write-mode to read-mode

    // need one VBO to store in one of the attribute list of the VAO
    vboID = glGenBuffers();

    // set global GL_ARRAY_BUFFER to be vboID
    glBindBuffer(GL_ARRAY_BUFFER, vboID);

    // put data into the VBO
    // more exactly put data into the currently bound GL_ARRAY_BUFFER object
    glBufferData(GL_ARRAY_BUFFER, positionsBuffer, GL_STATIC_DRAW);

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
    glVertexAttribPointer(
        attributeListIndex, // which attribute list
        3, // number of values per vertex
        GL_FLOAT, // type of value
        false,
        0, // offset between to vertices. > 0 if contain intermediary values, why do that ?
        0); // where to begin

    // enable the ith attribute of the currently bound Vertex Array Object
    glEnableVertexAttribArray(0);

    // unbind VBO - not really necessary
    glBindBuffer(GL_ARRAY_BUFFER, 0);

    indicesCount = 3*2;
    IntBuffer indicesBuffer = MemoryUtil.memAllocInt(indicesCount);
    // sens trigonométrique
    indicesBuffer.put(0).put(1).put(3); //top left triangle (v0, v1, v3)
    indicesBuffer.put(3).put(1).put(2); //bottom right triangle (v3, v1, v2)

    indicesBuffer.flip();

    // element/index buffer object
    // warning DO NOT unbind EBO while VAO is still bound else VAO loses EBO !!!
    eboID = glGenBuffers();

    // bind index buffer to the currently bound VAO
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);

    glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);

    MemoryUtil.memFree(indicesBuffer);

    // unbind VAO
    glBindVertexArray(0);

    // can unbind EBO after unbinding VAO
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);

    /* ########################### */
    /* END vertex attributes loading */
    /* ########################### */

    shader = new Shader("shaders/vertexShader.glsl", "shaders/fragmentShader.glsl");
    shader.bindAttribute(attributeListIndex, "position");
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
