package org.jade.scenes;

import org.components.SpriteRenderer;
import org.jade.ecs.Entity;
import org.jade.render.Sprite;
import org.jade.render.TexturedQuadRenderer;
import org.jade.render.TexturedQuadRenderer.TexturedQuad;
import org.jade.render.TexturedQuadRenderer.TexturedVertex;
import org.jade.render.pool.ResourcePool;
import org.jade.render.shader.Shader;
import org.jade.render.texture.Texture;

public class BatchedTexturedQuadsScene extends AbstractScene {

  private TexturedQuadRenderer texturedQuadRenderer;

  private ResourcePool pool;

  SpriteRenderer spriteRenderer;

  @Override
  public void setPool(ResourcePool pool) {
    this.pool = pool;
  }

  @Override
  public void load() {
    texturedQuadRenderer = new TexturedQuadRenderer();

    TexturedVertex[] vertices = new TexturedVertex[4];

    // in order: top-left => top-right => bottom-right => bottom-left
    float texId0 = 0;
    vertices[0] = new TexturedVertex(new float[] {-0.5f,  0.5f, 0.0f}, new float[] {0f, 1f}, texId0);
    vertices[1] = new TexturedVertex(new float[] {0.5f, 0.5f, 0.0f}, new float[] {1f, 1f}, texId0);
    vertices[2] = new TexturedVertex(new float[] {0.5f, -0.5f, 0.0f}, new float[] {1, 0f}, texId0);
    vertices[3] = new TexturedVertex(new float[] {-0.5f,  -0.5f, 0.0f}, new float[] {0f, 0f}, texId0);

    TexturedQuad quad = new TexturedQuad(vertices);

    texturedQuadRenderer.addQuad(quad);

    float texId1 = 1;
    float translateX = .2f;
    float translateY = .3f;
    vertices[0] = new TexturedVertex(new float[] {-0.5f + translateX,  0.5f+ translateY, 0.0f}, new float[] {0f, 1f}, texId1);
    vertices[1] = new TexturedVertex(new float[] {0.5f + translateX, 0.5f+ translateY, 0.0f}, new float[] {1f, 1f}, texId1);
    vertices[2] = new TexturedVertex(new float[] {0.5f+ translateX, -0.5f+ translateY, 0.0f}, new float[] {1, 0f}, texId1);
    vertices[3] = new TexturedVertex(new float[] {-0.5f+ translateX,  -0.5f+ translateY, 0.0f}, new float[] {0f, 0f}, texId1);

    quad = new TexturedQuad(vertices);

    texturedQuadRenderer.addQuad(quad);

    for (int i = 0; i < 5000; i++) {
    }

    Texture wallTexture = pool.getTexture("textures/wall.jpg");
    Texture marioTexture = pool.getTexture("textures/mario.png");
    wallTexture.load(false);
    marioTexture.load(true, true);

    texturedQuadRenderer.addTexture(wallTexture);
    texturedQuadRenderer.addTexture(marioTexture);

    Shader shader = pool.getShader("shaders/textured-renderer/vertexShader.glsl",
        "shaders/textured-renderer/fragmentShader.glsl");

    texturedQuadRenderer.setShader(shader);

    spriteRenderer = new SpriteRenderer();

    spriteRenderer.setVertexAttributeSizes(new int[]{3, 2, 1});
    spriteRenderer.start();

    spriteRenderer.setShader(shader);
    spriteRenderer.addTexture(wallTexture);

    Entity entity = new Entity();

    Sprite aWall = new Sprite();

    spriteRenderer.addSprite(aWall);
  }


  @Override
  public void unload() {
    texturedQuadRenderer.clean();
  }


  @Override
  public void update(double dt) {
    super.update(dt);
    spriteRenderer.update(dt);
  }

  @Override
  public void render() {
//    texturedQuadRenderer.render();
    spriteRenderer.render();
  }
}
