package org.jade.scenes;

import org.jade.render.LayingTile;
import org.jade.render.Triangles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LayingTileScene extends AbstractScene {

  private static final Logger logger = LoggerFactory.getLogger(LayingTileScene.class);

  private LayingTile layingTile;

  @Override
  public void load() {
    layingTile = new LayingTile();
  }

  @Override
  public void render() {
    layingTile.render();
  }

  @Override
  public void unload() {
    layingTile.clean();
    layingTile = null;
  }
}
