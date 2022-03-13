package org.jade.render;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL30.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL30.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL30.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL30.GL_FLOAT;
import static org.lwjgl.opengl.GL30.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL30.GL_TRIANGLES;
import static org.lwjgl.opengl.GL30.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL30.glBindBuffer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glBufferData;
import static org.lwjgl.opengl.GL30.glBufferSubData;
import static org.lwjgl.opengl.GL30.glDeleteBuffers;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glDrawElements;
import static org.lwjgl.opengl.GL30.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glGenBuffers;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.opengl.GL30.glVertexAttribPointer;
import static org.lwjgl.system.MemoryStack.stackMallocFloat;
import static org.lwjgl.system.MemoryStack.stackMallocInt;
import static org.lwjgl.system.MemoryStack.stackPush;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import org.jade.debug.Debugger;
import org.jade.render.shader.Shader;
import org.jade.render.texture.Texture;
import org.lwjgl.system.MemoryStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TexturedQuadRenderer {

  private static final Debugger glDebugger = new Debugger();

  private static final Logger logger = LoggerFactory.getLogger(TexturedQuadRenderer.class);

  private static final int POSITION_SIZE = 3;
  private static final int TEXTURE_SIZE = 2;
  private static final int TEXTURE_ID_SIZE = 1;
  private static final int VERTEX_TOTAL_SIZE = POSITION_SIZE + TEXTURE_SIZE + TEXTURE_ID_SIZE;

  private static final int MAX_QUADS_PER_BATCH = 500;
  private static final int VERTICES_PER_QUAD = 4;
  private static final int INDICES_PER_QUAD = 6;

  private static final int MAX_TOTAL_QUADS = 1000;

  private int totalQuads;
  private final List<TexturedQuadRenderBatch> batches = new ArrayList<>();

  private final List<Texture> textures = new ArrayList<>();
  private Shader shader;

  public void setShader(Shader shader) {
    if (shader == null) return;
    this.shader = shader;
    shader.use();
    shader.setUniform1iv("textures", new int[] {0, 1, 2, 3, 4, 5, 6, 7});
  }

  public boolean addTexture(Texture texture) {
    if (textures.size() >= 8) {
      logger.error("textures full");
      return false;
    }
    return textures.add(texture);
  }

  public void render() {

    logger.info("rendering {} quads", totalQuads);
    if (shader != null) {
      shader.use();

      for (int i = 0; i < textures.size(); i++) {
        textures.get(i).use(GL_TEXTURE0 + i);
      }

    }

    for (TexturedQuadRenderBatch batch : batches) {
      batch.render();
    }

  }

  public void addQuad(TexturedQuad quad) {

    if (totalQuads >= MAX_TOTAL_QUADS) {
      logger.error("max totalQuads reached");
      return;
    }

    ++totalQuads;
    for (TexturedQuadRenderBatch batch : batches) {
      if (batch.addQuad(quad)) {
        return;
      }
    }

    TexturedQuadRenderBatch batch = new TexturedQuadRenderBatch();
    batches.add(batch);
    batch.addQuad(quad);

    logger.info("batches size {}", batches.size());
  }

  public void clean() {
    for (TexturedQuadRenderBatch batch : batches) {
      batch.clean();
    }
  }

  public static class TexturedQuad {

    private final float[] vertices = new float[VERTICES_PER_QUAD * VERTEX_TOTAL_SIZE];

    // 0 1 3 - 1 2 3

    // add offset of 4 vertices for the indices

    // 4 5 7 - 5 6 7

    /**
     *
     * @param vertices in order: top-left => top-right => bottom-right => bottom-left
     */
    public TexturedQuad(TexturedVertex[] vertices) {

      if (vertices.length != VERTICES_PER_QUAD) throw new IllegalArgumentException("need 4 vertices");

      for (int i = 0; i < VERTICES_PER_QUAD; ++i) {
          System.arraycopy(vertices[i].getPosition(), 0, this.vertices, i * VERTEX_TOTAL_SIZE, POSITION_SIZE);
          System.arraycopy(vertices[i].getTexture(), 0, this.vertices, POSITION_SIZE + i * VERTEX_TOTAL_SIZE, TEXTURE_SIZE);
          this.vertices[POSITION_SIZE + TEXTURE_SIZE + (i * VERTEX_TOTAL_SIZE)] = vertices[i].getTextureId();
      }

      logger.info("textured Quad = {}", this);
    }
  }


  private static class TexturedQuadRenderBatch {

    private int quadCount;
    private int vaoID, vboID, eboID;

    TexturedQuadRenderBatch() {
      load();
    }

    void clean() {

      glDeleteBuffers(vboID);
      glDeleteBuffers(eboID);
      glDeleteVertexArrays(vaoID);

    }

    boolean addQuad(TexturedQuad quad) {

      if (quadCount >= MAX_QUADS_PER_BATCH) {
        logger.error("no room left");
        return false;
      }

      // update sub-region of the buffer
      glBindBuffer(GL_ARRAY_BUFFER, vboID);

      glDebugger.getError();

      try (MemoryStack ignored = stackPush()) { // pop called automatically via AutoCloseable
        FloatBuffer vertexBuffer = stackMallocFloat(VERTICES_PER_QUAD * VERTEX_TOTAL_SIZE);
        vertexBuffer.put(quad.vertices).flip();
        glBufferSubData(GL_ARRAY_BUFFER,
            (long) quadCount * VERTICES_PER_QUAD * VERTEX_TOTAL_SIZE * Float.BYTES,
            vertexBuffer);

        glDebugger.getError();

      }


      // clock-wise
      // 0 1 3 - 1 2 3

      // add offset of 4 vertices for the indices

      // 4 5 7 - 5 6 7
      // 8 9 11  9 10 11

      glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);

      glDebugger.getError();

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

        glDebugger.getError();
      }

      ++quadCount;
      if (quadCount < 10)
        logger.info("render is now {}", this);

      return true;
    }


    void render() {

      glBindVertexArray(vaoID);

      glDebugger.getError();

      glDrawElements(
          GL_TRIANGLES,
          quadCount * INDICES_PER_QUAD, // number of vertices
          GL_UNSIGNED_INT, // type of index values
          0); // where to start if index buffer object is bound

      glDebugger.getError();
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
          TEXTURE_SIZE,
          GL_FLOAT,
          false,
          VERTEX_TOTAL_SIZE * Float.BYTES,
          POSITION_SIZE * Float.BYTES);
      glEnableVertexAttribArray(1);


      glVertexAttribPointer(
          2,
          TEXTURE_ID_SIZE,
          GL_FLOAT,
          false,
          VERTEX_TOTAL_SIZE * Float.BYTES,
          (POSITION_SIZE + TEXTURE_SIZE) * Float.BYTES);
      glEnableVertexAttribArray(2);


      eboID = glGenBuffers();
      glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
      // allocate buffer space but do not load
      glBufferData(GL_ELEMENT_ARRAY_BUFFER, MAX_QUADS_PER_BATCH * INDICES_PER_QUAD * Float.BYTES, GL_STATIC_DRAW);

      logger.info("after loading {} {} {}", vaoID, vboID, eboID);

    }

  }


  public static class TexturedVertex {

    private final float[] position; // xyz

    private final float[] texture; // uv

    private final float textureId;

    public TexturedVertex(float[] position, float[] texture, float textureId) {
      if (position.length != 3 || texture.length != 2)
        throw new IllegalArgumentException("incorrect args");

      this.position = position;
      this.texture = texture;
      this.textureId = textureId;
    }

    public float[] getPosition() {
      return position;
    }

    public float[] getTexture() {
      return texture;
    }

    public float getTextureId() {
      return textureId;
    }
  }
}
