package org.jade;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_LAST;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class KeyListener {

  private static final KeyListener instance = new KeyListener();

  private final boolean[] keyPressed = new boolean[GLFW_KEY_LAST];

  private KeyListener() {}

  public static void keyCallback(long window, int key, int scancode, int action, int mods) {
    if (key >= instance.keyPressed.length) { return; }
    if (GLFW_PRESS == action) {
      instance.keyPressed[key] = true;
    } else if (GLFW_RELEASE == action) {
      instance.keyPressed[key] = false;
    }
  }

  public static boolean isKeyPressed(int keyCode) throws ArrayIndexOutOfBoundsException {
    return instance.keyPressed[keyCode];
  }
}
