package org.jade.scenes;

import org.jade.render.ColoredQuadRenderer;
import org.jade.render.ColoredQuadRenderer.ColoredQuad;
import org.jade.render.ColoredQuadRenderer.ColoredVertex;
import org.jade.render.shader.Shader;

public class BatchedColoredQuadsScene extends AbstractScene {

  private ColoredQuadRenderer renderer;

  private Shader shader;

  @Override
  public void load() {
    ColoredVertex[] vertices = new ColoredVertex[4];

    vertices[0] = new ColoredVertex(new float[] {0.5f,  0.5f, 0.0f}, new float[] {1.0f, 0.0f, 0.0f, 1});
    vertices[1] = new ColoredVertex(new float[] {0.5f, -0.5f, 0.0f}, new float[] {0.0f, 1.0f, 0.0f, 1f});
    vertices[2] = new ColoredVertex(new float[] {-0.5f, -0.5f, 0.0f}, new float[] {0.0f, 0.0f, 1.0f, 1f});
    vertices[3] = new ColoredVertex(new float[] {-0.5f,  0.5f, 0.0f}, new float[] {1.0f, 1.0f, 0.0f, 1f});

    ColoredQuad quad = new ColoredQuad(vertices);

    renderer = new ColoredQuadRenderer();

    renderer.addQuad(quad);

    // translate right
    vertices[0].getPosition()[0] += .5f;
    vertices[1].getPosition()[0] += .5f;
    vertices[2].getPosition()[0] += .5f;
    vertices[3].getPosition()[0] += .5f;

    quad = new ColoredQuad(vertices);
    renderer.addQuad(quad);

    // translate up
    vertices[0].getPosition()[1] += .5f;
    vertices[1].getPosition()[1] += .5f;
    vertices[2].getPosition()[1] += .5f;
    vertices[3].getPosition()[1] += .5f;

    quad = new ColoredQuad(vertices);
    renderer.addQuad(quad);

    for (int i = 0; i < 9_999; i++) {
      renderer.addQuad(quad);
    }

    shader = new Shader("shaders/colored-renderer/vertexShader.glsl",
        "shaders/colored-renderer/fragmentShader.glsl");
    renderer.setShader(shader);

  }

  @Override
  public void unload() {
    renderer.clean();
    shader.delete();

  }

  @Override
  public void render() {
    renderer.render();
  }
}
