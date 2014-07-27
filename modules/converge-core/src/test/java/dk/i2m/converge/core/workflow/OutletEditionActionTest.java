/*
 * Copyright (C) 2014 Allan Lykke Christensen
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package dk.i2m.converge.core.workflow;

import dk.i2m.converge.core.plugin.EditionAction;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for {@link OutletEdtionAction}.
 *
 * @author Allan Lykke Christensen
 */
public class OutletEditionActionTest {

    @Test
    public void outletEditionAction_manualAction_returnManualActionIstrue() {
        // Arrange
        OutletEditionAction outletAction = new OutletEditionAction();
        outletAction.setManualAction(true);

        // Act
        boolean actual = outletAction.isManualAction();

        // Assert
        assertTrue(actual);
    }

    @Test
    public void outletEditionAction_validAction_returnActionInstance() {
        // Arrange
        OutletEditionAction outletAction = new OutletEditionAction();
        outletAction.setActionClass("does.not.exist.Plugin");

        // Act
        try {
            outletAction.getAction();
            // Assert
            fail("EditionActionException was supposed to be thrown");
        } catch (EditionActionException ex) {
            // Success
        }
    }

    @Test
    public void outletEditionAction_invalidAction_throwEditionActionException() throws Exception {
        // Arrange
        OutletEditionAction outletAction = new OutletEditionAction();
        outletAction.setActionClass(DummyEditionAction.class.getName());

        // Act
        EditionAction actionInstance = outletAction.getAction();

        // Assert
        assertTrue(actionInstance instanceof DummyEditionAction);
    }

    @Test
    public void outletEditionAction_invalidAction_returnActionNotValid() {
        // Arrange
        OutletEditionAction outletAction = new OutletEditionAction();
        outletAction.setActionClass("does.not.exist.Plugin");

        // Act
        boolean actionValid = outletAction.isActionValid();

        // Assert
        assertFalse(actionValid);
    }

}
