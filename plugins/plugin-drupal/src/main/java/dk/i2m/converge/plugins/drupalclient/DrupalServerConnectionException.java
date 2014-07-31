/*
 * Copyright (C) 2012 Interactive Media Management
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
package dk.i2m.converge.plugins.drupalclient;

/**
 *
 * @author <a href="mailto:allan@i2m.dk">Allan Lykke Christensen</a>
 */
public class DrupalServerConnectionException extends Exception {

    public DrupalServerConnectionException() {
    }

    public DrupalServerConnectionException(String message) {
        super(message);
    }

    public DrupalServerConnectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public DrupalServerConnectionException(Throwable cause) {
        super(cause);
    }
}
