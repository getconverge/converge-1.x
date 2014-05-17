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
package dk.i2m.converge.ejb.services;

import dk.i2m.converge.core.DataNotFoundException;
import dk.i2m.converge.core.newswire.NewswireBasket;
import dk.i2m.converge.core.newswire.NewswireItem;
import dk.i2m.converge.core.newswire.NewswireItemAttachment;
import dk.i2m.converge.core.newswire.NewswireService;
import dk.i2m.converge.domain.search.SearchResults;
import java.util.List;
import javax.ejb.Local;

/**
 * Local interface for the newswire stateless session bean.
 *
 * @author Allan Lykke Christensen
 */
@Local
public interface NewswireServiceLocal {

    /**
     * Gets the latest news from the external newswire services.
     *
     * @return {@link List} of {@link NewswireItem}s from the external newswire
     * services.
     */
    List<NewswireItem> getNews();

    /**
     * Gets the newswire items for a particular newswire service.
     *
     * @param newswireServiceId
* Unique identifier of the newswire service
     * @return {@link List} of {@link NewswireItem}s for a given newswire service
     */
    List<NewswireItem> getNews(Long newswireServiceId);

    /**
     * Gets all the {@link NewswireService}s in the database.
     *
     * @return {@link List} of {@link NewswireService}s in the database
     */
    public List<NewswireService> getNewswireServices();

    /**
     * Gets the {@link NewswireService}s in the database with a collection
     * of subscribers and items.
     *
     * @return {@link List} of {@link NewswireService}s in the database with
     * their subscribers and items.
     */
    public List<NewswireService> getNewswireServicesWithSubscribersAndItems();

    /**
     * Gets a {@link NewswireService} from the database.
     *
     * @param id
* ID of the {@link NewswireService}
     * @return {@link NewswireService} matching the given id
     * @throws DataNotFoundException
* If the request {@link NewswireService} doesn't exist
     */
    public NewswireService findById(Long id) throws DataNotFoundException;

    /**
     * Updates an existing {@link NewswireService} in the database.
     *
     * @param newsFeed
* {@link NewswireService} to update in the database
     */
    public void update(NewswireService newsFeed);

    /**
     * Deletes an existing {@link NewswireService} from the database.
     *
     * @param id
* ID of the {@link NewswireService} to delete from the database
     * @throws DataNotFoundException
* If the {@link NewswireService} did not exist
     */
    public void delete(Long id) throws DataNotFoundException;

    /**
     * Creates a new {@link NewswireService} in the database.
     *
     * @param newsFeed
* {@link NewswireService} to create in the database
     * @return Created {@link NewswireService}
     */
    public NewswireService create(NewswireService newsFeed);

    //void downloadNewswireServicesSync();

    void downloadNewswireServices();

    void downloadNewswireService(Long id) throws DataNotFoundException;

    /**
     * Searches the subscribed {@link NewswireService}s.
     * <p/>
     * @param search Search phrase
     * @return {@link List} of matching {@link NewswireItem}s
     */
    SearchResults search(String query, int start, int rows, String sortField,
            boolean sortOrder, String... filterQueries);

    int emptyNewswireService(java.lang.Long newswireServiceId);

    /**
     * Finds {@link NewswireItem}s with a given external identifier.
     * <p/>
     * @param externalId
* External identifier of the {@link NewswireItem}s
     * @return {@link List} of {@link NewswireItem}s with the given external
     * identifier
     */
    java.util.List<dk.i2m.converge.core.newswire.NewswireItem> findByExternalId(
            java.lang.String externalId);

    /**
     * Creates a new {@link NewswireItem} in the database.
     * <p/>
     * @param item
* {@link NewswireItem} to create
     * @return Created {@link NewswireItem}
     */
    NewswireItem create(NewswireItem item);

    /**
     * Gets a {@link java.util.Map} of discovered newswire decoders.
     *
     * @return {@link java.util.Map} of discovered newswire decoders
     */
    java.util.Map<java.lang.String, dk.i2m.converge.core.plugin.NewswireDecoder> getNewswireDecoders();

    /**
     * Finds all active newswire services.
     * <p/>
     * @return {@link List} of active newswire services
     */
    List<NewswireService> findActiveNewswireServices();

    NewswireItem findNewswireItemById(Long id) throws DataNotFoundException;

    List<NewswireService> findAvailableNewswireServices(Long id);

    NewswireBasket createBasket(NewswireBasket basket);

    dk.i2m.converge.core.newswire.NewswireBasket updateBasket(
            dk.i2m.converge.core.newswire.NewswireBasket basket);

    void deleteBasket(dk.i2m.converge.core.newswire.NewswireBasket basket);

    java.util.List<dk.i2m.converge.core.newswire.NewswireBasket> findBasketsByUser(
            java.lang.Long userId);

    void dispatchBaskets();

    NewswireItemAttachment findNewswireItemAttachmentById(Long id) throws
            DataNotFoundException;

    void removeItem(Long id);

    void purgeNewswires();

    void index(dk.i2m.converge.core.newswire.NewswireItem item) throws
            dk.i2m.converge.core.search.SearchEngineIndexingException;

    @javax.ejb.TransactionAttribute(value =
    javax.ejb.TransactionAttributeType.REQUIRES_NEW)
    void stopProcessingNewswireService(java.lang.Long id);

    @javax.ejb.TransactionAttribute(value =
    javax.ejb.TransactionAttributeType.REQUIRES_NEW)
    public void startProcessingNewswireService(java.lang.Long id);

    boolean dispatchBasket(java.lang.Long id);
}
