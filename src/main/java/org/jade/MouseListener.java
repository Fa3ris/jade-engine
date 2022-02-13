package org.jade;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LAST;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class MouseListener {

  public static final MouseListener instance = new MouseListener();

  public double scrollX, scrollY, x, y, prevX, prevY;
  public boolean dragging;
  private final boolean[] buttonPressed = new boolean[GLFW_MOUSE_BUTTON_LAST];

  private MouseListener() {
    scrollX = scrollY = x = y = prevX = prevY = 0;
    dragging = false;
  }

  /**
   * called only when window has focus
   */
  public static void mousePosCallback(long window, double xpos, double ypos) {
    instance.prevX = instance.x;
    instance.prevY = instance.y;
    instance.x = xpos;
    instance.y = ypos;
    instance.dragging = instance.buttonPressed[0] || instance.buttonPressed[1] || instance.buttonPressed[2];
  }

  /**
   * called only when window has focus
   * clicking outside of window loses focus
   */
  public static void mouseButtonCallback(long window, int button, int action, int mods) {
    if (button >= instance.buttonPressed.length) { return; }
    if (GLFW_PRESS == action) {
      instance.buttonPressed[button] = true;
    } else if (GLFW_RELEASE == action) {
      instance.buttonPressed[button] = false;
      instance.dragging = false;
    }
  }

  public static void mouseScrollCallback(long window, double xOffset, double yOffset) {
    instance.scrollX = xOffset;
    instance.scrollY = yOffset;
  }

  public static void endFrame() {
    instance.scrollX = instance.scrollY = 0;
    instance.prevX = instance.x;
    instance.prevY = instance.y;
  }

  public static boolean isButtonPressed(int button) {
    if (button >= instance.buttonPressed.length) {
      return false;
    }
    return instance.buttonPressed[button];
  }
}
