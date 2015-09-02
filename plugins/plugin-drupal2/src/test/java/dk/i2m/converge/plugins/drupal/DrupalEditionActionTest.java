/*
 * Copyright (C) 2015 Raymond Wanyoike
 *
 * This file is part of Converge.
 *
 * Converge is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Converge is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Converge. If not, see <http://www.gnu.org/licenses/>.
 */

package dk.i2m.converge.plugins.drupal;

import org.junit.Test;

import java.util.Date;
import java.util.Map;
import java.util.ResourceBundle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for {@link DrupalEditionAction}.
 */
public class DrupalEditionActionTest {

    @Test
    public void drupalEditionAction_instance_supportEditionExecution() {
        DrupalEditionAction action = new DrupalEditionAction();
        boolean supportsEdition = action.isSupportEditionExecute();

        assertTrue(supportsEdition);
    }

    @Test
    public void drupalEditionAction_instance_supportPlacementExecution() {
        DrupalEditionAction action = new DrupalEditionAction();
        boolean supportsPlacement = action.isSupportPlacementExecute();

        assertTrue(supportsPlacement);
    }

    @Test
    public void drupalEditionAction_instance_metadataAvailable() {
        DrupalEditionAction action = new DrupalEditionAction();

        String name = action.getName();
        String about = action.getAbout();
        String description = action.getDescription();
        String vendor = action.getVendor();
        ResourceBundle bundle = action.getBundle();
        Date date = action.getDate();

        assertNotNull(name);
        assertNotNull(about);
        assertNotNull(bundle);
        assertNotNull(date);
        assertNotNull(description);
        assertNotNull(vendor);
    }

    @Test
    public void drupalEditionAction_instance_propertiesAvailable() {
         DrupalEditionAction action = new DrupalEditionAction();
         Map<String, String> availableProperties = action.getAvailableProperties();

         assertEquals(12, availableProperties.size());
    }

    @Test
    public void drupalEditionAction_instance_propertiesAvailableOnMultipleInvocations() {
        DrupalEditionAction action = new DrupalEditionAction();
        Map<String, String> firstInvocation = action.getAvailableProperties();
        Map<String, String> secondInvocation = action.getAvailableProperties();

        assertEquals(firstInvocation, secondInvocation);
    }
}
