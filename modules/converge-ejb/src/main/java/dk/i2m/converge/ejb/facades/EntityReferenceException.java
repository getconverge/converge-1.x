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
package dk.i2m.converge.ejb.facades;

/**
 * {@link Exception} thrown when an entity is attempted to be deleted while
 * there is still a reference to it from another entity.
 *
 * @author Allan Lykke Christensen
 */
public class EntityReferenceException extends Exception {

    public EntityReferenceException(Throwable cause) {
        super(cause);
    }

    public EntityReferenceException(String message, Throwable cause) {
        super(message, cause);
    }

    public EntityReferenceException(String message) {
        super(message);
    }

    public EntityReferenceException() {
        super();
    }
}
