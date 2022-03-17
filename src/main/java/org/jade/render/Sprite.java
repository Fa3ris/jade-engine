package org.jade.render;

import org.jade.ecs.Component;
import org.jade.render.texture.Texture;
import org.joml.Vector2f;

public class Sprite extends Component {

  Texture texture;

  Vector2f[] coords;

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

  @Override
  public void update(double dt) {

  }

  public float[] getVertices() {

    // texture unit index
    float texId0 = 0;

    float[] vertices = new float[] {
        -0.5f,  0.5f, 0.0f, 0f, 1f, texId0,
        0.5f, 0.5f, 0.0f, 1f, 1f, texId0,
        0.5f, -0.5f, 0.0f, 1f, 0f, texId0,
        -0.5f,  -0.5f, 0.0f, 0f, 0f, texId0
    };
    return vertices;
  }

  public boolean isDirty() {
    return true;
  }
}
