package org.components;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glBufferSubData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.system.MemoryStack.stackMallocFloat;
import static org.lwjgl.system.MemoryStack.stackMallocInt;
import static org.lwjgl.system.MemoryStack.stackPush;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import org.jade.ecs.Component;
import org.jade.render.Sprite;
import org.jade.render.shader.Shader;
import org.jade.render.texture.Texture;
import org.lwjgl.system.MemoryStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpriteRenderer extends Component {

  private static final int POSITION_SIZE = 3;
  private static final int TEXTURE_SIZE = 2;
  private static final int TEXTURE_ID_SIZE = 1;
  private static final int VERTEX_TOTAL_SIZE = POSITION_SIZE + TEXTURE_SIZE + TEXTURE_ID_SIZE;

  private static final int VERTICES_PER_QUAD = 4;
  private static final int INDICES_PER_QUAD = 6;

  private static final int MAX_QUADS_PER_BATCH = 500;

  private final static Logger logger = LoggerFactory.getLogger(SpriteRenderer.class);

  private int vaoID, vboID, eboID;


  private int totalSprites;
  private int vertexTotalSize;

  // pass array of sizes
  // index in array gives the index of attribute
  // suppose float
  // stride = sum of sizes

  // offset  = 0
  // for sizes
  // offset += size * Float.bytes



  private int[] vertexAttributesSizes;

  @Deprecated
  private final List<Texture> textures = new ArrayList<>();
  private final Texture[] texturesArr = new Texture[8];
  private Shader shader;

  List<Sprite> sprites = new ArrayList<>();

  Sprite[] spritesArr = new Sprite[MAX_QUADS_PER_BATCH];

  public void setShader(Shader shader) {
    this.shader = shader;
  }

  public void addTexture(Texture texture) {
    for (int i = 0; i < texturesArr.length; i++) {
      if (texturesArr[i] == null) {
        texturesArr[i] = texture;
        break;
      }
    }
  }

  public void setVertexAttributeSizes(int[] sizes) {
    vertexAttributesSizes = sizes;
    int totalSize = 0;
    for (int size : sizes) {
      totalSize += size;
    }
    totalSize *= Float.BYTES;

    vertexTotalSize = totalSize;
    int offset=0;
    for (int i = 0; i < sizes.length; i++) {
      glVertexAttribPointer(
          i,
          sizes[i],
          GL_FLOAT,
          false,
          totalSize,
          offset);
      glEnableVertexAttribArray(i);

      offset += sizes[i] * Float.BYTES;
    }
  }

  @Override
  public void start() {

    logger.info("{} starting", SpriteRenderer.class);

    vaoID = glGenVertexArrays();
    glBindVertexArray(vaoID);

    vboID = glGenBuffers();
    glBindBuffer(GL_ARRAY_BUFFER, vboID);

    // allocate buffer space but do not load
    // Use DYNAMIC_DRAW when the data store contents will be modified repeatedly and used many times.
    glBufferData(GL_ARRAY_BUFFER,
        (long) MAX_QUADS_PER_BATCH * VERTICES_PER_QUAD * VERTEX_TOTAL_SIZE * Float.BYTES,
        GL_DYNAMIC_DRAW);

    int offset = 0;
    for (int i = 0; i < vertexAttributesSizes.length; i++) {
      glVertexAttribPointer(
          i,
          vertexAttributesSizes[i],
          GL_FLOAT,
          false,
          vertexTotalSize,
          offset);
      glEnableVertexAttribArray(i);

      offset += vertexAttributesSizes[i] * Float.BYTES;
    }

    eboID = glGenBuffers();
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
    // allocate buffer space but do not load
    glBufferData(GL_ELEMENT_ARRAY_BUFFER, MAX_QUADS_PER_BATCH * INDICES_PER_QUAD * Float.BYTES, GL_STATIC_DRAW);

  }

  @Override
  public void update(double dt) {
    logger.info("{} updating", SpriteRenderer.class);

    glBindBuffer(GL_ARRAY_BUFFER, vboID);
    for (int i = 0; i < totalSprites; i++) {
      Sprite sprite = spritesArr[i];
      if (sprite.isDirty()) {
        // rebuffer for
        try (MemoryStack ignored = stackPush()) { // pop called automatically via AutoCloseable
          FloatBuffer vertexBuffer = stackMallocFloat(VERTICES_PER_QUAD * VERTEX_TOTAL_SIZE);
          vertexBuffer.put(sprite.getVertices()).flip();
          glBufferSubData(GL_ARRAY_BUFFER,
              (long) i * VERTICES_PER_QUAD * VERTEX_TOTAL_SIZE * Float.BYTES,
              vertexBuffer);
        }
      }
    }
  }

  /*

   need position
       texture coordinates within sprite sheet - what if need more than 8 textures ?
       texture id

       if sprite changed position or tex coord or tex id

       if changed
         find the place where sprite is stored and index in VBO and rebuffer data for this segment
    */
  public void addSprite(Sprite sprite) {
    if (totalSprites >= spritesArr.length) {
      return;
    }
    // update sub-region of the buffer
    glBindBuffer(GL_ARRAY_BUFFER, vboID);

    try (MemoryStack ignored = stackPush()) { // pop called automatically via AutoCloseable
      FloatBuffer vertexBuffer = stackMallocFloat(VERTICES_PER_QUAD * VERTEX_TOTAL_SIZE);
      vertexBuffer.put(sprite.getVertices()).flip();
      glBufferSubData(GL_ARRAY_BUFFER,
          (long) totalSprites * VERTICES_PER_QUAD * VERTEX_TOTAL_SIZE * Float.BYTES,
          vertexBuffer);
    }

    // clock-wise
    // 0 1 3 - 1 2 3

    // add offset of 4 vertices for the indices

    // 4 5 7 - 5 6 7
    // 8 9 11  9 10 11
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);

    try (MemoryStack ignored = stackPush()) { // pop called automatically via AutoCloseable
      int valueOffset = totalSprites * VERTICES_PER_QUAD;
      int[] indices = new int[6];
      // TODO: put values in buffer directly
      // triangle 1
      indices[0] = valueOffset;
      indices[1] = valueOffset + 1;
      indices[2] = valueOffset + 3;

      // triangle 2
      indices[3] = valueOffset + 1;
      indices[4] = valueOffset + 2;
      indices[5] = valueOffset + 3;
      IntBuffer indicesBuffer = stackMallocInt(indices.length);
      indicesBuffer.put(indices).flip();
      glBufferSubData(GL_ELEMENT_ARRAY_BUFFER, (long) totalSprites * INDICES_PER_QUAD * Float.BYTES, indicesBuffer);

    }

    spritesArr[totalSprites] = sprite;
    sprites.add(sprite);
    ++totalSprites;

  }

  public void render() {
    logger.info("rendering {} sprites", sprites.size());

    if (shader != null) {
      shader.use();

      // TODO use textures Arr
      // how come it works ?
      for (int i = 0; i < textures.size(); i++) {

        // enable texture unit texture unit
        // sampler uniform value correspond to index of texture unit
        // i.e. TEXTURE0 = 0
        //      TEXTURE1 = 1
        //      TEXTURE2 = 2
        // ...
        // TODO use texturesArr
        textures.get(i).use(GL_TEXTURE0 + i);
      }
    }

    glBindVertexArray(vaoID);

    glDrawElements(
        GL_TRIANGLES,
        totalSprites * INDICES_PER_QUAD, // number of vertices
        GL_UNSIGNED_INT, // type of index values
        0); // where to start if index buffer object is bound
  }
}
