package org.components;

import org.jade.ecs.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpriteRenderer extends Component {

  private final static Logger logger = LoggerFactory.getLogger(SpriteRenderer.class);

  @Override
  public void start() {
    logger.info("{} starting", SpriteRenderer.class);
  }

  @Override
  public void update(double dt) {
    logger.info("{} updating", SpriteRenderer.class);
  }
}
