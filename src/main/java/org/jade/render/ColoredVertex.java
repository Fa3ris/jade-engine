package org.jade.render;

public class ColoredVertex {

  private final float[] position; // xyz

  private final float[] color; // rgba

  public ColoredVertex(float[] position, float[] color) {
    if (position.length != 3 || color.length != 4) throw new IllegalArgumentException("incorrect args");

    this.position = position;
    this.color = color;
  }

  public float[] getPosition() {
    return position;
  }

  public float[] getColor() {
    return color;
  }
}
