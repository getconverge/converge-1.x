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
package dk.i2m.converge.plugins.joomla;

/**
 * Exception thrown by the {@link JoomlaAction} plug-in. The exception is
 * thrown if an exception occurs while interacting with the Converge API on
 * the Joomla installation.
 *
 * @author Allan Lykke Christensen
 */
public class JoomlaActionException extends Exception {

    public JoomlaActionException(Throwable cause) {
        super(cause);
    }

    public JoomlaActionException(String message, Throwable cause) {
        super(message, cause);
    }

    public JoomlaActionException(String message) {
        super(message);
    }

    public JoomlaActionException() {
        super();
    }
}
