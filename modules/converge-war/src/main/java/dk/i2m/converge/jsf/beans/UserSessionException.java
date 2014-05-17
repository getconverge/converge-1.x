/*
 *  Copyright (C) 2010 Interactive Media Management
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
package dk.i2m.converge.jsf.beans;

/**
 * {@link Exception} thrown when a problem occurred with a user session.
 *
 * @author Allan Lykke Christensen
 */
public class UserSessionException extends Exception {

    /**
     * Creates a new {@link UserSessionException}.
     *
     * @param cause
     *          Cause of the {@link Exception}
     */
    public UserSessionException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new {@link UserSessionException}.
     *
     * @param message
     *          Message to attach
     * @param cause
     *          Cause of the {@link Exception}
     */
    public UserSessionException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new {@link UserSessionException}.
     *
     * @param message
     *          Message to attach
     */
    public UserSessionException(String message) {
        super(message);
    }

    /**
     * Creates a new {@link UserSessionException}.
     */
    public UserSessionException() {
        super();
    }
}
