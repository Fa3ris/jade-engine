package org.jade.render;

import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;

import org.jade.render.texture.Texture;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL30;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Cube {

  private static final Logger logger = LoggerFactory.getLogger(Cube.class);

  private final Triangles triangles;

  private final Texture wallTexture;
  private final Texture smileyTexture;

  private final Matrix4f rotation = new Matrix4f();
  private final Vector3f rotationAxis = new Vector3f(.5f, 1f, 0f);

  private final Matrix4f projection = new Matrix4f();

  private Vector3f translation;
  private int rotationOffset;

  public Cube() {

    float[] vertices = {
        // positions          // texture coords
        -0.5f, -0.5f, -0.5f,  0.0f, 0.0f,
        0.5f, -0.5f, -0.5f,  1.0f, 0.0f,
        0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
        0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
        -0.5f,  0.5f, -0.5f,  0.0f, 1.0f,
        -0.5f, -0.5f, -0.5f,  0.0f, 0.0f,

        -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
        0.5f, -0.5f,  0.5f,  1.0f, 0.0f,
        0.5f,  0.5f,  0.5f,  1.0f, 1.0f,
        0.5f,  0.5f,  0.5f,  1.0f, 1.0f,
        -0.5f,  0.5f,  0.5f,  0.0f, 1.0f,
        -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,

        -0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
        -0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
        -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
        -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
        -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
        -0.5f,  0.5f,  0.5f,  1.0f, 0.0f,

        0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
        0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
        0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
        0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
        0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
        0.5f,  0.5f,  0.5f,  1.0f, 0.0f,

        -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
        0.5f, -0.5f, -0.5f,  1.0f, 1.0f,
        0.5f, -0.5f,  0.5f,  1.0f, 0.0f,
        0.5f, -0.5f,  0.5f,  1.0f, 0.0f,
        -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
        -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,

        -0.5f,  0.5f, -0.5f,  0.0f, 1.0f,
        0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
        0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
        0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
        -0.5f,  0.5f,  0.5f,  0.0f, 0.0f,
        -0.5f,  0.5f, -0.5f,  0.0f, 1.0f
    };

    triangles = new Triangles(vertices, 0, 36,
        "shaders/texture/vertexShader.glsl",
        "shaders/texture/fragmentShader.glsl");

    triangles.configVertexAttribute(0, 3, 5*Float.BYTES, 0);
    triangles.configVertexAttribute(2, 2, 5*Float.BYTES, 3*Float.BYTES);

    triangles.shader.setUniform1iv("texture1", 0);

    triangles.shader.setUniform1iv("texture2", 1);

    wallTexture = new Texture("textures/wall.jpg", GL_RGB);
    wallTexture.load(false);

    smileyTexture = new Texture("textures/awesomeface.png", GL_RGBA);
    smileyTexture.load(true);

    // static too
    Vector3f moveSceneForward = new Vector3f(0f, 0f, -3f);
    Matrix4f view = new Matrix4f();
    view.translate(moveSceneForward); // move the entire scene forward == move camera backwards

    projection.perspective(
        (float) Math.toRadians(45f), // field of view
        800f/600f, // aspect ratio
        0.1f, // near plane
        100f, // far plane
        false); // z axis in range 0:1 of -1:1

    projection.mul(view); // result is stored in projection
  }


  public void setTranslation(Vector3f translation) {
    this.translation = translation;
  }

  public void setRotationOffset(int offset) {
    rotationOffset = offset;
  }

  public void render() {
    rotation.identity();

    if (translation != null) {
      rotation.translate(translation);
    }

    rotation.rotate((float) (glfwGetTime() * Math.toRadians(50f) + rotationOffset), rotationAxis);

    triangles.shader.setUniformMatrix4fv("transform", new Matrix4f(projection).mul(rotation));

    wallTexture.use(GL_TEXTURE0);
    smileyTexture.use(GL_TEXTURE1);
    GL30.glEnable(GL30.GL_DEPTH_TEST);
    triangles.render();
    GL30.glDisable(GL30.GL_DEPTH_TEST);
  }
}
