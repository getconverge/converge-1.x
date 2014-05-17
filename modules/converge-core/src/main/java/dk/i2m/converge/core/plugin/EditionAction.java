/*
 *  Copyright (C) 2010 - 2011 Interactive Media Management
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

import dk.i2m.converge.core.content.NewsItem;
import dk.i2m.converge.core.content.NewsItemPlacement;
import dk.i2m.converge.core.workflow.Edition;
import dk.i2m.converge.core.workflow.OutletEditionAction;
import java.util.Map;

/**
 * Interface for implementing a {@link EditionAction}.
 *
 * @author Allan Lykke Christensen
 */
public interface EditionAction extends Plugin {

    /**
     * Executes the {@link EditionAction}.
     *
     * @param ctx     Context for which the plug-in is being executed
     * @param edition {@link Edition} being processed
     * @param action  {@link OutletEditionAction} to be executed
     */
    public abstract void execute(PluginContext ctx, Edition edition,
            OutletEditionAction action);

    /**
     * Executes the {@link EditionAction} on a single {@link NewsItemPlacement}.
     *
     * @param ctx       Context for which the plug-in is being executed
     * @param placement {@link NewsItemPlacement} being processed
     * @param edition   {@link Edition} of the {@link NewsItem} being processed
     * @param action    {@link OutletEditionAction} to be executed
     */
    public abstract void executePlacement(PluginContext ctx,
            NewsItemPlacement placement,
            Edition edition, OutletEditionAction action);

    /**
     * Determines if the {@link EditionAction} supports execution on
     * {@link Edition}s. If this method returns {@code false},
     * {@link EditionAction#execute(dk.i2m.converge.core.plugin.PluginContext,
     * dk.i2m.converge.core.workflow.Edition,
     * dk.i2m.converge.core.workflow.OutletEditionAction) } should return
     * {@link UnsupportedOperationException}.
     *
     * @return {@code true} if the {@link EditionAction} supports execution on
     *         {@link Edition}s, otherwise {@code false}
     */
    public boolean isSupportEditionExecute();

    /**
     * Determines if the {@link EditionAction} supports execution on
     * {@link NewsItemPlacement}s. If this method returns {@code false},
     * {@link EditionAction#executePlacement(dk.i2m.converge.core.plugin.PluginContext,
     * dk.i2m.converge.core.content.NewsItem,
     * dk.i2m.converge.core.workflow.Edition,
     * dk.i2m.converge.core.workflow.OutletEditionAction) } should return
     * {@link UnsupportedOperationException}.
     *
     * @return {@code true} if the {@link EditionAction} supports execution on
     *         {@link Edition}s, otherwise {@code false}
     */
    public boolean isSupportPlacementExecute();

    /**
     * Provides a map of possible properties for the action.
     *
     * @return Map of possible action properties
     */
    public abstract Map<String, String> getAvailableProperties();
}
