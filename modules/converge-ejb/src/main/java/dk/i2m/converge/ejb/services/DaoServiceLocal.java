/*
 *  Copyright (C) 2010 Allan Lykke Christensen
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
package dk.i2m.converge.ejb.services;

import dk.i2m.converge.core.DataNotFoundException;
import java.util.List;
import java.util.Map;
import javax.ejb.Local;
import javax.persistence.OptimisticLockException;

/**
 * Local interface for the Data Access Object Service.
 *
 * @author Allan Lykke Christensen
 */
@Local
public interface DaoServiceLocal {

    /**
     * Stores a given object in the data store.
     *
     * @param <T>
     * Type of entity to store
     * @param t
     * Entity to store
     * @return Object stored in the data store.
     */
     <T> T create(T t);

    /**
     * Remove a given object from the data store.
     *
     * @param type
     * Type of object
     * @param id
     * Unique identifier of the object
     */
    void delete(Class type, Object id);

    /**
     * Finds all the entities of a given type.
     *
     * @param <T>
     * Type of entity
     * @param type
     * Type of entity
     * @return {@link List} of all entities of the given type
     */
     <T> List<T> findAll(Class<T> type);
     
     <T> List<T> findAll(Class<T> type, String orderBy, boolean asc);

     <T> List<T> findAll(Class<T> type, int start, int resultLimit);
     
     <T> List<T> findAll(Class<T> type, int start, int resultLimit, String orderBy, boolean asc);

     <T> Number count(Class<T> type, String field);

    /**
     * Finds a given entity in the data store.
     *
     * @param <T>
     * Type of entity
     * @param type
     * Type of entity
     * @param id
     * Unique identifier of the entity
     * @return Entity matching the unique identifier
     * @throws DataNotFoundException
     * If no match could be found
     */
     <T> T findById(Class<T> type, Object id) throws DataNotFoundException;

    /**
     * Finds a {@link List} of entities returned from the given native SQL
     * query.
     *
     * @param sql
     * Native SQL query
     * @param type
     * Type of entity
     * @return {@link List} of entities returned from the given native SQL query
     */
    List findByNativeQuery(String sql, Class type);

    /**
     * Finds a {@link List} of entity returned by the given named query.
     *
     * @param namedQueryName
     * Name of the query
     * @return {@link List} of entities returned by the given query
     */
    List findWithNamedQuery(String namedQueryName);

    /**
     * Finds a {@link List} of entity returned by the given named query.
     *
     * @param namedQueryName
     * Name of the query
     * @param parameters
     * Parameters of the query
     * @return {@link List} of entities returned by the given query
     */
    List findWithNamedQuery(String namedQueryName, Map<String, Object> parameters);

    /**
     * Finds a {@link List} of entity returned by the given named query.
     *
     * @param <T>
     *          Type of object to retrieve
     * @param type
     *          Type of object to retrieve
     * @param namedQueryName
     *          Name of the query
     * @param parameters
     *          Parameters of the query
     * @return Matched entity
     * @throws DataNotFoundException
     *          If an entity could not be found
     */
     <T> T findObjectWithNamedQuery(Class<T> type, String namedQueryName, Map<String, Object> parameters) throws DataNotFoundException;
     
     /**
     * Finds a {@link List} of entity returned by the given named query.
     *
     * @param <T>
     *          Type of object to retrieve
     * @param type
     *          Type of object to retrieve
     * @param namedQueryName
     *          Name of the query
     * @param queryBuilder
     *          Builder containing the query parameters
     * @return Matched entity
     * @throws DataNotFoundException
     *          If an entity could not be found
     */
     <T> T findObjectWithNamedQuery(Class<T> type, String namedQueryName, QueryBuilder queryBuilder) throws DataNotFoundException;

    /**
     * Finds a {@link List} of entity returned by the given named query.
     *
     * @param queryName
     *          Name of the query
     * @param resultLimit
     *          Maximum number of results
     * @return {@link List} of entities returned by the given query
     */
    List findWithNamedQuery(String queryName, int resultLimit);

    /**
     * Finds a {@link List} of entity returned by the given named query.
     *
     * @param namedQueryName
     *          Name of the query
     * @param parameters
     *          Parameters of the query
     * @param resultLimit
     *          Maximum number of results
     * @return {@link List} of entities returned by the given query
     */
    List findWithNamedQuery(String namedQueryName, Map<String, Object> parameters, int resultLimit);

    List findWithNamedQuery(String namedQueryName, Map<String, Object> parameters, int start, int resultLimit);

    /**
     * Updates an existing entity in the database.
     *
     * @param <T>
     *       Type of entity
     * @param t
     *       Entity to update
     * @return Updated entity
     * @throws OptimisticLockException
     *          If <code>t</code> is outdated, and updated prior
     *          to the call of this method
     */
     <T> T update(T t) throws OptimisticLockException;

    /**
     * Executes a query on the database and returns the number of records affected.
     *
     * @param namedQueryName
     *          Name of the Named Query
     * @param qb
     *          QueryBuilder containing the parameters
     * @return Number of affected records
     */
    int executeQuery(String namedQueryName, QueryBuilder qb);

    /**
     * Executes a query on the database and returns the number of records affected.
     *
     * @param namedQueryName
     *          Name of the Named Query
     * @return Number of affected records
     */
    int executeQuery(String namedQueryName);

    void commit();
}
