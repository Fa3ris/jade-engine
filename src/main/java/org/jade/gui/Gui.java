package org.jade.gui;

import static imgui.ImGui.begin;
import static imgui.ImGui.button;
import static imgui.ImGui.createContext;
import static imgui.ImGui.destroyContext;
import static imgui.ImGui.end;
import static imgui.ImGui.getDrawData;
import static imgui.ImGui.newFrame;
import static imgui.ImGui.text;

import imgui.ImFontAtlas;
import imgui.ImFontConfig;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Gui {

  private static final Logger logger = LoggerFactory.getLogger(Gui.class);
  private final ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
  private final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();

  boolean showDemo = true;

  public Gui(long windowHandle) {

    logger.info("init gui");

    createContext();

    /* setup font */
    final ImGuiIO io = ImGui.getIO();
    final ImFontConfig fontConfig = new ImFontConfig(); // Natively allocated object, should be explicitly destroyed
    ImFontAtlas fontAtlas = io.getFonts();
    fontConfig.setGlyphRanges(fontAtlas.getGlyphRangesDefault());
    byte[] fontBytes;

    try {
      fontBytes = getClass().getClassLoader().getResourceAsStream("fonts/segoeui.ttf").readAllBytes();
    } catch (IOException e) {
      throw new RuntimeException("cannot load font file", e);
    }

    fontAtlas.addFontFromMemoryTTF(fontBytes, 16, fontConfig);
    fontAtlas.build();
    fontConfig.destroy(); // After all fonts were added we don't need this config more
    /* setup font END */
    imGuiGlfw.init(windowHandle, true);
    imGuiGl3.init();
  }



  /**
   * uses imgui.ini file to restore saved window state (position, size, collapsed)
   */
  public void process() {
    logger.info("process gui frame");
    imGuiGlfw.newFrame();
    newFrame();

    if (showDemo)
    ImGui.showDemoWindow();
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
