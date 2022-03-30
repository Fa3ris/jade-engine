package org.jade.scenes;

import static imgui.ImGui.begin;
import static imgui.ImGui.button;
import static imgui.ImGui.end;
import static imgui.ImGui.text;

import org.components.SpriteComponent;
import org.jade.ecs.ECS;
import org.jade.ecs.Entity;
import org.jade.render.SpriteSheet;
import org.jade.render.camera.Camera;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.systems.RenderSystem;

public class SpriteRendererScene extends AbstractScene {

  private static final Logger logger = LoggerFactory.getLogger(SceneManager.class);

  ECS ecs;

  @Override
  public void load() {

    ecs = new ECS();
    RenderSystem renderSystem = new RenderSystem();
    renderSystem.setPool(pool);
    ecs.addSystem(renderSystem);

    SpriteSheet spriteSheet = pool.getSpriteSheet("textures/spritesheet.png", 16f, 16f, 0f, 0f);
    spriteSheet.setTotalSprites(26); // remove empty sprites at the end

    Entity entity = new Entity();
    entity.addComponent(new SpriteComponent(spriteSheet));

    activeEntity = entity;

    ecs.addEntity(entity);
  }


  @Override
  public void setCamera(Camera camera) {
      ecs.setCamera(camera);
  }

  @Override
  public void setFOV(double angleDegree) {
      ecs.setFOV(angleDegree);
  }


  @Override
  public void unload() {
  }

  @Override
  public void update(double dt) {
    logger.trace("update sprite renderer scene");
    ecs.update(dt);
  }

  @Override
  public void render() {
    ecs.render();
  }

  private boolean showText = true;

  @Override
  public void imGui() {

    // push window
    begin("Sprite renderer scene imgui window");
    if (button("toggle text")) {
      showText = !showText;
    }

    if (showText) {
      text("Hello, Dear IMGui");
    }

    if (activeEntity != null) {
      activeEntity.imGui();
    }
    // pop window
    end();
  }

}
