/*
 *  Copyright (C) 2012 Interactive Media Management
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
package dk.i2m.converge.plugins.decoders.newsml12;

/**
 * Exception thrown if a NewsML could not be unmarshalled.
 *
 * @author Allan Lykke Christensen
 */
public class NewsMLUnmarshalException extends Exception {

    /**
     * Creates a new instance of {@link NewsMLUnmarshalException}.
     *
     * @param cause Cause of the {@link Exception}
     */
    public NewsMLUnmarshalException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new instance of {@link NewsMLUnmarshalException}.
     *
     * @param message Message of the {@link Exception}
     * @param cause Cause of the {@link Exception}
     */
    public NewsMLUnmarshalException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new instance of {@link NewsMLUnmarshalException}.
     *
     * @param message Message of the {@link Exception}
     */
    public NewsMLUnmarshalException(String message) {
        super(message);
    }

    /**
     * Creates a new instance of {@link NewsMLUnmarshalException}.
     */
    public NewsMLUnmarshalException() {
        super();
    }
}
