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
package dk.i2m.converge.jsf.functions;

import java.util.ArrayList;
import java.util.List;

/**
 * Static class providing JSF EL collection utility functions.
 *
 * @author Allan Lykke Christensen
 */
public final class CollectionFunctions {

    /**
     * Create a {@link List} of {@link String}s from an array of
     * {@link String}s.
     *
     * @param strings
     *          Array of {@link String}s
     * @return {@link List} of {@link String}s
     */
    public static List<String> createList(String strings) {
        String[] separated = strings.split(",");
        List<String> collection = new ArrayList<String>();
        for (String string : separated) {
            collection.add(string.trim());
        }
        return collection;
    }

    /**
     * Determines if a given object is in a {@link List}.
     *
     * @param obj
     *          {@link Object} to determine if is in {@link List}
     * @return {@code true} if the {@link Object} is in the {@link List}, otherwise {@code false}
     */
    public static boolean contains(List list, Object obj) {
        return list.contains(obj);
    }
}
