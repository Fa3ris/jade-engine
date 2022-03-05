package org.jade.render;

import static org.lwjgl.opengl.GL30.GL_TEXTURE0;
import static org.lwjgl.opengl.GL30.GL_TEXTURE1;

import org.jade.render.texture.Texture;
import org.joml.Matrix4f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TexturedQuad {

  private static final Logger logger = LoggerFactory.getLogger(TexturedQuad.class);

  private final Triangles triangles;

  private final Texture wallTexture;
  private final Texture smileyTexture;

  public TexturedQuad() {

    wallTexture = new Texture("textures/wall.jpg");
    wallTexture.load(false);

    smileyTexture = new Texture("textures/awesomeface.png");
    smileyTexture.load(true);

    float[] vertices = {
        // positions          // colors           // texture coords
        0.5f,  0.5f, 0.0f,   1.0f, 0.0f, 0.0f,   1.0f, 1.0f,   // top right
        0.5f, -0.5f, 0.0f,   0.0f, 1.0f, 0.0f,   1.0f, 0.0f,   // bottom right
        -0.5f, -0.5f, 0.0f,   0.0f, 0.0f, 1.0f,   0.0f, 0.0f,   // bottom left
        -0.5f,  0.5f, 0.0f,   1.0f, 1.0f, 0.0f,   0.0f, 1.0f    // top left
    };

    int[] indices = {
        0, 1, 2,
        0, 2, 3
    };

    triangles = new Triangles(vertices, indices,
        "shaders/texture/vertexShader.glsl",
        "shaders/texture/fragmentShader.glsl");
    triangles.configVertexAttribute(0, 3, 8*Float.BYTES, 0);
    triangles.configVertexAttribute(1, 3, 8*Float.BYTES, 3*Float.BYTES);
    triangles.configVertexAttribute(2, 2, 8*Float.BYTES, 6*Float.BYTES);


    triangles.shader.setUniform1iv("texture1", 0);

    triangles.shader.setUniform1iv("texture2", 1);
  }


  public void render() {
    wallTexture.use(GL_TEXTURE0);
    smileyTexture.use(GL_TEXTURE1);

    triangles.render();
  }


  public void applyTransform(Matrix4f matrix4f) {
    triangles.shader.setUniformMatrix4fv("transform", matrix4f);
  }

}
