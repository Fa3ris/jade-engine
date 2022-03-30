package org.components;

import static imgui.ImGui.text;

import org.jade.ecs.Component;
import org.jade.render.Sprite;
import org.jade.render.texture.Texture;
import org.joml.Matrix4f;
import org.joml.Vector4f;

public class SpriteComponent extends Component {

  private Sprite sprite;

  private int spriteRendererIndex = -1;

  private int quadIndex = -1;

  private int textureUnit = -1;

  private boolean isDirty;

  private final Vector4f topLeft = new Vector4f(defaultTopLeft);
  private static final Vector4f defaultTopLeft = new Vector4f(-.5f, .5f, 0, 1);

  private final Vector4f topRight = new Vector4f(defaultTopRight);
  private static final  Vector4f defaultTopRight = new Vector4f(.5f, .5f, 0, 1);

  private final Vector4f bottomLeft = new Vector4f(defaultBottomLeft);
  private static final Vector4f defaultBottomLeft = new Vector4f(-.5f, -.5f, 0, 1);

  private final Vector4f bottomRight = new Vector4f(defaultBottomRight);
  private static final Vector4f defaultBottomRight = new Vector4f(.5f, -.5f, 0, 1);

  public SpriteComponent(Sprite sprite) {
    this.sprite = sprite;

  }

  public void transform(Matrix4f mat) {
    mat.transform(defaultTopLeft, topLeft);
    mat.transform(defaultTopRight, topRight);
    mat.transform(defaultBottomLeft, bottomLeft);
    mat.transform(defaultBottomRight, bottomRight);
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

  @Override
  public void imGui() {

    text("sprite component");
  }
}
