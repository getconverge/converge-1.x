/*
 * Copyright (C) 2011 Interactive Media Management
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
package dk.i2m.converge.ejb.facades;

import dk.i2m.converge.core.security.UserRole;

/**
 * Exception thrown when the actor is missing from a story.
 *
 * @author Allan Lykke Christensen
 */
public class MissingActorException extends WorkflowStateTransitionException {

    private final UserRole role;

    /**
     * Creates a new instance of {@link MissingActorException}.
     *
     * @param message Message to include in the exception
     * @param role The {@link UserRole} that was missing
     */
    public MissingActorException(String message, UserRole role) {
        super(message);
        this.role = role;
    }

    /**
     * Gets the {@link UserRole} that was missing.
     *
     * @return Missing {@link UserRole}
     */
    public UserRole getRole() {
        return role;
    }
}
