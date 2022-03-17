package org.jade.render;

import org.jade.ecs.Component;
import org.jade.render.texture.Texture;
import org.joml.Vector2f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Sprite extends Component {

  private static final Logger logger = LoggerFactory.getLogger(Sprite.class);

  @Deprecated
  // texture added to sprite renderer
  Texture texture;

  Vector2f[] coords;

  float texId = 0;

  public Sprite(Texture texture) {
    //    in order: top-left => top-right => bottom-right => bottom-left
    this(texture, new Vector2f[] {
      new Vector2f(0f, 1f),
      new Vector2f(1f, 1f),
      new Vector2f(1f, 0f),
      new Vector2f(0f, 0f),
    });
  }

  // if taken from spritesheet
  public Sprite(Texture texture, Vector2f[] coords) {
    this.texture = texture;
    this.coords = coords;
  }

  private double elapsed;
  private final double switchTime = 1f; // in seconds

  @Override
  public void update(double dt) {

    logger.info("update sprite !!!!");
    elapsed += dt;
    if (elapsed >= switchTime) {
      elapsed -= switchTime;
      texId = texId == 0 ? 1:0;
    }

  }

  public float[] getVertices() {

    // texture unit index
    float texId0 = 0;

    float[] vertices = new float[] {
        -0.5f,  0.5f, 0.0f, 0f, 1f, texId,
        0.5f, 0.5f, 0.0f, 1f, 1f, texId,
        0.5f, -0.5f, 0.0f, 1f, 0f, texId,
        -0.5f,  -0.5f, 0.0f, 0f, 0f, texId
    };
    return vertices;
  }

  public boolean isDirty() {
    return true;
  }
}
