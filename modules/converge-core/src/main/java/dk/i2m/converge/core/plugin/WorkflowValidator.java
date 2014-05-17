/*
 * Copyright (C) 2011 Interactive Media Management
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package dk.i2m.converge.core.plugin;

import dk.i2m.converge.core.content.NewsItem;
import dk.i2m.converge.core.workflow.WorkflowStep;
import dk.i2m.converge.core.workflow.WorkflowStepValidator;
import java.util.Map;

/**
 * Interface for implementing a {@link Workflow} validator. Workflow validators can be added to {@link WorkflowStep}s for validation of inputted information.
 *
 * @author Allan Lykke Christensen
 */
public interface WorkflowValidator extends Plugin {

    /**
     * Executes the validation.
     *
     * @param ctx Context for which the workflow is being executed
     * @param item
     *          {@link NewsItem} being processed
     * @param stepAction
     *          {@link WorkflowStepAction} to be executed
     */
    abstract void execute(NewsItem item, WorkflowStep step, WorkflowStepValidator validator) throws WorkflowValidatorException;

    /**
     * Provides a map of possible properties for the validator.
     *
     * @return Map of possible validator properties
     */
    abstract Map<String, String> getAvailableProperties();
}
