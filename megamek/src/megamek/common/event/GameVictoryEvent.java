/*
 * MegaMek - Copyright (C) 2005 Ben Mazur (bmazur@sev.org)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 */
package megamek.common.event;

import megamek.common.Entity;
import megamek.common.Game;
import megamek.common.IEntityRemovalConditions;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * An event that is fired at the end of the victory phase, before the game state
 * is reset. It can be used to retrieve information from the game before the
 * state is reset and the lounge phase begins.
 *
 * @see Game#end(int, int)
 * @see GameListener
 */
public class GameVictoryEvent extends GameEvent implements PostGameResolution {
    private static final long serialVersionUID = -8470655646019563063L;

    /**
     * Track game entities
     */
    private Vector<Entity> entities = new Vector<>();
    private Hashtable<Integer, Entity> entityIds = new Hashtable<>();

    /**
     * Track entities removed from the game (probably by death)
     */
    Vector<Entity> vOutOfGame = new Vector<>();

    /**
     * @param source event source
     */
    @SuppressWarnings("unchecked")
    public GameVictoryEvent(Object source, Game game) {
        super(source);
        for (Entity entity : game.getEntitiesVector()) {
            entities.add(entity);
            entityIds.put(entity.getId(), entity);
        }

        vOutOfGame = (Vector<Entity>) game.getOutOfGameEntitiesVector().clone();
        for (Entity entity : vOutOfGame) {
            entityIds.put(entity.getId(), entity);
        }
    }

    @Override
    public void fireEvent(GameListener gl) {
        gl.gameVictory(this);
    }

    @Override
    public String getEventName() {
        return "Game Victory";
    }

    /**
     * @return an enumeration of all the entities in the game.
     */
    @Override
    public Enumeration<Entity> getEntities() {
        return entities.elements();
    }

    /**
     * @return the entity with the given id number, if any.
     */
    @Override
    public Entity getEntity(int id) {
        return entityIds.get(id);
    }

    /**
     * @return an enumeration of salvageable entities.
     */
    // TODO: Correctly implement "Captured" Entities
    @Override
    public Enumeration<Entity> getGraveyardEntities() {
        Vector<Entity> graveyard = new Vector<>();

        for (Entity entity : vOutOfGame) {
            if (isGraveyardEntity(entity)) {
                graveyard.addElement(entity);
            }
        }

        return graveyard.elements();
    }

    /**
     * Checks whether an entity should be considered as belonging to the
     * "graveyard".
     * This includes entities that have been:
     * - recovered as salvageable,
     * - captured by the enemy,
     * - ejected from the battlefield.
     *
     * @param e the entity to evaluate.
     * @return {@code true} if the entity meets the graveyard criteria,
     *         {@code false} otherwise.
     */
    private boolean isGraveyardEntity(Entity e) {
        int cond = e.getRemovalCondition();
        return cond == IEntityRemovalConditions.REMOVE_SALVAGEABLE ||
                cond == IEntityRemovalConditions.REMOVE_CAPTURED ||
                cond == IEntityRemovalConditions.REMOVE_EJECTED;
    }
    /**
     * @return an enumeration of wrecked entities.
     */
    @Override
    public Enumeration<Entity> getWreckedEntities() {
        Vector<Entity> wrecks = new Vector<>();
        for (Entity entity : vOutOfGame) {
            if (isWreckedEntity(entity)) {
                wrecks.addElement(entity);
            }
        }
        return wrecks.elements();
    }

    /**
     * Checks whether an entity should be considered as "wrecked".
     *
     * @param e the entity to evaluate.
     * @return {@code true} if the entity meets the wrecked criteria,
     *         {@code false} otherwise.
     */
    private boolean isWreckedEntity(Entity e) {
        int cond = e.getRemovalCondition();
        return cond == IEntityRemovalConditions.REMOVE_SALVAGEABLE ||
                cond == IEntityRemovalConditions.REMOVE_EJECTED ||
                cond == IEntityRemovalConditions.REMOVE_CAPTURED;
    }

    /**
     * Returns an enumeration of entities that have retreated
     */
    @Override
    public Enumeration<Entity> getRetreatedEntities() {
        Vector<Entity> sanctuary = new Vector<>();

        for (Entity entity : vOutOfGame) {
            if ((entity.getRemovalCondition() == IEntityRemovalConditions.REMOVE_IN_RETREAT)
                    || (entity.getRemovalCondition() == IEntityRemovalConditions.REMOVE_PUSHED)) {
                sanctuary.addElement(entity);
            }
        }

        return sanctuary.elements();
    }

    /**
     * Returns an enumeration of entities that were utterly destroyed
     */
    @Override
    public Enumeration<Entity> getDevastatedEntities() {
        Vector<Entity> smithereens = new Vector<>();

        for (Entity entity : vOutOfGame) {
            if (entity.getRemovalCondition() == IEntityRemovalConditions.REMOVE_DEVASTATED) {
                smithereens.addElement(entity);
            }
        }

        return smithereens.elements();
    }
}
