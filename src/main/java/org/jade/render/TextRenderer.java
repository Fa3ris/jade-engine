package org.jade.render;


import static org.lwjgl.opengl.GL30.GL_ALPHA;
import static org.lwjgl.opengl.GL30.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL30.GL_BLEND;
import static org.lwjgl.opengl.GL30.GL_FLOAT;
import static org.lwjgl.opengl.GL30.GL_LINEAR;
import static org.lwjgl.opengl.GL30.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL30.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL30.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL30.GL_TEXTURE0;
import static org.lwjgl.opengl.GL30.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL30.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL30.GL_TRIANGLES;
import static org.lwjgl.opengl.GL30.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL30.glActiveTexture;
import static org.lwjgl.opengl.GL30.glBindBuffer;
import static org.lwjgl.opengl.GL30.glBindTexture;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glBlendFunc;
import static org.lwjgl.opengl.GL30.glBufferData;
import static org.lwjgl.opengl.GL30.glDisable;
import static org.lwjgl.opengl.GL30.glDrawArrays;
import static org.lwjgl.opengl.GL30.glEnable;
import static org.lwjgl.opengl.GL30.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glGenBuffers;
import static org.lwjgl.opengl.GL30.glGenTextures;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.opengl.GL30.glTexImage2D;
import static org.lwjgl.opengl.GL30.glTexParameteri;
import static org.lwjgl.opengl.GL30.glUseProgram;
import static org.lwjgl.opengl.GL30.glVertexAttribPointer;
import static org.lwjgl.stb.STBTruetype.stbtt_BakeFontBitmap;
import static org.lwjgl.stb.STBTruetype.stbtt_GetFontVMetrics;
import static org.lwjgl.stb.STBTruetype.stbtt_InitFont;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.jade.Window;
import org.jade.render.shader.Shader;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTBakedChar;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Singleton
 *
 * la police et la taille sont fixe
 * dessine du texte à l'écran
 *
 * ajoute du texte à dessiner avec {@link #addText(String, float, float)} <br>
 *
 * le texte sera dessiné à l'appel de {@link #render()} <br>
 *
 * reset les textes à afficher avec {@link #begin()}
 */
public final class TextRenderer {

  private static final Logger logger = LoggerFactory.getLogger(TextRenderer.class);
  private static TextRenderer instance;

  public static TextRenderer getInstance() {
    if (instance == null) {
      instance = new TextRenderer();
    }
    return instance;
  }

  private final List<STBTTAlignedQuad> stringQuads = new ArrayList<>(300);

  private final Shader shader;
  private final int vao;
  private final int positionVBO;
  private final int textureVBO;

  private final int texId;

  private final STBTTFontinfo fontInfo;
  private final STBTTBakedChar.Buffer charData;

  private final int fontHeight = 32;
//  private final int bitMapW = 1024;
  private final int bitMapW = 256;
//  private final int bitMapH = 1024;
  private final int bitMapH = 256;

  private int fontAscent, fontDescent, fontLineGap;

  private TextRenderer () {

     /*
    ########################################
    FONT
    ########################################
     */

    ByteBuffer fontBuffer;

    try {
      URL url = Thread.currentThread().getContextClassLoader().getResource("fonts/segoeui.ttf");
      FileChannel fc = FileChannel.open(Paths.get(url.toURI()));
      fontBuffer = BufferUtils.createByteBuffer((int) fc.size());
      fc.read(fontBuffer);
      fc.close();
      fontBuffer.flip();
    } catch (IOException e) {
      throw new RuntimeException(e);
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }

    fontInfo = STBTTFontinfo.create();

    if (!stbtt_InitFont(fontInfo, fontBuffer)) {
      throw new IllegalStateException("Failed to initialize font information.");
    }

    IntBuffer pAscent, pDescent, pLineGap;

    try (MemoryStack stack = MemoryStack.stackPush()) {
      pAscent = stack.mallocInt(1);
      pDescent = stack.mallocInt(1);
      pLineGap = stack.mallocInt(1);
      stbtt_GetFontVMetrics(fontInfo, pAscent, pDescent, pLineGap);

      fontAscent = pAscent.get(0);
      fontDescent = pDescent.get(0);
      fontLineGap = pLineGap.get(0);
    }

    charData = STBTTBakedChar.malloc(96); // why 96? - 96 chars to fit

    ByteBuffer fontBitMap = BufferUtils.createByteBuffer(bitMapW * bitMapW);
    stbtt_BakeFontBitmap(fontBuffer, fontHeight, fontBitMap, bitMapW, bitMapH,
        32, // 32 = first printable character = SPACE
        charData);

     /*
    ########################################
    TEXTURE from FONT
    ########################################
     */
    texId = glGenTextures();
    glBindTexture(GL_TEXTURE_2D, texId);

    glTexImage2D(GL_TEXTURE_2D, 0, GL_ALPHA, bitMapW, bitMapH, 0, GL_ALPHA, GL_UNSIGNED_BYTE,
        fontBitMap);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);

    shader = new Shader("shaders/font/vertexShader.glsl", "shaders/font/fragmentShader.glsl");

    Matrix4f ortho = new Matrix4f().ortho(0, Window.getInstance().getWidth(), Window.getInstance().getHeight(), 0, -1, 1);
    shader.setUniformMatrix4fv("projection", ortho);

    vao = glGenVertexArrays();

    positionVBO = glGenBuffers();
    textureVBO = glGenBuffers();

    glBindVertexArray(vao);
    glBindBuffer(GL_ARRAY_BUFFER, positionVBO);
    glVertexAttribPointer(0, 2, GL_FLOAT, false, 2 * Float.BYTES, 0);
    glEnableVertexAttribArray(0);

    glBindBuffer(GL_ARRAY_BUFFER, textureVBO);
    glVertexAttribPointer(1, 2, GL_FLOAT, false, 2 * Float.BYTES, 0);
    glEnableVertexAttribArray(1);

    glBindVertexArray(0);
   }



  /**
   * empty all texts to draw
   */
  public void begin() {
    logger.info("begin text Renderer".toUpperCase());
    stringQuads.clear();

    addText("Render Text", 300, 150);
  }

  /**
   * register a text to draw
   * @param text
   * @param x
   * @param y
   */
  public void addText(String text, float x, float y) {

    char[] chars = new char[text.length()];
    text.getChars(0, text.length(), chars, 0);

    try (MemoryStack stack = MemoryStack.stackPush()) {
      FloatBuffer xBuf = stack.floats(x);
      FloatBuffer yBuf = stack.floats(y);

      for (char aChar : chars) {
        STBTTAlignedQuad oneQuad = STBTTAlignedQuad.create();

        STBTruetype.stbtt_GetBakedQuad(charData, bitMapW, bitMapH,
            aChar - 32, // reminder that the offset is 32
            xBuf, yBuf, oneQuad, true);

        stringQuads.add(oneQuad);
      }
    }
  }

  /**
   * actually render to screen
   *
   * load vertices to vbos and render
   */
  public void render() {
    addText("string quad size: " + stringQuads.size(), 100, 200);
    float[] positionVertices  = new float[stringQuads.size() * 12];
    float[] textureVertices = new float[stringQuads.size() * 12];

    for (int i = 0; i < stringQuads.size(); i++) {
      STBTTAlignedQuad oneQuad = stringQuads.get(i);
      float x0 = oneQuad.x0();
      float x1 = oneQuad.x1();
      float y0 = oneQuad.y0();
      float y1 = oneQuad.y1();

      float s0 = oneQuad.s0();
      float s1 = oneQuad.s1();
      float t0 = oneQuad.t0();
      float t1 = oneQuad.t1();

      float[] positions = new float[]{
          x0, y0,
          x1, y0,
          x0, y1,

          x1, y0,
          x1, y1,
          x0, y1,
      };
      System.arraycopy(positions, 0, positionVertices, i * positions.length, positions.length);

      float[] textureCoords = new float[]{
          s0, t0,
          s1, t0,
          s0, t1,

          s1, t0,
          s1, t1,
          s0, t1
      };

      System.arraycopy(textureCoords, 0, textureVertices, i * textureCoords.length,
          textureCoords.length);
    }

    try (MemoryStack stack = MemoryStack.stackPush()) {

      glBindBuffer(GL_ARRAY_BUFFER, positionVBO);

      FloatBuffer stringPosBufferData = stack.mallocFloat(positionVertices.length);

      stringPosBufferData.put(positionVertices);
      stringPosBufferData.flip();

      glBufferData(GL_ARRAY_BUFFER, stringPosBufferData, GL_STATIC_DRAW);

    }

    try (MemoryStack stack = MemoryStack.stackPush()) {
      glBindBuffer(GL_ARRAY_BUFFER, textureVBO);

      FloatBuffer stringTexBufferData = stack.mallocFloat(textureVertices.length);
      stringTexBufferData.put(textureVertices);
      stringTexBufferData.flip();
      glBufferData(GL_ARRAY_BUFFER, stringTexBufferData, GL_STATIC_DRAW);
    }

    glActiveTexture(GL_TEXTURE0);
    glBindTexture(GL_TEXTURE_2D, texId);

    // need to blend to take alpha into account and not draw plain quads
    // do not know how to explain
    glEnable(GL_BLEND);
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

    shader.use();

    glBindVertexArray(vao);

    glDrawArrays(GL_TRIANGLES, 0, stringQuads.size() * 6);
    glDisable(GL_BLEND);
    glBindVertexArray(0);
    glUseProgram(0);
  }
}
