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
package dk.i2m.converge.core.security;

/**
 * Enumeration containing privileges that can be protected with a
 * {@link UserRole}.
 *
 * @author Allan Lykke Christensen
 */
public enum SystemPrivilege {

    REPORTING,
    /** Update the calendar. */
    CALENDAR,
    /** Read/write access to the contacts database. */
    CONTACTS_MANAGE,
    /** Read-access to the contacts database. */
    CONTACTS_READ,
    /** Manage listings. */
    LISTINGS,
    /** Manage concepts. */
    MANAGE_CONCEPTS,
    /** Access to create news items and submit them . */
    MY_NEWS_ITEMS,
    /** Access to upload own photos into the database. */
    MY_PHOTOS,
    /** Adding new concepts. */
    NEW_CONCEPTS,
    /** Access to planning the content of an outlet. */
    OUTLET_PLANNING,
    /** Super user access. */
    SUPER_USER,
    /** News reader access. */
    NEWS_READER,
    /** Assign newswire items to users, i.e. copy stories from newswire to inbox. */
    NEWSWIRE_ITEM_ASSIGN,
    /** Remove newswire item from search engine. */
    NEWSWIRE_ITEM_REMOVE,
    /** Remove news item from search engine. */
    SEARCH_ENGINE_RESULT_REMOVE,
    /** Allow management of wiki pages. */
    MANAGE_WIKI_PAGE,
}
