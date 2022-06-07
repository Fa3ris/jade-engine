package org.jade.scenes;

import static org.lwjgl.opengl.GL11.GL_ALPHA;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.stb.STBTruetype.stbtt_BakeFontBitmap;
import static org.lwjgl.stb.STBTruetype.stbtt_GetBakedQuad;
import static org.lwjgl.stb.STBTruetype.stbtt_GetFontVMetrics;
import static org.lwjgl.stb.STBTruetype.stbtt_InitFont;

import imgui.ImGui;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.jade.MouseListener;
import org.jade.Window;
import org.jade.render.shader.Shader;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTBakedChar;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.system.MemoryStack;

public class FontRenderingScene extends AbstractScene {

  int vao;
  int positionVBO;

  String myString = "For The glory of the 7! Yeah ~~~";

  @Override
  public void load() {

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

    STBTTFontinfo info = STBTTFontinfo.create();

    if (!stbtt_InitFont(info, fontBuffer)) {
      throw new IllegalStateException("Failed to initialize font information.");
    }

    int fontAscent, fontDescent, fontLineGap;
    IntBuffer pAscent, pDescent, pLineGap;

    try (MemoryStack stack = MemoryStack.stackPush()) {
      pAscent = stack.mallocInt(1);
      pDescent = stack.mallocInt(1);
      pLineGap = stack.mallocInt(1);
      stbtt_GetFontVMetrics(info, pAscent, pDescent, pLineGap);

      fontAscent = pAscent.get(0);
      fontDescent = pDescent.get(0);
      fontLineGap = pLineGap.get(0);
    }


    STBTTBakedChar.Buffer charData = STBTTBakedChar.malloc(96); // why 96? - 96 chars to fit

    int fontHeight = 32;
    int bitMapW = 1024;
    int bitMapH = 1024;
    ByteBuffer fontBitMap = BufferUtils.createByteBuffer(bitMapW * bitMapW);
    stbtt_BakeFontBitmap(fontBuffer, fontHeight, fontBitMap, bitMapW, bitMapH,
        32, // 32 = first printable character = SPACE
        charData);


    /*
    ########################################
    TEXTURE
    ########################################
     */

    int texId = glGenTextures();
    glBindTexture(GL_TEXTURE_2D, texId);

    glTexImage2D(GL_TEXTURE_2D, 0, GL_ALPHA, bitMapW, bitMapH, 0, GL_ALPHA, GL_UNSIGNED_BYTE,
        fontBitMap);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);

    // need to blend to take alpha into account and not draw plain quads
    // do not know how to explain
    glEnable(GL_BLEND);
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

    /*
    ########################################
    SHADER
    ########################################
     */

    Matrix4f ortho = new Matrix4f().ortho(0, Window.getInstance().getWidth(), Window.getInstance().getHeight(), 0, -1, 1);

    shader = new Shader("shaders/font/vertexShader.glsl", "shaders/font/fragmentShader.glsl");

    shader.setUniformMatrix4fv("projection", ortho);

    System.out.println("tout va bien");

    /*
    ########################################
    Char Quads
    ########################################
     */

    STBTTAlignedQuad shaderQuad = STBTTAlignedQuad.create();

    List<STBTTAlignedQuad> stringQuads = new ArrayList<>();

    float[] myStringPositions;
    float[] myStringTexCoords;
    try (MemoryStack stack = MemoryStack.stackPush()) {

      FloatBuffer x = stack.floats(0f);
      FloatBuffer y = stack.floats(0f);
      x.put(200).flip();
      y.put(50).flip();

      stbtt_GetBakedQuad(charData, bitMapW, bitMapH,
          'F' - 32, // reminder that the offset is 32
          x, y, shaderQuad, true);

      x.put(350).flip();
      y.put(125).flip();
      char[] chars = new char[myString.length()];
      myString.getChars(0, myString.length(), chars, 0);

      for (char aChar : chars) {
        STBTTAlignedQuad oneQuad = STBTTAlignedQuad.create();

        stbtt_GetBakedQuad(charData, bitMapW, bitMapH,
            aChar - 32, // reminder that the offset is 32
            x, y, oneQuad, true);

        stringQuads.add(oneQuad);
      }

      myStringPositions = new float[stringQuads.size() * 12];
      myStringTexCoords = new float[stringQuads.size() * 12];

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
        System.arraycopy(positions, 0, myStringPositions, i * positions.length, positions.length);

        float[] textureCoords = new float[]{
            s0, t0,
            s1, t0,
            s0, t1,

            s1, t0,
            s1, t1,
            s0, t1
        };

        System.arraycopy(textureCoords, 0, myStringTexCoords, i * textureCoords.length,
            textureCoords.length);
      }
    }


    /*
    ########################################
    Bind openGL
    ########################################
     */
    vao = glGenVertexArrays();
    positionVBO = glGenBuffers();

    try (MemoryStack stack = MemoryStack.stackPush()) {

      glBindBuffer(GL_ARRAY_BUFFER, positionVBO);

      FloatBuffer stringPosBufferData = stack.mallocFloat(myStringPositions.length);

      stringPosBufferData.put(myStringPositions);
      stringPosBufferData.flip();

      glBufferData(GL_ARRAY_BUFFER, stringPosBufferData, GL_STATIC_DRAW);

    }

    textureVBO = glGenBuffers();

    try (MemoryStack stack = MemoryStack.stackPush()) {

      glBindBuffer(GL_ARRAY_BUFFER, textureVBO);

      FloatBuffer stringTexBufferData = stack.mallocFloat(myStringTexCoords.length);

      stringTexBufferData.put(myStringTexCoords);
      stringTexBufferData.flip();

      glBufferData(GL_ARRAY_BUFFER, stringTexBufferData, GL_STATIC_DRAW);
    }

    glBindVertexArray(vao);

    glBindBuffer(GL_ARRAY_BUFFER, positionVBO);
    glVertexAttribPointer(0, 2, GL_FLOAT, false, 2 * Float.BYTES, 0);
    glEnableVertexAttribArray(0);

    glBindBuffer(GL_ARRAY_BUFFER, textureVBO);
    glVertexAttribPointer(1, 2, GL_FLOAT, false, 2 * Float.BYTES, 0);
    glEnableVertexAttribArray(1);

    glBindVertexArray(0);

  }

  int textureVBO;
  Shader shader;

  @Override
  public void render() {
  }


  @Override
  public void update(double dt) {

//    drawText(camera.toString(), 300, 400);

    float baseY = 320;
    drawText("mouse x: " + MouseListener.instance.x + " y: " + MouseListener.instance.y, 250, baseY);
    float inc = 25;
    baseY += inc;

    drawText(String.format("window w: %.0f h: %.0f", Window.getInstance().getWidth(), Window.getInstance().getHeight()), 0, 20);
    // Scale to -1;1
    double normalizedDeviceCoordX = ((MouseListener.instance.x / Window.getInstance().getWidth()) * 2) - 1;

    /* on prend l'opposé car pour glfw les y positifs sont vers le bas
    et pour opengl les y positifs sont vers le haut
     */
    double normalizedDeviceCoordY = 1 - ((MouseListener.instance.y / Window.getInstance().getHeight()) * 2);

    drawText(String.format("mouse ndc x: %.2f y: %.2f", normalizedDeviceCoordX, normalizedDeviceCoordY), 250, baseY);

    baseY += inc;

    Matrix4f projectionMatrix = new Matrix4f();
    Matrix4f inverseProjection = new Matrix4f();

    // le centre du frustum est en (W/2, H/2)
    projectionMatrix.ortho(0.0f, Window.getInstance().getWidth(),
        0.0f, Window.getInstance().getHeight(),
        0.0f, 100.0f);
    float[] mat1 = new float[16];
    projectionMatrix.get(mat1);
    projectionMatrix.invert(inverseProjection);

    /*
    passer de la position à l'écran vers les coordonnées du monde
    Des coords de l'écran/viewport [(0,W), (0,H)] aux coords normalisées glPos [(-1,1), (-1,-1)] (normalized device coord)
    doubled = (x * 2) => in range (0, 2W)
    doubled and divided = (x * 2) / W => in range (0, 2)
    doubled and divided and translated = (x*2) / W - 1 => in range (-1, 1)

    glPos = Projection * View * World
     Projection-1 * glPos = View * World
     View-1 * Projection-1 * glPos = World
     glPos [-1,1]

     */

    Vector4f vector4f = new Vector4f((float) normalizedDeviceCoordX, (float) normalizedDeviceCoordY, 0, 0);
    inverseProjection.transform(vector4f);

    /*
    l'origine du monde est le centre de la window avec les x positifs vers la droite
    les y positifs vers le haut
     */
    drawText(String.format("world mouse x : %.2f y: %.2f", vector4f.x, vector4f.y), 250, baseY);

    baseY += inc;


    projectionMatrix.identity();
    float[] id = new float[16];
    projectionMatrix.get(id);
    // le centre du frustum est en (0,0)
    projectionMatrix.ortho(-Window.getInstance().getWidth() / 2, Window.getInstance().getWidth() / 2,
        -Window.getInstance().getHeight() / 2, Window.getInstance().getHeight()/2,
        0.0f, 100.0f);
    float[] mat2 = new float[16];
    projectionMatrix.get(mat2);
    projectionMatrix.invert(inverseProjection);

    vector4f.set((float) normalizedDeviceCoordX, (float) normalizedDeviceCoordY, 0, 0);
    inverseProjection.transform(vector4f);
    drawText(String.format("world mouse x 2 : %.2f y: %.2f", vector4f.x, vector4f.y), 250, baseY);


  }

  @Override
  public void imGui() {

    ImGui.text("font rendering scene");
  }
}
