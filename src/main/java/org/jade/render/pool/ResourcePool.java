package org.jade.render.pool;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.jade.render.Sprite;
import org.jade.render.SpriteSheet;
import org.jade.render.shader.Shader;
import org.jade.render.texture.Texture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourcePool {

  private final Map<Integer, Shader> shaders = new HashMap<>();

  private final Map<Integer, Texture> textures = new HashMap<>();

  private final Map<Integer, Sprite> sprites = new HashMap<>();

  private final Map<Integer, SpriteSheet> spriteSheets = new HashMap<>();

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

  public SpriteSheet getSpriteSheet(String path, float spriteW, float spriteH, float rowGap, float colGap) {
    int hash = path.hashCode();

    SpriteSheet spriteSheet = spriteSheets.get(hash);

    if (spriteSheet == null) {

      logger.info("create sprite sheet");

      spriteSheet = new SpriteSheet();
      Sprite sprite = getSprite(path);

      int sheetW = sprite.getWidth();
      int sheetH = sprite.getHeight();

      float xDelta = (spriteW + colGap) / sheetW;
      float yDelta = (spriteH + rowGap) / sheetH;

      int spritesPerRow = 0;

      float accumulatedW = 0;
      boolean firstItem = true;
      while (accumulatedW < sheetW) {
        if (firstItem) {
          accumulatedW += spriteW;
          firstItem = false;
        } else {
          accumulatedW += spriteW + colGap;
        }
        ++spritesPerRow;
      }

      logger.info("there are {} sprites per row", spritesPerRow);

      int spritesPerCol = 0;

      float accumulatedH = 0;
      firstItem = true;
      while (accumulatedH < sheetH) {
        if (firstItem) {
          accumulatedH += spriteH;
          firstItem = false;
        } else {
          accumulatedH += spriteH + rowGap;
        }
        ++spritesPerCol;
      }

      logger.info("there are {} sprites per col", spritesPerCol);

      int nCols = spritesPerRow;
      int nRows = spritesPerCol;

      float top = 1;
      float left = 0;
      float right = left + xDelta;
      float bottom = top - yDelta;

      boolean firstCol = true;
      for (int row = 0; row < nRows; row++) {
        for (int col = 0; col < nCols; col++) {
          Sprite subSprite = sprite.subSprite(top, left, left + (spriteW / sheetW), top - (spriteH / sheetH));
          spriteSheet.add(subSprite);
          left += xDelta;
        }
        top -= yDelta;
      }


//      float top = 1;
//      float left = 0;
//      float right = left + xDelta;
//      float bottom = top - yDelta;

//      Sprite subSprite = sprite.subSprite(top, left, right, bottom);

//      spriteSheet.add(subSprite);
      /*
      * find for texCoords
      * top left bottom right
      *
      * begin top = 1
        left = 0
        * right = left + spriteW
        * bottom = 1 - spriteH
        *
        *
        *
      * */
      logger.info("return sprite sheet with {} sprites", spriteSheet.size());
      spriteSheets.put(hash, spriteSheet);
    } else {
      logger.info("sprite sheet already loaded");
    }


    return spriteSheet;
  }

  public void clearTextures() {
    for (Texture texture : textures.values()) {
      texture.delete();
    }
    textures.clear();
  }
}
