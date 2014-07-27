/*
 *  Copyright (C) 2010 Interactive Media Manager
 *  Copyright (C) 2014 Allan Lykke Christensen
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package dk.i2m.converge.core.plugin;

import org.junit.Test;
import static org.junit.Assert.*;
import org.scannotation.ClasspathUrlFinder;

/**
 * Unit tests for {@link PluginManager}.
 *
 * @author Allan Lykke Christensen
 */
public class PluginManagerTest {

    @Test
    public void pluginManager_discoverPlugins_returnCorrectNumberOfDiscoveredPlugins() throws Exception {
        PluginManager pm = PluginManager.getInstance();
        assertEquals(6, pm.discover(ClasspathUrlFinder.findClassBase(getClass())));
    }

    @Test
    public void pluginManager_discoverNewswireDecoders_returnCorrectNumberOfDiscoveredNewswireDecoders() {
        PluginManager pm = PluginManager.getInstance();
        pm.discover(ClasspathUrlFinder.findClassBase(getClass()));
        assertEquals("Incorrect number of newswire decoders discovered", 1, pm.getNewswireDecoders().size());
    }

    @Test
    public void pluginManager_discoverOutletActions_returnCorrectNumberOfEditionActions() {
        PluginManager pm = PluginManager.getInstance();
        pm.discover(ClasspathUrlFinder.findClassBase(getClass()));
        assertEquals("Incorrect number of outlet actions discovered", 1, pm.getOutletActions().size());
    }

    @Test
    public void pluginManager_discoverWorkflowActions_returnCorrectNumberOfWorkflowActions() {
        PluginManager pm = PluginManager.getInstance();
        pm.discover(ClasspathUrlFinder.findClassBase(getClass()));
        assertEquals("Incorrect number of workflow actions discovered", 1, pm.getWorkflowActions().size());
    }

    @Test
    public void pluginManager_discoverWorkflowValidators_returnCorrectNumberOfWorkflowValidators() {
        PluginManager pm = PluginManager.getInstance();
        pm.discover(ClasspathUrlFinder.findClassBase(getClass()));
        assertEquals("Incorrect number of workflow validators discovered", 1, pm.getWorkflowValidators().size());
    }

    @Test
    public void pluginManager_discoverCatalogueActions_returnCorrectNumberOfCatalogueActions() {
        PluginManager pm = PluginManager.getInstance();
        pm.discover(ClasspathUrlFinder.findClassBase(getClass()));
        assertEquals("Incorrect number of catalogue actions discovered", 1, pm.getCatalogueActions().size());
    }

    @Test
    public void pluginManager_discoverNewsItemActions_returnCorrectNumberOfNewsItemActions() {
        PluginManager pm = PluginManager.getInstance();
        pm.discover(ClasspathUrlFinder.findClassBase(getClass()));
        assertEquals("Incorrect number of news item actions discovered", 1, pm.getNewsItemActions().size());
    }
}
