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

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.beanutils.PropertyUtils;

/**
 * General purpose {@link Comparator} for Java Beans.
 *
 * @author Allan Lykke Christensen
 */
public class BeanComparator implements Comparator<Object>, Serializable {
    
    private static final Logger LOG = Logger.getLogger(BeanComparator.class.getName());

    /** Field to sort by. */
    private String sortField = "";

    /** Direction to sort. */
    private boolean sortAscending = true;

    /**
     * Creates a new instance of {@link BeanComparator}.
     *
     * @param propertyName
     *            Field to sort by
     */
    public BeanComparator(final String propertyName) {
        this(propertyName, true);
    }

    /**
     * Creates a new instance of {@link BeanComparator}.
     *
     * @param propertyName
     *            Field to sort by
     * @param sortOrder
     *            Should the comparison be done in ascending order
     */
    public BeanComparator(final String propertyName, final boolean sortOrder) {
        this.sortField = propertyName;
        this.sortAscending = sortOrder;
    }

    /**
     * Compares the two JavaBeans and returns a negative number if
     * <code>o1</code> should come before <code>o2</code>, and a positive
     * number if <code>o2</code> should come before <code>o1</code>. If
     * <code>o1</code> and <code>o2</code> are equal zero (0) will be
     * returned.
     *
     * @param o1
     *            First JavaBean
     * @param o2
     *            Second JavaBean
     * @return a negative number if <code>o1</code> should come before
     *          <code>o2</code>, and a positive number if <code>o2</code>
     *          should come before <code>o1</code>. If <code>o1</code> and
     *          <code>o2</code> are equal zero (0) will be returned.
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public final int compare(final Object o1, final Object o2) {

        // Result holder
        int result = 0;

        // Holder for the value fetched from the first JavaBean
        Object field1 = null;

        // Holder for the value fetched from the second JavaBean
        Object field2 = null;

        try {
            field1 = PropertyUtils.getNestedProperty(o1, this.sortField);
            field2 = PropertyUtils.getNestedProperty(o2, this.sortField);
        } catch (NoSuchMethodException e) {
            LOG.log(Level.SEVERE, "{0} does not exist.", this.sortField);
        } catch (IllegalAccessException e) {
            LOG.log(Level.SEVERE, "Access restricted to {0}", this.sortField);
        } catch (InvocationTargetException e) {
            LOG.log(Level.SEVERE, "{0} could not be invoked.", this.sortField);
        }


        if (field1 == null && field2 == null) {
            // If both fields are null, then they are both equal
            result = 0;
        } else if (field1 == null) {
            // If only field1 is null, then it should come before the second
            result = -1;
        } else if (field2 == null) {
            // If only field2 is null, then is should come before the first
            result = 1;
        } else {
            // Use the compareTo method of the JavaBean to determine which
            // should come first
            try {
                result = (Integer) MethodUtils.invokeMethod(
                        field1, "compareTo", field2);
            } catch (NoSuchMethodException e) {
                LOG.log(Level.SEVERE, "Bean ({0}) does not contain a compareTo method.", field1.getClass().getName());
            } catch (IllegalAccessException e) {
                LOG.log(Level.SEVERE, "Access to compareTo is restricted.");
            } catch (InvocationTargetException e) {
                LOG.log(Level.SEVERE, "compareTo could not be invoked.");
            }
        }

        if (!sortAscending) {
            result = -result;
        }

        return result;
    }
}
