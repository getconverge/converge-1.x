/*
 * Copyright (C) 2010 Interactive Media Management
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
package dk.i2m.converge.core;

/**
 * {@link Exception} to be thrown when an entity has been requested through the
 * Data Access Object pattern and the entity could not be located in the data
 * source.
 *
 * @author Allan Lykke Christensen
 */
public class DataNotFoundException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new instance of {@link DataNotFoundException}.
     *
     * @param cause
     *          Cause of the {@link Exception}
     */
    public DataNotFoundException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new instance of {@link DataNotFoundException}.
     *
     * @param message
     *          Message to attach to the {@link Exception}
     * @param cause
     *          Cause of the {@link Exception}
     */
    public DataNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new instance of {@link DataNotFoundException}.
     *
     * @param message
     *          Message to attach to the {@link Exception}
     */
    public DataNotFoundException(String message) {
        super(message);
    }

    /**
     * Creates a new instance of {@link DataNotFoundException}.
     */
    public DataNotFoundException() {
        super();
    }
}
