package org.jade.ecs;

public abstract class Component {

  Entity entity;

  public void start() {}
  public abstract void update(double dt);
}
