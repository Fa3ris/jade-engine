package org.jade.render;

import static org.lwjgl.stb.STBImage.stbi_load_from_memory;

import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import org.lwjgl.BufferUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TexturedQuad {

  private static final Logger logger = LoggerFactory.getLogger(TexturedQuad.class);

  public TexturedQuad() {

    IntBuffer width = BufferUtils.createIntBuffer(1);
    IntBuffer height = BufferUtils.createIntBuffer(1);
    IntBuffer components = BufferUtils.createIntBuffer(1);

    ByteBuffer buffer;
    try {
      URL url = Thread.currentThread().getContextClassLoader().getResource("textures/wall.jpg");
      FileChannel fc = FileChannel.open(Paths.get(url.toURI()));
      buffer = BufferUtils.createByteBuffer((int) fc.size());
      fc.read(buffer);
      fc.close();
      buffer.flip();
      ByteBuffer data = stbi_load_from_memory(buffer, width, height, components, 4);
      logger.info("data is {}", data);
    } catch (Exception e) {
      logger.error("error getting texture data", e);
    }
  }

}
