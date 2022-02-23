package org.jade.render;

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_load_from_memory;
import static org.lwjgl.stb.STBImage.stbi_set_flip_vertically_on_load;

import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TexturedQuad {

  private static final Logger logger = LoggerFactory.getLogger(TexturedQuad.class);

  private int textureId1;
  private int textureId2;

  private Triangles triangles;

  public TexturedQuad() {



    try {
      URL url = Thread.currentThread().getContextClassLoader().getResource("textures/wall.jpg");
      FileChannel fc = FileChannel.open(Paths.get(url.toURI()));
      ByteBuffer buffer = BufferUtils.createByteBuffer((int) fc.size());
      fc.read(buffer);
      fc.close();
      buffer.flip();
      IntBuffer width = BufferUtils.createIntBuffer(1);
      IntBuffer height = BufferUtils.createIntBuffer(1);
      IntBuffer components = BufferUtils.createIntBuffer(1);
      ByteBuffer data = stbi_load_from_memory(buffer, width, height, components, 0);

      textureId1 = glGenTextures();
      glBindTexture(GL_TEXTURE_2D, textureId1);

      if (false) {
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
      }

      glTexImage2D(GL_TEXTURE_2D,
          0,
          GL_RGB,
          width.get(),
          height.get(),
          0,
          GL_RGB,
          GL_UNSIGNED_BYTE,
          data);
      glGenerateMipmap(GL_TEXTURE_2D);
      stbi_image_free(data);

      logger.info("data is {}", data);
    } catch (Exception e) {
      logger.error("error getting texture data", e);
    }

    stbi_set_flip_vertically_on_load(true);

    try {
      URL url = Thread.currentThread().getContextClassLoader().getResource("textures/awesomeface.png");
      FileChannel fc = FileChannel.open(Paths.get(url.toURI()));
      ByteBuffer buffer = BufferUtils.createByteBuffer((int) fc.size());
      fc.read(buffer);
      fc.close();
      buffer.flip();
      IntBuffer width = BufferUtils.createIntBuffer(1);
      IntBuffer height = BufferUtils.createIntBuffer(1);
      IntBuffer components = BufferUtils.createIntBuffer(1);
      ByteBuffer data = stbi_load_from_memory(buffer, width, height, components, 0);
      width.mark();
      height.mark();
      components.mark();
      logger.info("w = {}, h = {}, c = {}", width.get(), height.get(), components.get());
      width.reset();
      height.reset();
      components.reset();

      textureId2 = glGenTextures();
      glBindTexture(GL_TEXTURE_2D, textureId2);

      if (false) {
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
      }

      glTexImage2D(GL_TEXTURE_2D,
          0,
          GL_RGBA,
          width.get(),
          height.get(),
          0,
          GL_RGBA,
          GL_UNSIGNED_BYTE,
          data);
      glGenerateMipmap(GL_TEXTURE_2D);
      stbi_image_free(data);

      logger.info("data is {}", data);
    } catch (Exception e) {
      logger.error("error getting texture data", e);
    }


    float[] vertices = {
        // positions          // colors           // texture coords
        0.5f,  0.5f, 0.0f,   1.0f, 0.0f, 0.0f,   1.0f, 1.0f,   // top right
        0.5f, -0.5f, 0.0f,   0.0f, 1.0f, 0.0f,   1.0f, 0.0f,   // bottom right
        -0.5f, -0.5f, 0.0f,   0.0f, 0.0f, 1.0f,   0.0f, 0.0f,   // bottom left
        -0.5f,  0.5f, 0.0f,   1.0f, 1.0f, 0.0f,   0.0f, 1.0f    // top left
    };

    int[] indices = {
        0, 1, 2,
        0, 2, 3
    };

    triangles = new Triangles(vertices, indices,
        "shaders/texture/vertexShader.glsl",
        "shaders/texture/fragmentShader.glsl");
    triangles.configVertexAttribute(0, 3, 8*Float.BYTES, 0);
    triangles.configVertexAttribute(1, 3, 8*Float.BYTES, 3*Float.BYTES);
    triangles.configVertexAttribute(2, 2, 8*Float.BYTES, 6*Float.BYTES);


    try (MemoryStack ignored = MemoryStack.stackPush()) {
      IntBuffer buffer = MemoryStack.stackMallocInt(1);
      buffer.put(0);
      buffer.flip();
      triangles.shader.setUniform1i("texture1", buffer);
    }

    try (MemoryStack ignored = MemoryStack.stackPush()) {
      IntBuffer buffer = MemoryStack.stackMallocInt(1);
      buffer.put(1);
      buffer.flip();
      triangles.shader.setUniform1i("texture2", buffer);
    }
  }


  public void render() {
    glActiveTexture(GL_TEXTURE0);
    glBindTexture(GL_TEXTURE_2D, textureId1);
    glActiveTexture(GL_TEXTURE1);
    glBindTexture(GL_TEXTURE_2D, textureId2);

    triangles.render();
  }

}
