package org.jade.debug;

import org.lwjgl.opengl.GLDebugMessageCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DebugCallback extends GLDebugMessageCallback {

  private static final Logger logger = LoggerFactory.getLogger(DebugCallback.class);

  @Override
  public void invoke(int source, int type, int id, int severity, int length, long message,
      long userParam) {

    logger.warn("debug output message {}", GLDebugMessageCallback.getMessage(length, message));

  }
}
