package org.jade.scenes;

import org.jade.render.GradientTriangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GradientTriangleScene extends AbstractScene {

  private static final Logger logger = LoggerFactory.getLogger(GradientTriangleScene.class);

  private GradientTriangle gradientTriangle;

  @Override
  public void load() {
    gradientTriangle = new GradientTriangle();
  }

  @Override
  public void render() {
    logger.info("render gradient triangle");
    gradientTriangle.render();
  }

  @Override
  public void unload() {
    gradientTriangle.clean();
    gradientTriangle = null;
  }
}
