package org.jade.render.texture;

import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_LINEAR_MIPMAP_LINEAR;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_load_from_memory;
import static org.lwjgl.stb.STBImage.stbi_set_flip_vertically_on_load;

import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import org.lwjgl.BufferUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Texture {

  private static final Logger logger = LoggerFactory.getLogger(Texture.class);

  private int width, height, components;

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  public int getComponents() {
    return components;
  }

  final private int id;

  final private String path;

  public Texture(String path) {
    this.path = path;
    id = glGenTextures();
  }

  public void load(boolean flipVertically, boolean pixelate) {
    try {
      URL url = Thread.currentThread().getContextClassLoader().getResource(path);
      FileChannel fc = FileChannel.open(Paths.get(url.toURI()));
      ByteBuffer buffer = BufferUtils.createByteBuffer((int) fc.size());
      fc.read(buffer);
      fc.close();
      buffer.flip();
      IntBuffer width = BufferUtils.createIntBuffer(1);
      IntBuffer height = BufferUtils.createIntBuffer(1);
      IntBuffer components = BufferUtils.createIntBuffer(1);
      stbi_set_flip_vertically_on_load(flipVertically);
      ByteBuffer data = stbi_load_from_memory(buffer, width, height, components, 0);
      stbi_set_flip_vertically_on_load(false);
      width.mark();
      height.mark();
      components.mark();
      this.width = width.get();
      this.height = height.get();
      this.components = components.get();
      logger.info("w = {}, h = {}, c = {}", this.width, this.height, this.components);
      width.reset();
      height.reset();
      components.reset();

      glBindTexture(GL_TEXTURE_2D, id);

      if (pixelate) {
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
      }

      if (false) {
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
      }

      int glFormat = 0;
      if (this.components == 3) {
        glFormat = GL_RGB;
      } else if (this.components == 4) {
        glFormat = GL_RGBA;
      } else {
        logger.error("unknown number of components {}", this.components);
      }
      glTexImage2D(GL_TEXTURE_2D,
          0,
          glFormat,
          width.get(),
          height.get(),
          0,
          glFormat,
          GL_UNSIGNED_BYTE,
          data);
      glGenerateMipmap(GL_TEXTURE_2D);
      stbi_image_free(data);

      logger.trace("data is {}", data);
    } catch (Exception e) {
      logger.error("error getting texture data", e);
    }

  }

  public void load(boolean flipVertically) {
    load(flipVertically, false);
  }

  public void use(int textureUnit) {
    glActiveTexture(textureUnit);
    glBindTexture(GL_TEXTURE_2D, id);
  }

  public void delete() {
    glDeleteTextures(id);
  }
}
