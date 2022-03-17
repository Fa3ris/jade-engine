package org.jade.scenes;

import java.lang.reflect.Constructor;

public abstract class SceneManagerFactory {

  private static final String[] sceneClassNames = {
      "org.jade.scenes.SpriteRendererScene",
      "org.jade.scenes.BatchedTexturedQuadsScene",
      "org.jade.scenes.BatchedColoredQuadsScene",
      "org.jade.scenes.CubesScene",
      "org.jade.scenes.LayingTileScene",
      "org.jade.scenes.CircularRotationTexturedQuadScene",
      "org.jade.scenes.ScaleThenRotateTexturedQuadScene",
      "org.jade.scenes.TranslateThenRotateTexturedQuadScene",
      "org.jade.scenes.ColoredTriangleScene",
      "org.jade.scenes.UpdatingTriangleScene",
      "org.jade.scenes.LinkedTriangleScene",
      "org.jade.scenes.YellowTriangleScene",
      "org.jade.scenes.Triangle2Scene",
      "org.jade.scenes.Triangle1Scene",
      "org.jade.scenes.TwoTrianglesScene",
      "org.jade.scenes.SingleTriangleScene",
      "org.jade.scenes.GradientTriangleScene"
  };

  public static SceneManager createInstance() throws SceneManagerInitializationException {

    SceneManager manager = new SceneManager();
    for (String sceneClassName : sceneClassNames) {
      try {
        Class<?> clazz = Class.forName(sceneClassName);
        Constructor<?> constructor = clazz.getConstructor();
        Scene scene = (Scene) constructor.newInstance();
        manager.addScene(scene);
      } catch (Exception e) {
        throw new SceneManagerInitializationException("cannot init scene manager", e);
      }
    }

    manager.init();
    return manager;
  }

  static class SceneManagerInitializationException extends RuntimeException {

    public SceneManagerInitializationException() {
      super();
    }

    public SceneManagerInitializationException(String message) {
      super(message);
    }

    public SceneManagerInitializationException(String message, Throwable cause) {
      super(message, cause);
    }

    public SceneManagerInitializationException(Throwable cause) {
      super(cause);
    }
  }

}
