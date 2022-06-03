package org.components;

import org.jade.ecs.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FontRenderer extends Component {

  private final static Logger logger = LoggerFactory.getLogger(FontRenderer.class);

  @Override
  public void start() {
  }

  @Override
  public void update(double dt) {
    logger.info("{} updating", FontRenderer.class);
  }
}
