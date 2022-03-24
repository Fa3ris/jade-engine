package org.jade.scenes;

import static imgui.ImGui.begin;
import static imgui.ImGui.button;
import static imgui.ImGui.end;
import static imgui.ImGui.text;

import org.components.SpriteComponent;
import org.components.SpriteRenderer;
import org.jade.ecs.ECS;
import org.jade.ecs.Entity;
import org.jade.render.Sprite;
import org.jade.render.SpriteSheet;
import org.jade.render.camera.Camera;
import org.jade.render.shader.Shader;
import org.jade.render.texture.Texture;
import org.joml.Matrix4f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.systems.RenderSystem;

public class SpriteRendererScene extends AbstractScene {

  private static final Logger logger = LoggerFactory.getLogger(SceneManager.class);

  SpriteRenderer spriteRenderer;

  private Entity entity;

  ECS ecs;

  SpriteComponent spriteComponent;

  @Override
  public void load() {

    ecs = new ECS();
    RenderSystem renderSystem = new RenderSystem();
    renderSystem.setPool(pool);
    ecs.addSystem(renderSystem);

    Entity dummy = new Entity();

    SpriteSheet spriteSheet = pool.getSpriteSheet("textures/spritesheet.png", 16f, 16f, 0f, 0f);

    spriteSheet.setTotalSprites(26); // remove empty sprites at the end
    Sprite firstSprite = spriteSheet.get(spriteIndex);
    spriteComponent = new SpriteComponent(firstSprite);

    dummy.addComponent(spriteComponent);

    activeEntity = dummy;

    ecs.addEntity(dummy);

    Texture wallTexture = pool.getTexture("textures/wall.jpg");
    Texture marioTexture = pool.getTexture("textures/mario.png");
    wallTexture.load(false);
    marioTexture.load(true, true);


    Shader shader = pool.getShader("shaders/textured-renderer/vertexShader.glsl",
        "shaders/textured-renderer/fragmentShader.glsl");


    spriteRenderer = new SpriteRenderer();

    spriteRenderer.setVertexAttributeSizes(new int[]{3, 2, 1});
    spriteRenderer.start();

    spriteRenderer.setShader(shader);
    spriteRenderer.addTexture(wallTexture);
    spriteRenderer.addTexture(marioTexture);

    entity = new Entity();
    Sprite aWall = new Sprite();

    float topPos = .5f;
    float leftPos = -.5f;

    float rightPos = .5f;
    float bottomPos = -.5f;
    aWall.setPos(topPos, leftPos, bottomPos, rightPos);

    float topCoord = 1;
    float bottomCoord = .50f;

    float leftCoord = 0;
    float rightCoord = .07f;

    aWall.setTexCoords(topCoord, leftCoord, bottomCoord, rightCoord);
    aWall.setEntity(entity);
    entity.addComponent(aWall);

    spriteRenderer.addSprite(aWall);
  }


  @Override
  public void setCamera(Camera camera) {
      ecs.setCamera(camera);
  }

  @Override
  public void setFOV(double angleDegree) {
      ecs.setFOV(angleDegree);
  }


  @Override
  public void unload() {
  }

  double elapsed;
  double waitTime = 1;
  int pos = 1;

  int spriteIndex = 0;
  double elapsedSwitchSprite;
  double switchSpriteTime = .5;

  @Override
  public void update(double dt) {
    super.update(dt);
    logger.info("update sprite renderer scene");
    entity.update(dt);
    spriteRenderer.update(dt);

    elapsedSwitchSprite += dt;
    if (elapsedSwitchSprite > switchSpriteTime) {
      elapsedSwitchSprite -= switchSpriteTime;

      SpriteSheet spriteSheet = pool.getSpriteSheet("textures/spritesheet.png", 16f, 16f, 0f, 0f);

      spriteIndex++;
      if (spriteIndex >= spriteSheet.size()) {
        spriteIndex = 0;
      }
      spriteComponent.setSprite(spriteSheet.get(spriteIndex));
      logger.info("changed sprite to index {}", spriteIndex);
      spriteComponent.setDirty();
    }

    elapsed += dt;
    if (elapsed > waitTime) {

      elapsed = -waitTime;

      if (pos == 1) {
        spriteComponent.transform(new Matrix4f());
        pos = 2;
      } else {
        pos = 1;
        float delta = .5f;
        spriteComponent.transform(new Matrix4f().translate(delta, delta, 0));
      }
      logger.info("changed to position {}", pos);
      spriteComponent.setDirty();
    }
    ecs.update(dt);
  }

  @Override
  public void render() {
    if (false) {
      spriteRenderer.render();
    }
    ecs.render();
  }

  private boolean showText = true;

  @Override
  public void imGui() {
    // actual logic

    begin("Sprite renderer scene imgui window");
    if (button("toggle text")) {
      showText = !showText;
    }

    if (showText) {
      text("Hello, Dear IMGui");
    }

    if (activeEntity != null) {
      activeEntity.imGui();
    }
    // actual logic END
    end();
  }

}
