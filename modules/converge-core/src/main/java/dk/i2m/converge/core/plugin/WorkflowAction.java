/*
 * Copyright (C) 2010 Interactive Media Management
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package dk.i2m.converge.core.plugin;

import dk.i2m.converge.core.content.NewsItem;
import dk.i2m.converge.core.security.UserAccount;
import dk.i2m.converge.core.workflow.WorkflowStepAction;
import java.util.Map;

/**
 * Interface for implementing a {@link WorkflowAction}.
 *
 * @author Allan Lykke Christensen
 */
public interface WorkflowAction extends Plugin {

    /**
     * Executes the {@link WorkflowAction}.
     *
     * @param ctx Context for which the workflow is being executed
     * @param item
     *          {@link NewsItem} being processed
     * @param stepAction
     *          {@link WorkflowStepAction} to be executed
     * @param user User that selected the step
     */
    public abstract void execute(PluginContext ctx, NewsItem item, WorkflowStepAction stepAction, UserAccount user);

    /**
     * Provides a map of possible properties for the action.
     *
     * @return Map of possible action properties
     */
    public abstract Map<String, String> getAvailableProperties();
}
