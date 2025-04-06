/*
 * Copyright (c) 2024 - The MegaMek Team. All Rights Reserved.
 *
 * This file is part of MegaMek.
 *
 * MegaMek is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MegaMek is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MegaMek. If not, see <http://www.gnu.org/licenses/>.
 */
package megamek.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import megamek.client.ui.swing.util.PlayerColour;
import org.mockito.Mockito;

class PlayerTest {

    private Player  owner; // La classe où se trouve isEligibleEntity
    private Game game;
    @BeforeEach
    public void setup() {
        game = Mockito.mock(Game.class);
        owner = new Player(0, "Test Player 1"); // Passe game si nécessaire
    }
    @Test
    void testGetColorForPlayerDefault() {
        String playerName = "Test Player 1";
        Player player = new Player(0, playerName);
        assertEquals("<B><font color='8080b0'>" + playerName + "</font></B>", player.getColorForPlayer());
    }

    @Test
    void testGetColorForPlayerFuchsia() {
        String playerName = "Test Player 2";
        Player player = new Player(1, playerName);
        player.setColour(PlayerColour.FUCHSIA);
        assertEquals("<B><font color='f000f0'>" + playerName + "</font></B>", player.getColorForPlayer());
    }




    @Test
    public void testIsEligibleEntity_NullOwner() {
        Entity entity = Mockito.mock(Entity.class);
        Mockito.when(entity.getOwner()).thenReturn(null);

        assertFalse(owner.isEligibleEntity(entity));
    }

    @Test
    public void testIsEligibleEntity_DestroyedEntity() {
        Entity entity = Mockito.mock(Entity.class);
        Mockito.when(entity.getOwner()).thenReturn(owner);
        Mockito.when(entity.isDestroyed()).thenReturn(true);

        assertFalse(owner.isEligibleEntity(entity));
    }


    @Test
    public void testCalculateBonus_CommandInitFalse_NoBonusSources() {
        Entity entity = Mockito.mock(Entity.class);
        Crew crew = Mockito.mock(Crew.class);

        Mockito.when(entity.getCrew()).thenReturn(crew);
        Mockito.when(entity.hasCommandConsoleBonus()).thenReturn(false);
        Mockito.when(crew.hasActiveTechOfficer()).thenReturn(false);

        int result = owner.calculateBonus(entity, false);
        assertEquals(0, result); // No bonus at all
    }

    @Test
    public void testGetCommandBonus_GameIsNull() {
        owner = new Player(0,null); // force game=null
        assertEquals(0, owner.getCommandBonus());
    }

    @Test
    void testGetCommandBonus_gameIsNull() {
        String playerName = "Test Player 1";
        // Cas où 'game' est null, la méthode doit retourner 0
        Player player = new Player(1, playerName);


        int result = player.getCommandBonus();

        assertEquals(0, result);
    }


}
