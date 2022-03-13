package org.jade.gui;

import static imgui.ImGui.*;

import imgui.ImGui;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Gui {

  private static final Logger logger = LoggerFactory.getLogger(Gui.class);
  private final ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
  private final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();

  public Gui(long windowHandle) {

    logger.info("init gui");

    createContext();
    imGuiGlfw.init(windowHandle, true);
    imGuiGl3.init();
  }

  private boolean showText = true;

  /**
   * uses imgui.ini file to restore saved window state (position, size, collapsed)
   */
  public void process() {
    logger.info("process gui frame");
    imGuiGlfw.newFrame();
    newFrame();

    // actual logic

    begin("imgui window");
    if (button("toggle text")) {
      showText = !showText;
    }

    if (showText) {
      text("Hello, Dear IMGui");
    }
    // actual logic END
    end();
  }

  public void render() {
    logger.info("render gui frame");
    ImGui.render();
    imGuiGl3.renderDrawData(getDrawData());
  }


  public void dispose() {
    logger.info("dispose gui");
    imGuiGl3.dispose();
    imGuiGlfw.dispose();
    destroyContext();
  }
}
