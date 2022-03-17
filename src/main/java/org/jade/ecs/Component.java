package org.jade.ecs;

public abstract class Component {

  protected Entity entity;

  public void start() {}

  public void setEntity(Entity entity) {
    this.entity = entity;
  }
  public abstract void update(double dt);
}
