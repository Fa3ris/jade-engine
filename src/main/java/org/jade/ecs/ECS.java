package org.jade.ecs;

import java.util.ArrayList;
import java.util.List;

/*
       ECS
       Définition générale :

       entité = un id uniquement
       composant = data
       système = algos

       comment gerer la communication ????????

       init entity
       add component

       add entity to system
       each system updates // what order ?

        goal
       render the state of the game every frame
       so make sense to update and render


       split between updater and renderer ? NO


       how make systems communicate with each other ?

       idea
       system.add(entity)

       system.update() {
         for each entity
           get relevant components
             maybe store for computing related use
               if need to compare collected components with each others
            do your thing
        }

        rendering
          how to batch things ?
            give id for the vertices index

            seems not advised to delete part of vbo - so we do not delete
            put it off screen

 */

public class ECS {

  private final List<Entity> entities = new ArrayList<>();

  private final List<System> systems = new ArrayList<>();

  public boolean addEntity(Entity entity) {
    entities.add(entity);
    return true;
  }

  public boolean removeEntity(Entity entity) {

    entities.remove(entity);
    // clean up all the resource

    return true;
  }

  public boolean addSystem(System system) {
    systems.add(system);
    return true;
  }

  public boolean removeSystem(System system) {
    systems.remove(system);
    return true;
  }

  public void update(double dt) {
    for (System system : systems) {
      system.update(dt, entities);
    }
  }

  public void render() {
    for (System system : systems) {
      system.render(entities);
    }
  }

}
