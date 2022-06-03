package org.jade.ecs;

public abstract class Component {

  @Deprecated(forRemoval = true, since = "now")
  protected transient Entity entity;

  public void start() {}

  public void update(double dt) {}

  public void imGui() {}
}
