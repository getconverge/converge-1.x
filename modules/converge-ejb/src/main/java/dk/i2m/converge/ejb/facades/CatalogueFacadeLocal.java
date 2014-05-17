/*
 * Copyright (C) 2010 - 2012 Interactive Media Management
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
package dk.i2m.converge.ejb.facades;

import dk.i2m.converge.core.DataNotFoundException;
import dk.i2m.converge.core.content.catalogue.*;
import dk.i2m.converge.core.newswire.NewswireItem;
import dk.i2m.converge.core.security.UserAccount;
import dk.i2m.converge.ejb.services.InvalidMediaRepositoryException;
import dk.i2m.converge.ejb.services.MediaRepositoryIndexingException;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.ejb.Local;

/**
 * Local interface for the {@link CatalogueFacadeBean}.
 *
 * @author Allan Lykke Christensen
 */
@Local
public interface CatalogueFacadeLocal {

    /**
     * Creates a new {@link Catalogue} in the database.
     *
     * @param catalogue
     *          {@link Catalogue} to create
     * @return {@link Catalogue} created with auto-generated properties set
     */
    Catalogue create(Catalogue catalogue);

    /**
     * Updates an existing {@link Catalogue} in the database.
     *
     * @param catalogue 
     *          {@link Catalogue} to update
     * @return Updated {@link Catalogue}
     */
    Catalogue update(Catalogue catalogue);

    /**
     * Deletes an existing {@link Catalogue} from the database.
     *
     * @param id 
     *          Unique identifier of the {@link Catalogue}
     * @throws DataNotFoundException 
     *          If the given {@link Catalogue} does not exist
     */
    void deleteCatalogueById(Long id) throws DataNotFoundException;

    /**
     * Finds all {@link Catalogue}s in the database.
     *
     * @return {@link List} of all {@link Catalogue}s in the database
     */
    List<Catalogue> findAllCatalogues();

    Catalogue findCatalogueById(Long id) throws DataNotFoundException;

    void indexCatalogues() throws InvalidMediaRepositoryException,
            MediaRepositoryIndexingException;

    void scanDropPoints();

    List<Catalogue> findWritableCatalogues();

    List<Rendition> findRenditions();

    Rendition findRenditionById(Long id) throws DataNotFoundException;

    Rendition findRenditionByName(String name) throws DataNotFoundException;

    Rendition create(Rendition rendition);

    Rendition update(Rendition rendition);

    /**
     * Deletes a {@link Rendition} by its unique identifier.
     *
     * @param id
     *          Unique identifier of the {@link Rendition}
     */
    void deleteRendition(Long id);

    /**
     * Creates a {@link MediaItem} in the database.
     *
     * @param mediaItem 
     *          {@link MediaItem} to create
     * @return Created {@link MediaItem}
     */
    MediaItem create(MediaItem mediaItem);

    MediaItem create(NewswireItem newswireItem, Catalogue catalogue);

    MediaItem update(MediaItem mediaItem);

    void deleteMediaItemById(Long id);

    MediaItem findMediaItemById(Long id) throws DataNotFoundException;

    List<MediaItem> findMediaItemsByStatus(MediaItemStatus status);

    List<MediaItem> findMediaItemsByOwner(UserAccount owner);

    List<MediaItem> findCurrentMediaItems(UserAccount user, Long catalogueId);

    /**
     * Finds the {@link MediaItem}s for a given {@code UserAccount}, with a
     * given {@link MediaItemStatus} in a given {@link Catalogue}.
     *
     * @param user
     *          {@link UserAccount} owning the {@link MediaItem}s
     * @param mediaItemStatus
     *          Status of the {@link MediaItem}s
     * @param catalogueId
     *          {@link Catalogue} containing the {@link MediaItem}s
     * @return {@link List} of {@link MediaItem}s owned by the given 
     *         {@link UserAccount} with the given {@link MediaItemStatus}, in 
     *         the given {@link Catalogue}
     */
    List<MediaItem> findCurrentMediaItems(UserAccount user,
            MediaItemStatus mediaItemStatus, Long catalogueId);

    /**
     * Determines if the given {@link MediaItem} is referenced by a
     * {@link NewsItem}.
     *
     * @param id 
     *          Unique identifier of the {@link MediaItem}
     * @return {@code true} if the {@link MediaItem} is referenced, otherwise
     *         {@code false}
     */
    boolean isMediaItemUsed(Long id);

    /**
     * Gets a {@link List} of all placements for a given {@link MediaItem}.
     *
     * @param id 
     *          Unique identifier of the {@link MediaItem}
     * @return {@link List} of placements for the given {@link MediaItem}
     * @throws DataNotFoundException
     *          If the given {@link MediaItem} does not exist
     */
    List<MediaItemUsage> getMediaItemUsage(Long id) throws DataNotFoundException;

    String archive(File file, Catalogue catalogue, String fileName) throws IOException;

    void deleteMediaItemRenditionById(java.lang.Long id);

    /**
     * Creates a new {@link MediaItemRendition} based on a {@link File} and
     * {@link MediaItem}.
     *
     * @param file
     *          File representing the {@link MediaItemRendition}
     * @param item
     *          {@link MediaItem} to add the {@link MediaItemRendition}
     * @param rendition
     *          {@link Rendition} of the {@link MediaItemRendition}
     * @param filename
     *          Name of the file
     * @param contentType
     *          Content type of the file
     * @param executeHooks
     *          Should hooks be executed upon creation
     * @return Created {@link MediaItemRendition}
     * @throws IOException
     *          If the {@link MediaItemRendition} could not be stored in the 
     *          {@link Catalogue}
     */
    MediaItemRendition create(File file, MediaItem item, Rendition rendition,
            String filename, String contentType, boolean executeHooks) throws
            IOException;

    /**
     * Updates an existing {@link MediaItemRendition} based on a replacement
     * {@link File} and {@link MediaItemRendition}.
     *
     * @param file
     *          {@link File} to replace the existing file of the 
     *          {@link MediaItemRendition}
     * @param filename
     *          Name of the file
     * @param contentType
     *          Content type of the file
     * @param mediaItemRendition
     *          {@link MediaItemRendition} to replace
     * @param executeHooks
     *          Should hooks be executed upon updating
     * @return Updated {@link MediaItemRendition}
     * @throws IOException 
     *          If the {@link MediaItemRendition} could not be updated in the
     *          {@link Catalogue}
     */
    MediaItemRendition update(File file, String filename, String contentType,
            MediaItemRendition mediaItemRendition, boolean executeHooks)
            throws java.io.IOException;

    CatalogueHookInstance createCatalogueAction(CatalogueHookInstance action);

    CatalogueHookInstance updateCatalogueAction(CatalogueHookInstance action);

    void executeBatchHook(CatalogueHookInstance hookInstance, Long catalogueId) throws
            DataNotFoundException;

    /**
     * Executes a {@link CatalogueHookInstance} on a {@link MediaItem}.
     * <p/>
     * @param mediaItemId    Unique identifier of the {@link MediaItem}
     * @param hookInstanceId Unique identifier of the {@link CatalogueHookInstance}
     * @throws DataNotFoundException If the given {@code mediaItemId} or {@code hookInstanceId} was invalid
     */
    void executeHook(java.lang.Long mediaItemId, java.lang.Long hookInstanceId)
            throws dk.i2m.converge.core.DataNotFoundException;

    /**
     * Finds a {@link List} of {@link Catalogue}s accessible to a given
     * {@link UserAccount}.
     * 
     * @param username 
     *          Username of the {@link UserAccount} for which to find the 
     *          accessible {@link Catalogue}s
     * @return {@link List} of {@link Catalogue}s accessible to the given
     *         {@link UserAccount}
     */
    List<Catalogue> findCataloguesByUser(String username);

    /**
     * Updates an existing {@link MediaItemRendition} in the database without
     * executing {@link CatalogueHook}s.
     *
     * @param mir
     *          {@link MediaItemRendition} to update
     * @return Updated {@link MediaItemRendition}
     */
    MediaItemRendition update(MediaItemRendition mir);
}
