package org.jade.render;

import org.jade.ecs.Component;
import org.jade.render.texture.Texture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Sprite extends Component {

  // to know if texture is present in this sprite renderer
  // compare by equality
  private Texture texture;

  private static final Logger logger = LoggerFactory.getLogger(Sprite.class);

  private float topCoord, leftCoord, rightCoord, bottomCoord;

  // TODO extract to other component - unrelated to the sprite itself
  @Deprecated
  private float topPos, leftPos, rightPos, bottomPos;

  @Deprecated
  private float texId;

  @Deprecated
  private boolean dirty = false;

  public float getTopCoord() {
    return topCoord;
  }

  public float getLeftCoord() {
    return leftCoord;
  }

  public float getRightCoord() {
    return rightCoord;
  }

  public float getBottomCoord() {
    return bottomCoord;
  }

  public Sprite subSprite(float top, float left, float right, float bottom) {
    Sprite s = new Sprite(texture);

    s.topCoord = top;
    s.leftCoord = left;
    s.bottomCoord = bottom;
    s.rightCoord = right;

    return s;
  }

  public Sprite() {

    // default values

    topPos = .5f;
    leftPos = -.5f;

    rightPos = .5f;
    bottomPos = -.5f;

    topCoord = 1;
    bottomCoord = 0;

    leftCoord = 0;
    rightCoord = 1;
  }

  public Sprite(Texture texture) {
    this();
    this.texture = texture;
  }

  // remove when useless
  private double elapsed;
  private final double switchTime = 1f; // in seconds

  @Override
  @Deprecated
  public void update(double dt) {
    logger.info("update sprite !!!!");
    elapsed += dt;
    if (elapsed >= switchTime) {
      elapsed -= switchTime;
      setTexId(texId == 0 ? 1:0);
    }
  }

  @Deprecated
  private void setTexId(float texId) {
    this.texId = texId;
    dirty = true;
  }

  @Deprecated
  public void setPos(float top, float left, float bottom, float right) {
    topPos = top;
    leftPos = left;
    bottomPos = bottom;
    rightPos = right;

    dirty = true;
  }

  @Deprecated
  public void setTexCoords(float top, float left, float bottom, float right) {
    topCoord = top;
    leftCoord = left;
    bottomCoord = bottom;
    rightCoord = right;

    dirty = true;
  }


  @Deprecated
  public float[] getVertices() {

    dirty = false; // reset

    // see if z-position useful
    return new float[] {
     // position                   texCoord                 texId
        leftPos,  topPos,    0.0f, leftCoord,  topCoord,    texId, // left top
        rightPos, topPos,    0.0f, rightCoord, topCoord,    texId, // right top
        rightPos, bottomPos, 0.0f, rightCoord, bottomCoord, texId, // right bottom
        leftPos,  bottomPos, 0.0f, leftCoord,  bottomCoord, texId // left bottom
    };
  }

  @Deprecated
  public boolean isDirty() {
    return dirty;
  }

  public Texture getTexture() {
    return texture;
  }

  public void load(boolean flipVertically) {
    texture.load(flipVertically, false);
  }

  public void load(boolean flipVertically, boolean pixelate) {
    texture.load(flipVertically, pixelate);
  }
}
