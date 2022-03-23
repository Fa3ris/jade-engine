package org.jade.scenes;

import org.components.SpriteComponent;
import org.components.SpriteRenderer;
import org.jade.ecs.ECS;
import org.jade.ecs.Entity;
import org.jade.render.Sprite;
import org.jade.render.shader.Shader;
import org.jade.render.texture.Texture;
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

    spriteComponent = new SpriteComponent(pool.getSprite("textures/wall.jpg"));

    dummy.addComponent(spriteComponent);

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
    float bottomCoord = 0;

    float leftCoord = 0;
    float rightCoord = 1;
    aWall.setTexCoords(topCoord, leftCoord, bottomCoord, rightCoord);
    aWall.setEntity(entity);
    entity.addComponent(aWall);

    spriteRenderer.addSprite(aWall);
  }


  @Override
  public void unload() {
  }

  double elapsed;
  double waitTime = 1;

  int pos = 1;

  @Override
  public void update(double dt) {
    super.update(dt);
    logger.info("update sprite renderer scene");
    entity.update(dt);
    spriteRenderer.update(dt);

    elapsed += dt;
    if (elapsed > waitTime) {

      elapsed = -waitTime;

      if (pos == 1) {
        spriteComponent.setPos(.5f, -.5f, -.5f, .5f);
        pos = 2;
      } else {
        pos = 1;
        float delta = .5f;
        spriteComponent.setPos(.5f + delta, -.5f + delta, -.5f + delta, .5f + delta);
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
}
