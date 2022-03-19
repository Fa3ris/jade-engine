package org.jade.scenes;

import org.components.SpriteRenderer;
import org.jade.ecs.Entity;
import org.jade.render.Sprite;
import org.jade.render.pool.ResourcePool;
import org.jade.render.shader.Shader;
import org.jade.render.texture.Texture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpriteRendererScene extends AbstractScene {

  private static final Logger logger = LoggerFactory.getLogger(SceneManager.class);

  private ResourcePool pool;

  SpriteRenderer spriteRenderer;

  private Entity entity;
  @Override
  public void setPool(ResourcePool pool) {
    this.pool = pool;
  }

  @Override
  public void load() {

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


  @Override
  public void update(double dt) {
    super.update(dt);
    logger.info("update sprite renderer scene");
    entity.update(dt);
    spriteRenderer.update(dt);
  }

  @Override
  public void render() {
    spriteRenderer.render();
  }
}
