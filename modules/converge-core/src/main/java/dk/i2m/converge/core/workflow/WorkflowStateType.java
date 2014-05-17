/*
 * Copyright (C) 2009 - 2012 Interactive Media Management
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

/**
 * Type of a {@link WorkflowState}. This type is not an entity and is not
 * persisted. Instead it is used to determine if a {@link WorkflowState} should
 * be set as the start or end state of a {@link Workflow}.
 *
 * @author Allan Lykke Christensen
 */
public enum WorkflowStateType {

    /** Start of the workflow. */
    START,
    /** Normal workflow state. */
    MIDDLE,
    /** End of the workflow. */
    END,
    /** Trash state of the workflow. Items scheduled for deletion */
    TRASH;
}
