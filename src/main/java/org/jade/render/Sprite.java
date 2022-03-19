package org.jade.render;

import org.jade.ecs.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Sprite extends Component {

  private static final Logger logger = LoggerFactory.getLogger(Sprite.class);

  private float topCoord, leftCoord, rightCoord, bottomCoord;

  private float topPos, leftPos, rightPos, bottomPos;

  private float texId;

  private boolean dirty = false;

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

  // remove when useless
  private double elapsed;
  private final double switchTime = 1f; // in seconds

  @Override
  public void update(double dt) {
    logger.info("update sprite !!!!");
    elapsed += dt;
    if (elapsed >= switchTime) {
      elapsed -= switchTime;
      setTexId(texId == 0 ? 1:0);
    }
  }

  private void setTexId(float texId) {
    this.texId = texId;
    dirty = true;
  }

  public void setPos(float top, float left, float bottom, float right) {
    topPos = top;
    leftPos = left;
    bottomPos = bottom;
    rightPos = right;

    dirty = true;
  }

  public void setTexCoords(float top, float left, float bottom, float right) {
    topCoord = top;
    leftCoord = left;
    bottomCoord = bottom;
    rightCoord = right;

    dirty = true;
  }


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

  public boolean isDirty() {
    return dirty;
  }
}
