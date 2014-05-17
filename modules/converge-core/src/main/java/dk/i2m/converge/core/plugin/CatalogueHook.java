/*
 * Copyright (C) 2011 - 2012 Interactive Media Management
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later 
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT 
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more 
 * details.
 *
 * You should have received a copy of the GNU General Public License along with 
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package dk.i2m.converge.core.plugin;

import dk.i2m.converge.core.content.catalogue.CatalogueHookInstance;
import java.util.Map;

/**
 * Interface for implementing a hook into the event lifecycle of a
 * {@link Catalogue}.
 *
 * @author Allan Lykke Christensen
 */
public abstract class CatalogueHook implements Plugin {

    /**
     * Executes the hook.
     *
     * @param ctx      Context in which the hook is being executed
     * @param event    Event that occurred
     * @param instance Instance being executed
     * @throws CatalogueEventException If the {@link CatalogueHook} failed executing
     */
    public abstract void execute(PluginContext ctx, CatalogueEvent event,
            CatalogueHookInstance instance) throws CatalogueEventException;

    /**
     * Provides a map of possible properties for the action.
     *
     * @return Map of possible action properties
     */
    public abstract Map<String, String> getAvailableProperties();

    /**
     * Determines if the {@link CatalogueHook} support batch processing
     * in that it can be executed for all items in a Catalogue.
     * <p/>
     * @return {@code true} if the hook can be executed for all items
     * in a catalogue, otherwise {@code false}
     */
    public abstract boolean isSupportBatch();
}
