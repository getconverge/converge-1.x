/*
 *  Copyright (C) 2010 Interactive Media Manager
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

import dk.i2m.converge.core.plugin.PluginManager;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test cases for the {@link PluginManager}.
 *
 * @author Allan Lykke Christensen
 */
public class PluginManagerTest {

    @Test
    @Ignore(value="Seems that there is a classpath issues preventing the plugins to be discovered during tests but not in production")
    public void testDiscoveryPlugins() throws Exception {
        PluginManager pm = PluginManager.getInstance();
        assertEquals("Incorrect number of plugins discovered", 4, pm.getPlugins().size());
    }

    @Test
    @Ignore
    public void testDiscoveryNewswireDecoders() throws Exception {
        PluginManager pm = PluginManager.getInstance();
        assertEquals("Incorrect number of newswire decoders discovered", 2, pm.getNewswireDecoders().size());
        if (!pm.getNewswireDecoders().containsValue("dk.i2m.converge.plugins.newswires.DailyMailDecoder")) {
            fail("Daily Mail Decoder not found by plug-in manager");
        }
        if (!pm.getNewswireDecoders().containsValue("dk.i2m.converge.plugins.newswires.RssDecoder")) {
            fail("RSS Decodernot found by plug-in manager");
        }
    }

    @Test
    @Ignore
    public void testDiscoveryWorkflowActions() throws Exception {
        PluginManager pm = PluginManager.getInstance();
        assertEquals("Incorrect number of workflow actions discovered", 1, pm.getWorkflowActions().size());
        if (!pm.getWorkflowActions().containsValue("dk.i2m.converge.plugins.workflow.AlertAction")) {
            fail("Alert Action was not discovered by plug-in manager");
        }
    }
}
