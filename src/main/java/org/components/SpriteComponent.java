package org.components;

import org.jade.ecs.Component;
import org.jade.render.Sprite;
import org.joml.Vector3f;

public class SpriteComponent extends Component {

  Sprite sprite;

  // in draw component ?? premature
  boolean isDirty;

  Vector3f[] positions = new Vector3f[4];

  float[] getVertices() {
    return new float[6];
  }

  public SpriteComponent(Sprite sprite) {
    this.sprite = sprite;
  }
  @Override
  public void update(double dt) {

  }
}
