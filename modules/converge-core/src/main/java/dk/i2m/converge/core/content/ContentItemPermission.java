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
package dk.i2m.converge.core.content;

/**
 * User permission indicator for a given content item.
 *
 * @author Allan Lykke Christensen
 */
public enum ContentItemPermission {

    /** The user is among the current actors of the item. */
    USER,
    /** The user has the role of the current actor of the item. */
    ROLE,
    /** The user is among the actors of the item, but is not the current actor. */
    ACTOR,
    /** The user is not authorised to read or write the item. */
    UNAUTHORIZED
}
