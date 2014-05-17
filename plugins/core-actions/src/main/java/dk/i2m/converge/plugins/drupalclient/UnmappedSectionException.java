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
 * {@link Exception} thrown if a section for a given {@link NewsItemPlacement}
 * was not mapped.
 *
 * @author <a href="mailto:allan@i2m.dk">Allan Lykke Christensen</a>
 */
public class UnmappedSectionException extends Exception {

    public UnmappedSectionException() {
        super();
    }

    public UnmappedSectionException(String message) {
        super(message);
    }

    public UnmappedSectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnmappedSectionException(Throwable cause) {
        super(cause);
    }
}
