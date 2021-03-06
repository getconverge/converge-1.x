/*
 * EntityNotFoundException.java
 *
 * Copyright (C) 2009 Interactive Media Management
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
package dk.i2m.converge.ejb.services;

/**
 * {@link Exception} thrown when a requested entity could not be found.
 *
 * @author Allan Lykke Christensen
 */
public class EntityNotFoundException extends Exception {

    /**
     * Creates a new instance of {@link EntityNotFoundException}.
     *
     * @param cause
     *          Cause of the {@link Exception}
     */
    public EntityNotFoundException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new instance of {@link EntityNotFoundException}.
     *
     * @param message
     *          Message to attach to the {@link Exception}
     * @param cause
     *          Cause of the {@link Exception}
     */
    public EntityNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new instance of {@link EntityNotFoundException}.
     *
     * @param message
     *          Message to attach to the {@link Exception}
     */
    public EntityNotFoundException(String message) {
        super(message);
    }

    /**
     * Creates a new instance of {@link EntityNotFoundException}.
     */
    public EntityNotFoundException() {
        super();
    }
}
