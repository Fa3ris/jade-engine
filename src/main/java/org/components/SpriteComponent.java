package org.components;

import static imgui.ImGui.image;
import static imgui.ImGui.text;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import imgui.ImGui;
import imgui.type.ImFloat;
import imgui.type.ImInt;
import org.jade.ecs.Component;
import org.jade.json.Json;
import org.jade.render.Sprite;
import org.jade.render.SpriteSheet;
import org.jade.render.texture.Texture;
import org.joml.Matrix4f;
import org.joml.Vector4f;

public class SpriteComponent extends Component {

  private Sprite sprite;

  private int spriteRendererIndex = -1;

  private int quadIndex = -1;

  private int textureUnit = -1;

  private boolean isDirty;

  private final Vector4f topLeft = new Vector4f(defaultTopLeft);
  private transient static final Vector4f defaultTopLeft = new Vector4f(-.5f, .5f, 0, 1);

  private final Vector4f topRight = new Vector4f(defaultTopRight);
  private transient static final Vector4f defaultTopRight = new Vector4f(.5f, .5f, 0, 1);

  private final Vector4f bottomLeft = new Vector4f(defaultBottomLeft);
  private transient static final Vector4f defaultBottomLeft = new Vector4f(-.5f, -.5f, 0, 1);

  private final Vector4f bottomRight = new Vector4f(defaultBottomRight);
  private transient static final Vector4f defaultBottomRight = new Vector4f(.5f, -.5f, 0, 1);

  private final transient SpriteSheet spriteSheet;

  private int spriteIndex = -1;

  public SpriteComponent() {
    spriteSheet = null;
  }

  public SpriteComponent(Sprite sprite) {
    this.sprite = sprite;
    this.spriteSheet = null;
  }

  public SpriteComponent(SpriteSheet spriteSheet) {
    spriteIndex = 0;
    this.sprite = spriteSheet.get(spriteIndex);
    this.spriteSheet = spriteSheet;
  }

  public void transform(Matrix4f mat) {
    mat.transform(defaultTopLeft, topLeft);
    mat.transform(defaultTopRight, topRight);
    mat.transform(defaultBottomLeft, bottomLeft);
    mat.transform(defaultBottomRight, bottomRight);
    setDirty();
  }

  public void setDirty() {
    isDirty = true;
  }

  public void clean() {
    isDirty = false;
  }

  /*
    sprite render calls this
   */
  public float[] getVertices() {

    // see if z-position useful
    return new float[] {
        // position                   texCoord                 texId
        topLeft.x,  topLeft.y,    0.0f, sprite.getLeftCoord(),  sprite.getTopCoord(),    textureUnit, // left top
        topRight.x, topRight.y,    0.0f, sprite.getRightCoord(), sprite.getTopCoord(),    textureUnit, // right top
        bottomRight.x, bottomRight.y, 0.0f, sprite.getRightCoord(), sprite.getBottomCoord(), textureUnit, // right bottom
        bottomLeft.x,  bottomLeft.y, 0.0f, sprite.getLeftCoord(),  sprite.getBottomCoord(), textureUnit // left bottom
    };
  }

  public int getSpriteRendererIndex() {
    return spriteRendererIndex;
  }

  public Texture getTexture() {
    return sprite.getTexture();
  }

  public void setTextureUnit(int i) {
    textureUnit = i;
  }

  public void setQuadIndex(int index) {
    quadIndex = index;
  }

  public boolean isDirty() {
    return isDirty;
  }

  public void setSpriteRendererIndex(int i) {
    spriteRendererIndex = i;
  }

  public int getQuadIndex() {
    return quadIndex;
  }

  public void setSprite(Sprite newSprite) {
    sprite = newSprite;
    setDirty();
  }

  private final ImFloat xTranslate = new ImFloat(0);
  private final ImFloat yTranslate = new ImFloat(0);

  @Override
  public void imGui() {
    text("sprite component");
    boolean updateTransform = false;
    if (ImGui.inputFloat("x translate", xTranslate,  0.01f, 1.0f, "%.3f")) {
      updateTransform = true;
    }

    if (ImGui.inputFloat("y translate", yTranslate,  0.01f, 1.0f, "%.3f")) {
      updateTransform = true;
    }

    if (spriteSheet != null) {
      int[] value = new int[]{ spriteIndex };
      if (ImGui.sliderInt("sprite index", value, 0, spriteSheet.size() - 1)) {
        spriteIndex = value[0];
        setSprite(spriteSheet.get(spriteIndex));
      }
    }
    ImGui.text("JSON data");
    String json = Json.toJson(this);
    String json2 = Json.serializeComponent(this);
    SpriteComponent c2 = (SpriteComponent) Json.deserializeAsComponent(json2);
    ImGui.text(json2);

    ImGui.text("JSON data 2");
    String json3 = Json.serializeComponent(c2);

    if (!json2.equals(json3)) {
      throw new AssertionError("json are not identical");
    }
    ImGui.text(json3);

    SpriteComponent copy = Json.fromJson(json, SpriteComponent.class);

    if (updateTransform) {
      transform(new Matrix4f().translate(xTranslate.get(), yTranslate.get(), 0));
    }
  }
}
