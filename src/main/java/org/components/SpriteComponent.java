package org.components;

import org.jade.ecs.Component;
import org.jade.render.Sprite;
import org.jade.render.texture.Texture;
import org.joml.Vector3f;

public class SpriteComponent extends Component {

  Sprite sprite;

  private float topPos, leftPos, rightPos, bottomPos;

  int spriteRendererIndex = -1;

  int quadIndex = -1;

  int textureUnit = -1;

  boolean isDirty;

  public SpriteComponent(Sprite sprite) {
    this.sprite = sprite;

    topPos = .5f;
    leftPos = -.5f;

    rightPos = .5f;
    bottomPos = -.5f;
  }

  Vector3f[] positions = new Vector3f[4];

  public void setDirty() {
    isDirty = true;
  }

  public void clean() {
    isDirty = false;
  }

  /*
    sprite render calls this
   */
  public float[] getVertices() {

    // see if z-position useful
    return new float[] {
        // position                   texCoord                 texId
        leftPos,  topPos,    0.0f, sprite.getLeftCoord(),  sprite.getTopCoord(),    textureUnit, // left top
        rightPos, topPos,    0.0f, sprite.getRightCoord(), sprite.getTopCoord(),    textureUnit, // right top
        rightPos, bottomPos, 0.0f, sprite.getRightCoord(), sprite.getBottomCoord(), textureUnit, // right bottom
        leftPos,  bottomPos, 0.0f, sprite.getLeftCoord(),  sprite.getBottomCoord(), textureUnit // left bottom
    };
  }

  public void setPos(float top, float left, float bottom, float right) {
    topPos = top;
    leftPos = left;
    bottomPos = bottom;
    rightPos = right;
  }






  @Override
  public void update(double dt) {

  }

  public int getSpriteRendererIndex() {
    return spriteRendererIndex;
  }

  public Texture getTexture() {
    return sprite.getTexture();
  }

  public void setTextureUnit(int i) {
    textureUnit = i;
  }

  public void setQuadIndex(int index) {
    quadIndex = index;
  }

  public boolean isDirty() {
    return isDirty;
  }

  public void setSpriteRendererIndex(int i) {
    spriteRendererIndex = i;
  }

  public int getQuadIndex() {
    return quadIndex;
  }
}
