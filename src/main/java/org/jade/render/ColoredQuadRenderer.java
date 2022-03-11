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
import org.jade.render.shader.Shader;
import org.lwjgl.system.MemoryStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ColoredQuadRenderer {

  private static final Logger logger = LoggerFactory.getLogger(ColoredQuadRenderer.class);

  private static final int POSITION_SIZE = 3;
  private static final int COLOR_SIZE = 4;
  private static final int VERTEX_TOTAL_SIZE = POSITION_SIZE + COLOR_SIZE;

  private static final int MAX_QUADS_PER_BATCH = 500;
  private static final int VERTICES_PER_QUAD = 4;
  private static final int INDICES_PER_QUAD = 6;

  private static final int MAX_TOTAL_QUADS = 1000;

  private int quadCount;
  private int vaoID, vboID, eboID;

  private int totalQuads;
  private final List<ColoredQuadRenderBatch> batches = new ArrayList<>();

  private Shader shader;

  public void setShader(Shader shader) { this.shader = shader; }

  public ColoredQuadRenderer() {
//    load();
//    logger.info("colored vertex renderer = {}", this);
  }

  @Override
  public String toString() {
    return "ColoredVertexRenderer{" +
        "quadCount=" + quadCount +
        '}';
  }

  public void render() {

    logger.info("rendering {} quads", totalQuads);
    if (shader != null) {
      shader.use();
    }

    for (ColoredQuadRenderBatch batch : batches) {
      batch.render();
    }

    if (false) {
      glBindVertexArray(vaoID);

      glDrawElements(
          GL_TRIANGLES,
          quadCount * INDICES_PER_QUAD, // number of vertices
          GL_UNSIGNED_INT, // type of index values
          0); // where to start if index buffer object is bound
    }
  }

  public void addQuad(ColoredQuad quad) {

    if (totalQuads >= MAX_TOTAL_QUADS) {
      logger.error("max totalQuads reached");
      return;
    }
    
    ++totalQuads;
    for (ColoredQuadRenderBatch batch : batches) {
      if (batch.addQuad(quad)) {
        return;
      }
    }

    ColoredQuadRenderBatch batch = new ColoredQuadRenderBatch();
    batches.add(batch);
    batch.addQuad(quad);

    logger.info("batches size {}", batches.size());
    if (true) { return; }

    if (false) {

      if (quadCount >= MAX_QUADS_PER_BATCH) {
        logger.error("no room left");
        return;
      }

      // update sub-region of the buffer
      glBindBuffer(GL_ARRAY_BUFFER, vboID);
      try (MemoryStack ignored = stackPush()) { // pop called automatically via AutoCloseable
        FloatBuffer vertexBuffer = stackMallocFloat(VERTICES_PER_QUAD * VERTEX_TOTAL_SIZE);
        vertexBuffer.put(quad.vertices).flip();
        glBufferSubData(GL_ARRAY_BUFFER,
            (long) quadCount * VERTICES_PER_QUAD * VERTEX_TOTAL_SIZE * Float.BYTES,
            vertexBuffer);
      }

      // clock-wise
      // 0 1 3 - 1 2 3

      // add offset of 4 vertices for the indices

      // 4 5 7 - 5 6 7
      // 8 9 11  9 10 11

      glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
      try (MemoryStack ignored = stackPush()) { // pop called automatically via AutoCloseable
        int valueOffset = quadCount * VERTICES_PER_QUAD;
        int[] indices = new int[6];
        // triangle 1
        indices[0] = valueOffset + 0;
        indices[1] = valueOffset + 1;
        indices[2] = valueOffset + 3;

        // triangle 2
        indices[3] = valueOffset + 1;
        indices[4] = valueOffset + 2;
        indices[5] = valueOffset + 3;
        IntBuffer indicesBuffer = stackMallocInt(indices.length);
        indicesBuffer.put(indices).flip();
        glBufferSubData(GL_ELEMENT_ARRAY_BUFFER, (long) quadCount * INDICES_PER_QUAD * Float.BYTES,
            indicesBuffer);
      }

      ++quadCount;
      if (quadCount < 10)
        logger.info("render is now {}", this);

    }
  }

  private void load() {
    vaoID = glGenVertexArrays();
    glBindVertexArray(vaoID);

    vboID = glGenBuffers();
    glBindBuffer(GL_ARRAY_BUFFER, vboID);

    // allocate buffer space but do not load
    // DYNAMIC
    // Use DYNAMIC_DRAW when the data store contents will be modified repeatedly and used many times.
    glBufferData(GL_ARRAY_BUFFER,
        (long) MAX_QUADS_PER_BATCH * VERTICES_PER_QUAD * VERTEX_TOTAL_SIZE * Float.BYTES,
        GL_DYNAMIC_DRAW);

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
    // allocate buffer space but do not load
    glBufferData(GL_ELEMENT_ARRAY_BUFFER, MAX_QUADS_PER_BATCH * INDICES_PER_QUAD * Float.BYTES, GL_STATIC_DRAW);

    logger.info("after loading {} {} {}", vaoID, vboID, eboID);
  }

  public static class ColoredQuad {

    private final float[] vertices = new float[VERTICES_PER_QUAD * VERTEX_TOTAL_SIZE];

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
          System.arraycopy(vertices[i].getPosition(), 0, this.vertices, i * VERTEX_TOTAL_SIZE, POSITION_SIZE);
          System.arraycopy(vertices[i].getColor(), 0, this.vertices, POSITION_SIZE + i * VERTEX_TOTAL_SIZE, COLOR_SIZE);
      }

      logger.info("colored Quad = {}", this);
    }
  }


  private static class ColoredQuadRenderBatch {

    private int quadCount;
    private int vaoID, vboID, eboID;

    ColoredQuadRenderBatch() {
      load();
    }

    boolean addQuad(ColoredQuad quad) {

      if (quadCount >= MAX_QUADS_PER_BATCH) {
        logger.error("no room left");
        return false;
      }

      // update sub-region of the buffer
      glBindBuffer(GL_ARRAY_BUFFER, vboID);
      try (MemoryStack ignored = stackPush()) { // pop called automatically via AutoCloseable
        FloatBuffer vertexBuffer = stackMallocFloat(VERTICES_PER_QUAD * VERTEX_TOTAL_SIZE);
        vertexBuffer.put(quad.vertices).flip();
        glBufferSubData(GL_ARRAY_BUFFER,
            (long) quadCount * VERTICES_PER_QUAD * VERTEX_TOTAL_SIZE * Float.BYTES,
            vertexBuffer);
      }

      // clock-wise
      // 0 1 3 - 1 2 3

      // add offset of 4 vertices for the indices

      // 4 5 7 - 5 6 7
      // 8 9 11  9 10 11

      glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
      try (MemoryStack ignored = stackPush()) { // pop called automatically via AutoCloseable
        int valueOffset = quadCount * VERTICES_PER_QUAD;
        int[] indices = new int[6];
        // triangle 1
        indices[0] = valueOffset + 0;
        indices[1] = valueOffset + 1;
        indices[2] = valueOffset + 3;

        // triangle 2
        indices[3] = valueOffset + 1;
        indices[4] = valueOffset + 2;
        indices[5] = valueOffset + 3;
        IntBuffer indicesBuffer = stackMallocInt(indices.length);
        indicesBuffer.put(indices).flip();
        glBufferSubData(GL_ELEMENT_ARRAY_BUFFER, (long) quadCount * INDICES_PER_QUAD * Float.BYTES, indicesBuffer);
      }

      ++quadCount;
      if (quadCount < 10)
        logger.info("render is now {}", this);

      return true;
    }

    @Override
    public String toString() {
      return "ColoredQuadRenderBatch{" +
          "quadCount=" + quadCount +
          '}';
    }

    void render() {

      glBindVertexArray(vaoID);

      glDrawElements(
          GL_TRIANGLES,
          quadCount * INDICES_PER_QUAD, // number of vertices
          GL_UNSIGNED_INT, // type of index values
          0); // where to start if index buffer object is bound

    }


    private void load() {

      vaoID = glGenVertexArrays();
      glBindVertexArray(vaoID);

      vboID = glGenBuffers();
      glBindBuffer(GL_ARRAY_BUFFER, vboID);

      // allocate buffer space but do not load
      // DYNAMIC
      // Use DYNAMIC_DRAW when the data store contents will be modified repeatedly and used many times.
      glBufferData(GL_ARRAY_BUFFER,
          (long) MAX_QUADS_PER_BATCH * VERTICES_PER_QUAD * VERTEX_TOTAL_SIZE * Float.BYTES,
          GL_DYNAMIC_DRAW);

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
      // allocate buffer space but do not load
      glBufferData(GL_ELEMENT_ARRAY_BUFFER, MAX_QUADS_PER_BATCH * INDICES_PER_QUAD * Float.BYTES, GL_STATIC_DRAW);

      logger.info("after loading {} {} {}", vaoID, vboID, eboID);

    }

  }


  public static class ColoredVertex {

    private final float[] position; // xyz

    private final float[] color; // rgba

    public ColoredVertex(float[] position, float[] color) {
      if (position.length != 3 || color.length != 4) throw new IllegalArgumentException("incorrect args");

      this.position = position;
      this.color = color;
    }

    public float[] getPosition() {
      return position;
    }

    public float[] getColor() {
      return color;
    }
  }
}
