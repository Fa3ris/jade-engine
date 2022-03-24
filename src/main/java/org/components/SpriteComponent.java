package org.components;

import org.jade.ecs.Component;
import org.jade.render.Sprite;
import org.jade.render.texture.Texture;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

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

  /**
   * TODO clean this mess
   */
  Vector3f topLeft = new Vector3f(-.5f, .5f, 0);
  Vector4f defaultTopLeft = new Vector4f(-.5f, .5f, 0, 1);
  Vector3f topRight = new Vector3f(.5f, .5f, 0);
  Vector4f defaultTopRight = new Vector4f(.5f, .5f, 0, 1);
  Vector3f bottomLeft = new Vector3f(-.5f, -.5f, 0);
  Vector4f defaultBottomLeft = new Vector4f(-.5f, -.5f, 0, 1);
  Vector3f bottomRight = new Vector3f(.5f, -.5f, 0);
  Vector4f defaultBottomRight = new Vector4f(.5f, -.5f, 0, 1);

  public void transform(Matrix4f mat) {
    Vector4f v = new Vector4f();
    mat.transform(defaultTopLeft, v);
    topLeft.x = v.x;
    topLeft.y = v.y;

    mat.transform(defaultTopRight, v);
    topRight.x = v.x;
    topRight.y = v.y;

    mat.transform(defaultBottomLeft, v);
    bottomLeft.x = v.x;
    bottomLeft.y = v.y;

    mat.transform(defaultBottomRight, v);
    bottomRight.x = v.x;
    bottomRight.y = v.y;

    setDirty();

  }

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
        topLeft.x,  topLeft.y,    0.0f, sprite.getLeftCoord(),  sprite.getTopCoord(),    textureUnit, // left top
        topRight.x, topRight.y,    0.0f, sprite.getRightCoord(), sprite.getTopCoord(),    textureUnit, // right top
        bottomRight.x, bottomRight.y, 0.0f, sprite.getRightCoord(), sprite.getBottomCoord(), textureUnit, // right bottom
        bottomLeft.x,  bottomLeft.y, 0.0f, sprite.getLeftCoord(),  sprite.getBottomCoord(), textureUnit // left bottom
    };
  }

  @Deprecated
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

  public void setSprite(Sprite newSprite) {
    sprite = newSprite;
    setDirty();
  }
}
