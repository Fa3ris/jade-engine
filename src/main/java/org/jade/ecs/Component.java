package org.jade.ecs;

public abstract class Component {

  protected Entity entity;

  public void start() {}
  public abstract void update(double dt);
}
