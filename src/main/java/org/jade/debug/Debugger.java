package org.jade.debug;

import static org.lwjgl.opengl.GL11.GL_NO_ERROR;
import static org.lwjgl.opengl.GL11.glGetError;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Debugger {

  private static final Logger logger = LoggerFactory.getLogger(Debugger.class);

  private boolean debug = true;

  public GLError getError() {

    if (!debug) {
      return null;
    }

    int errorCode = glGetError();
    if (errorCode == GL_NO_ERROR) {
      return null;
    }
    GLError glError = new GLError();
    glError.code = errorCode;
    glError.message = "error occurred";

    StringBuilder sb = new StringBuilder();
    StackTraceElement[] stack = Thread.currentThread().getStackTrace();
    // start at 2 to not show stack frames related to calling the error callback
    for (int i = 2; i < stack.length; i++ ) {
      sb.append("\t\tat ");
      sb.append(stack[i].toString());
      sb.append("\n");
    }
    logger.error("debug error called with error: {}\n description: {}\n{}",
         glError.code, glError.message, sb);

    return glError;
  }


  public static class GLError {
    public String message;
    public int code;

    @Override
    public String toString() {
      return "GLError{" +
          "message='" + message + '\'' +
          ", code=" + code +
          '}';
    }
  }

}
