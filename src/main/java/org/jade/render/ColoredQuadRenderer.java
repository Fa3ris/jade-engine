package org.jade.render;

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryStack.stackMallocFloat;
import static org.lwjgl.system.MemoryStack.stackMallocInt;
import static org.lwjgl.system.MemoryStack.stackPush;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.lwjgl.system.MemoryStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ColoredQuadRenderer {

  private static final int POSITION_SIZE = 3;
  private static final int COLOR_SIZE = 4;
  private static final int VERTEX_TOTAL_SIZE = POSITION_SIZE + COLOR_SIZE;

  private static final Logger logger = LoggerFactory.getLogger(ColoredQuadRenderer.class);

  private List<ColoredVertex> coloredVertices = new ArrayList<>();

  private static final int maxQuad = 3;
  private static final int VERTICES_PER_QUAD = 4;
  private static final int indices_Per_Quad = 6;
  private final float[] vertices = new float[maxQuad * VERTICES_PER_QUAD * VERTEX_TOTAL_SIZE];
  private final int[] indices = new int[maxQuad * indices_Per_Quad];

  private int quadCount = 0;

  public ColoredQuadRenderer() {

    // 0 1 3 - 1 2 3

    // add offset of 4 vertices for the indices

    // 4 5 7 - 5 6 7
    // 8 9 11  9 10 11

    for (int i = 0; i < maxQuad; ++i) {
      int indexOffset = i * indices_Per_Quad;
      int valueOffset = i * VERTICES_PER_QUAD;

      // triangle 1
      indices[indexOffset + 0] = valueOffset + 0;
      indices[indexOffset + 1] = valueOffset + 1;
      indices[indexOffset + 2] = valueOffset + 3;

      // triangle 2
      indices[indexOffset + 3] = valueOffset + 1;
      indices[indexOffset + 4] = valueOffset + 2;
      indices[indexOffset + 5] = valueOffset + 3;
    }

    logger.info("colored vertex renderer = {}", this);
  }

  @Override
  public String toString() {
    return "ColoredVertexRenderer{" +
        "vertices=" + Arrays.toString(vertices) +
        ", indices=" + Arrays.toString(indices) +
        ", quadCount=" + quadCount +
        '}';
  }

  public void render() {
    logger.info("rendering {}", quadCount);
    glBindVertexArray(vaoID);

    glBindBuffer(GL_ARRAY_BUFFER, vboID);
    try (MemoryStack ignored = stackPush()) { // pop called automatically via AutoCloseable
      FloatBuffer positionsBuffer = stackMallocFloat(vertices.length);
      positionsBuffer.put(vertices).flip();
      glBufferSubData(GL_ARRAY_BUFFER, 0, positionsBuffer);
    }

    glDrawElements(
        GL_TRIANGLES,
        quadCount * indices_Per_Quad, // number of vertices
        GL_UNSIGNED_INT, // type of index values
        0); // where to start if index buffer object is bound
  }

  public void addQuad(ColoredQuad quad) {
    if (quadCount >= maxQuad) {
      logger.error("no room left");
      return;
    }
    logger.info("add quad {}", quadCount + 1);
    System.arraycopy(quad.vertices, 0,
        vertices, quadCount * VERTICES_PER_QUAD * VERTEX_TOTAL_SIZE,
        VERTICES_PER_QUAD * VERTEX_TOTAL_SIZE);
    ++quadCount;
    logger.info("render is now {}", this);
  }

  private int vaoID, vboID, eboID;


  public void load() {
    vaoID = glGenVertexArrays();
    glBindVertexArray(vaoID);

    vboID = glGenBuffers();
    glBindBuffer(GL_ARRAY_BUFFER, vboID);

    glBufferData(GL_ARRAY_BUFFER,
        (long) vertices.length * Float.BYTES,
        GL_DYNAMIC_DRAW);
      // DYNAMIC
      // Use DYNAMIC_DRAW when the data store contents will be modified repeatedly and used many times.

    glVertexAttribPointer(
        0,
        POSITION_SIZE,
        GL_FLOAT,
        false,
        VERTEX_TOTAL_SIZE * Float.BYTES,
        0);
    glEnableVertexAttribArray(0);

    glVertexAttribPointer(
        1,
        COLOR_SIZE,
        GL_FLOAT,
        false,
        VERTEX_TOTAL_SIZE * Float.BYTES,
        POSITION_SIZE * Float.BYTES);
    glEnableVertexAttribArray(1);

    eboID = glGenBuffers();
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);

    try (MemoryStack ignored = stackPush()) { // pop called automatically via AutoCloseable
      IntBuffer indicesBuffer = stackMallocInt(indices.length);
      indicesBuffer.put(indices).flip();
      glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);
    }

    logger.info("after loading {} {} {}", vaoID, vboID, eboID);
  }

  public static class ColoredQuad {

    @Deprecated
    private final float[] positions = new float[POSITION_SIZE * 4]; // xyz
    @Deprecated
    private final float[] colors = new float[COLOR_SIZE * 4]; // rgba

    private final float[] vertices = new float[VERTICES_PER_QUAD * (POSITION_SIZE + COLOR_SIZE)];

    // 0 1 3 - 1 2 3

    // add offset of 4 vertices for the indices

    // 4 5 7 - 5 6 7

    @Override
    public String toString() {
      return "ColoredQuad{" +
          "vertices=" + Arrays.toString(vertices) +
          '}';
    }

    /**
     *
     * @param vertices in order: top-left => top-right => bottom-right => bottom-left
     */
    public ColoredQuad(ColoredVertex[] vertices) {

      if (vertices.length != VERTICES_PER_QUAD) throw new IllegalArgumentException("need 4 vertices");

      for (int i = 0; i < VERTICES_PER_QUAD; ++i) {
          System.arraycopy(vertices[i].getPosition(), 0, positions, i * POSITION_SIZE, POSITION_SIZE);
          System.arraycopy(vertices[i].getColor(), 0, colors, i * COLOR_SIZE, COLOR_SIZE);

          System.arraycopy(vertices[i].getPosition(), 0, this.vertices, i * (POSITION_SIZE + COLOR_SIZE), POSITION_SIZE);

          System.arraycopy(vertices[i].getColor(), 0, this.vertices, POSITION_SIZE + i * (COLOR_SIZE + POSITION_SIZE), COLOR_SIZE);
      }

      logger.info("colored Quad = {}", this);

      // 0 1 3 - 1 2 3

      // add offset of 4 vertices for the indices

      // 4 5 7 - 5 6 7
    }

    /*
        *  0 --------  * 1      4          5
        |    /         |
        |  /            |
        *  3---------- * 2      7           6
     */

    // 3, 2, 0, 0, 2, 1        7, 6, 4, 4, 6, 5
  }

}
