package org.jade.ecs;

public abstract class Component {

  // TODO: remove reference to entity
  protected transient Entity entity;

  public void start() {}

  public void setEntity(Entity entity) {
    this.entity = entity;
  }
  public void update(double dt) {}

  public void imGui() {}
}
