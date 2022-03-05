package org.jade.scenes;

import org.jade.render.SingleTriangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SingleTriangleScene extends AbstractScene {

  private static final Logger logger = LoggerFactory.getLogger(SingleTriangleScene.class);

  private SingleTriangle singleTriangle;

  @Override
  public void load() {
    singleTriangle = new SingleTriangle();
  }

  @Override
  public void render() {
    singleTriangle.render();
  }

  @Override
  public void unload() {
    singleTriangle = null;
  }
}
