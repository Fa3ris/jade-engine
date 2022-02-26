package org.jade.render;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class LayingTile {

  final private TexturedQuad texturedQuad;

  public LayingTile() {

    Vector3f rotateXAxis = new Vector3f(1f, 0f, 0f);
    Matrix4f model = new Matrix4f();
    model.rotate((float) Math.toRadians(-55f), rotateXAxis);

    Vector3f moveSceneForward = new Vector3f(0f, 0f, -3f);
    Matrix4f view = new Matrix4f();
    view.translate(moveSceneForward); // move the entire scene forward == move camera backwards

    Matrix4f projection = new Matrix4f();
    projection.perspective(
        (float) Math.toRadians(45f), // field of view
        800f/600f, // aspect ratio
        0.1f, // near plane
        100f, // far plane
        false); // z axis in range 0:1 of -1:1

    projection.mul(view.mul(model)); // result is stored in projection
    texturedQuad = new TexturedQuad();
    texturedQuad.applyTransform(projection);
  }

  public void render() {
    texturedQuad.render();
  }
}
