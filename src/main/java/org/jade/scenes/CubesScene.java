package org.jade.scenes;

import org.jade.render.Cube;
import org.jade.render.Triangles;
import org.jade.render.camera.Camera;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CubesScene extends AbstractScene {

  private static final Logger logger = LoggerFactory.getLogger(CubesScene.class);

  private Cube cube;

  private final Vector3f[] cubePositions = {
      new Vector3f( 0.0f,  0.0f,  0.0f),
      new Vector3f( 2.0f,  5.0f, -15.0f),
      new Vector3f(-1.5f, -2.2f, -2.5f),
      new Vector3f(-3.8f, -2.0f, -12.3f),
      new Vector3f( 2.4f, -0.4f, -3.5f),
      new Vector3f(-1.7f,  3.0f, -7.5f),
      new Vector3f( 1.3f, -2.0f, -2.5f),
      new Vector3f( 1.5f,  2.0f, -2.5f),
      new Vector3f( 1.5f,  0.2f, -1.5f),
      new Vector3f(-1.3f,  1.0f, -1.5f)
  };

  @Override
  public void load() {

    cube = new Cube();
  }

  @Override
  public void setCamera(Camera camera) {
    cube.setView(camera.getLookAt());
  }

  @Override
  public void setFOV(double angleDegree) {
    cube.setFOV(angleDegree);
  }

  @Override
  public void render() {
    for (int i = 0; i < cubePositions.length; i++) {
      cube.setTranslation(cubePositions[i]);
      cube.setRotationOffset(20 * i);
      cube.render();
    }
  }

  @Override
  public void unload() {
    cube = null;
  }
}
