package org.jade.render.pool;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.jade.render.Sprite;
import org.jade.render.shader.Shader;
import org.jade.render.texture.Texture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourcePool {

  private final Map<Integer, Shader> shaders = new HashMap<>();

  private final Map<Integer, Texture> textures = new HashMap<>();

  private final Map<Integer, Sprite> sprites = new HashMap<>();

  private static final Logger logger = LoggerFactory.getLogger(ResourcePool.class);

  public Shader getShader(String vertexPath, String fragmentPath) {

    Objects.requireNonNull(vertexPath, "vertex is null");
    Objects.requireNonNull(fragmentPath, "fragment is null");
    int hash = 17;
    hash = hash * 23 + vertexPath.hashCode();
    hash = hash * 23 + fragmentPath.hashCode();

    Shader shader = shaders.get(hash);

    if (shader != null) {
      logger.info("shader is cached");
      return shader;
    }

    logger.info("create shader");
    shader = new Shader(vertexPath, fragmentPath);
    shaders.put(hash, shader);
    return  shader;
  }

  public Texture getTexture(String path) {
    Objects.requireNonNull(path, "texture path is null");

    int hash = path.hashCode();

    Texture texture = textures.get(hash);

    if (texture != null) {
      logger.info("texture is cached");
      return  texture;
    }

    logger.info("create texture");
    texture = new Texture(path);
    textures.put(hash, texture);
    return texture;
  }

  public Sprite getSprite(String path) {

    int hash = path.hashCode();

    Sprite sprite = sprites.get(hash);

    if (sprite != null) {
       return sprite;
    }

    Texture texture = getTexture(path);
    sprite = new Sprite(texture);
    sprite.load(true, true);
    sprites.put(hash, sprite);

    return sprite;
  }

  public void clearTextures() {
    for (Texture texture : textures.values()) {
      texture.delete();
    }
    textures.clear();
  }
}
