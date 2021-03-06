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
package dk.i2m.converge.plugins.drupalclient;

import java.util.Date;
import java.util.Map;
import java.util.ResourceBundle;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for {@link DrupalEditionAction}.
 *
 * @author Allan Lykke Christensen
 */
public class DrupalEditionActionTest {

    @Test
    public void drupalEditionAction_instance_supportsEditionExecution() {
        // Arrange
        DrupalEditionAction action = new DrupalEditionAction();

        // Act
        boolean supportsEdition = action.isSupportEditionExecute();

        // Assert
        assertTrue(supportsEdition);
    }

    @Test
    public void drupalEditionAction_instance_supportsPlacementExecution() {
        // Arrange
        DrupalEditionAction action = new DrupalEditionAction();

        // Act
        boolean supportsPlacement = action.isSupportPlacementExecute();

        // Assert
        assertTrue(supportsPlacement);
    }

    @Test
    public void drupalEditionAction_instance_metadataAvailable() {
        // Arrange
        DrupalEditionAction plugin = new DrupalEditionAction();

        // Act
        String name = plugin.getName();
        String about = plugin.getAbout();
        ResourceBundle bundle = plugin.getBundle();
        Date date = plugin.getDate();
        String description = plugin.getDescription();
        String vendor = plugin.getVendor();

        // Assert
        assertNotNull(name);
        assertNotNull(about);
        assertNotNull(bundle);
        assertNotNull(date);
        assertNotNull(description);
        assertNotNull(vendor);
    }

    @Test
    public void drupalEditionAction_instance_propertiesAvailable() {
        // Arrange
        DrupalEditionAction plugin = new DrupalEditionAction();

        // Act
        Map<String, String> availableProperties = plugin.getAvailableProperties();

        // Assert
        assertEquals(16, availableProperties.size());
    }

    @Test
    public void drupalEditionAction_instance_samePropertiesAvailableOnMultipleInvocations() {
        // Arrange
        DrupalEditionAction plugin = new DrupalEditionAction();

        // Act
        Map<String, String> firstInvocation = plugin.getAvailableProperties();
        Map<String, String> secondInvocation = plugin.getAvailableProperties();

        // Assert
        assertEquals(firstInvocation, secondInvocation);
    }
}
