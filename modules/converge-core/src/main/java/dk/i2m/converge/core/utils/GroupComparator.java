/*
 * Copyright 2010 Interactive Media Management
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
package dk.i2m.converge.core.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * {@link Comparator} for comparing objects using multiple {@link Comparator}s.
 *
 * @author Allan Lykke Christensen
 */
public class GroupComparator implements Comparator {

    private List<Comparator> comparators = new ArrayList<Comparator>();

    /**
     * Creates a new instance of {@link GroupComparator}.
     * 
     * @param comparators
     *          {@link Comparator}s to include in the group comparison
     */
    public GroupComparator(Comparator... comparators) {
        this.comparators.addAll(Arrays.asList(comparators));
    }

    /**
     * Adds a {@link Comparator} to the {@link List} of {@link Comparator}s
     * to use.
     * 
     * @param comparator
     *          {@link Comparator} to add
     */
    public void addComparator(Comparator comparator) {
        comparators.add(comparator);
    }

    
    /**
     * Compares the two {@link Object}s using the registered 
     * {@link Comparator}s in the order they were added.
     * 
     * @param object1
     *          First object
     * @param object2
     *          Second object
     * @return 0 if {@code object1} and {@link object2} are equal, less than 0 
     *         if {@code object1} comes before {@code object2}, greater than 0 
     *         if {@code object2} comes before {@code object1}
     */
    @Override
    @SuppressWarnings("unchecked")
    public int compare(Object object1, Object object2) {
        for (Comparator comparator : comparators) {
            int returnValue = comparator.compare(object1, object2);

            if (returnValue != 0) {
                return returnValue;
            }
        }

        return 0;
    }
}
