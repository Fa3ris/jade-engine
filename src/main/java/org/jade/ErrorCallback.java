package org.jade;

import java.util.Map;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.system.APIUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ErrorCallback extends GLFWErrorCallback {

  private static final Logger logger = LoggerFactory.getLogger(ErrorCallback.class);

  private final Map<Integer, String> ERROR_CODES;

  public ErrorCallback() {
    ERROR_CODES = APIUtil.apiClassTokens(
        (field, value) -> 0x10000 < value && value < 0x20000, null, GLFW.class);
  }
  @Override
  public void invoke(int error, long description) {

    StringBuilder sb = new StringBuilder();
    StackTraceElement[] stack = Thread.currentThread().getStackTrace();
    // start at 4 to not show stack frames related to calling the error callback
    for (int i = 4; i < stack.length; i++ ) {
      sb.append("\t\tat ");
      sb.append(stack[i].toString());
      sb.append("\n");
    }
    logger.error("error callback called with error: {}\n description: {}\n{}",
        ERROR_CODES.get(error), getDescription(description), sb);
  }
}
