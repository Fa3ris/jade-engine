package org.jade.render.camera;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * objective: always define a basis with forward, left and up
 * and keep them up to date when rotate around one axis
 */
public class Camera {

  private static final Logger logger = LoggerFactory.getLogger(Camera.class);

  private final Vector3f forward = new Vector3f(0, 0, -1);

  private final Vector3f left = new Vector3f(-1, 0, 0);

  private final Vector3f up = new Vector3f(0, 1, 0);

  private final Vector3f position;

  private final Matrix4f lookAt = new Matrix4f();

  /** used for vector operations without transforming the original vectors */
  private final Vector3f tempV3f = new Vector3f();

  private float rotateScale = 5e-2f;
  private float moveScale = 1e-1f;

  public Camera() {
    this(0f, 0f, 3f);
  }

  public Camera(float x, float y, float z) {
    position = new Vector3f(x, y, z);
    updateLookAt();
  }

  public Matrix4f getLookAt() {
    return lookAt;
  }

  /**
   * yaw
   * rotate forward around up and left = up cross forward
   */
  public void yaw(double offset) {
    if (false) {
      offset = Math.min(Math.abs(offset), .2) * Math.signum(offset);
    }
    logger.info("yaw offset = {}", offset);
    forward.rotateAxis((float) (-offset * rotateScale), up.x, up.y, up.z).normalize();
    up.cross(forward, left).normalize();
    updateLookAt();
  }

  /**
   * pitch
   * rotate up around left and forward = left cross up
   */
  public void pitch(double offset) {
    if (false) {
      offset = Math.min(Math.abs(offset), .2) * Math.signum(offset);
    }
    logger.info("pitch offset = {}", offset);
    up.rotateAxis((float) (offset * rotateScale), left.x, left.y, left.z).normalize();
    left.cross(up, forward).normalize();
    updateLookAt();
  }

  /**
   * roll
   * rotate left around forward and up = forward cross left
   */
  public void roll(double offset) {
    if (false) {
      offset = Math.min(Math.abs(offset), .2) * Math.signum(offset);
    }
    logger.info("roll offset = {}", offset);
    left.rotateAxis((float) (Math.signum(offset) * 2e-2), forward.x, forward.y, forward.z).normalize();
    forward.cross(left, up).normalize();
    updateLookAt();
  }

  public void forward() {
    position.add(tempV3f.set(forward).mul(moveScale));
    updateLookAt();
  }

  public void backward() {
    position.sub(tempV3f.set(forward).mul(moveScale));
    updateLookAt();
  }

  public void strafeLeft() {
    position.add(tempV3f.set(left).mul(moveScale));
    updateLookAt();
  }

  public void strafeRight() {
    position.sub(tempV3f.set(left).mul(moveScale));
    updateLookAt();
  }

  private void updateLookAt() {
    lookAt.setLookAt(position, tempV3f.set(position).add(forward) , up);
  }
}
