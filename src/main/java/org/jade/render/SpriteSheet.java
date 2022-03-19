package org.jade.render;

import java.util.List;
import org.jade.render.texture.Texture;

public class SpriteSheet {

  Texture texture;

  float sheetWidth, sheetHeight;

  public SpriteSheet(String path) {
    texture = new Texture(path);
  }

  public void load(boolean flipVertically, boolean pixelate) {
    texture.load(flipVertically, pixelate);
  }

  public Sprite getSprite(int index) {
    Sprite sprite = new Sprite();

    return sprite;
  }
  // coords must be in range [0, 1]

  /*
  * reuse same sprite for multiple entities
  * but change position (scale, rotation, translation)
  *
  * all entities have same reference
  *
  * so who render ?
  *
  * the entity
  *
  * sprite renderer receives entity
  *
  *
  * */
}
