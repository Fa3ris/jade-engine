package org.jade.render;

import java.util.ArrayList;
import java.util.List;
import org.jade.render.texture.Texture;

public class SpriteSheet {

  private final List<Sprite> sprites = new ArrayList<>();

  public Sprite get(int index) {
    return sprites.get(index);
  }

  public int size() {
    return sprites.size();
  }

  public void add(Sprite subSprite) {
    sprites.add(subSprite);
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
