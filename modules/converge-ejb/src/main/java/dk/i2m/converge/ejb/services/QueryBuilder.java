/*
 * Copyright (C) 2009 - 2010 Interactive Media Management
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
package dk.i2m.converge.ejb.services;

import java.util.HashMap;
import java.util.Map;

/**
 * Builder for creating query parameters.<br/>
 * <h1>Usage</h1>
 * <h2>Simple example (single condition)</h2>
 * <p><code>QueryBuilder.with("fieldName", "fieldValue")</code></p>
 *  <h2>Advanced example (multiple conditions)</h2>
 * <p><code>QueryBuilder.with("fieldName", "fieldValue").and("fieldName2",
 * "fieldValue2").and("fieldName3", "fieldValue3")</code></p>
 *
 * @author Allan Lykke Christensen
 */
public class QueryBuilder {

    /** Internal holder of parameters. */
    private Map<String, Object> parameters = null;

    /**
     * Creates a instance of {@link QueryBuilder}. The constructor is only
     * accessible from the static methods
     * {@link QueryBuilder#with(java.lang.String, java.lang.Object)} and
     * {@link QueryBuilder#and(java.lang.String, java.lang.Object)}.
     *
     * @param name
     *          Name of the query parameter
     * @param value
     *          Value of the query parameter
     */
    private QueryBuilder(String name, Object value) {
        this.parameters = new HashMap<String, Object>();
        this.parameters.put(name, value);
    }

    /**
     * Creates a new instance of {@link QueryBuilder} with a single parameter.
     *
     * @param name
     *          Name of the query parameter
     * @param value
     *          Value of the query parameter
     * @return {@link QueryBuilder} with the given parameter set
     */
    public static QueryBuilder with(String name, Object value) {
        return new QueryBuilder(name, value);
    }

    /**
     * Adds a parameter to the query.
     *
     * @param name
     *          Name of the query parameter
     * @param value
     *          Value of the query parameter
     * @return {@link QueryBuilder} with the given parameter added
     */
    public QueryBuilder and(String name, Object value) {
        this.parameters.put(name, value);
        return this;
    }

    /**
     * Parameters contained in the {@link QueryBuilder}.
     *
     * @return {@link Map} of parameters contained in the {@link QueryBuilder}
     */
    public Map<String, Object> parameters() {
        return this.parameters;
    }
}
