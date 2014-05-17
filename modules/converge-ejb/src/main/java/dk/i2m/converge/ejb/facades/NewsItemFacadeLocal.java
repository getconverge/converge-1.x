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
package dk.i2m.converge.ejb.facades;

import dk.i2m.converge.core.content.catalogue.MediaItem;
import dk.i2m.converge.core.views.CurrentAssignment;
import dk.i2m.converge.core.DataNotFoundException;
import dk.i2m.converge.core.workflow.Outlet;
import dk.i2m.converge.core.content.ContentItemPermission;
import dk.i2m.converge.core.content.NewsItem;
import dk.i2m.converge.core.content.NewsItemMediaAttachment;
import dk.i2m.converge.core.content.NewsItemPlacement;
import dk.i2m.converge.core.workflow.WorkflowState;
import dk.i2m.converge.core.security.UserAccount;
import dk.i2m.converge.core.views.InboxView;
import dk.i2m.converge.core.workflow.Edition;
import dk.i2m.converge.core.workflow.Section;
import java.util.List;
import javax.ejb.Local;

/**
 * Session-bean providing a facade to working with {@link NewsItem}s.
 *
 * @author Allan Lykke Christensen
 */
@Local
public interface NewsItemFacadeLocal {

    NewsItem start(NewsItem newsItem) throws WorkflowStateTransitionException;

    NewsItem step(NewsItem newsItem, Long step, String comment) throws WorkflowStateTransitionException;

    /**
     * Promotes the {@link NewsItem} in the workflow. This step is done
     * regardless of any options.
     *
     * @param newsItem
     *          {@link NewsItem} to promote
     * @param state
     *          New state
     * @param comment
     *          Comment from the sender
     * @return Promoted {@link NewsItem}
     * @throws WorkflowStateTransitionException
     *          If the transition could not be completed
     */
    NewsItem step(NewsItem newsItem, WorkflowState state, String comment) throws WorkflowStateTransitionException;

    
    NewsItemHolder checkout(java.lang.Long id) throws dk.i2m.converge.core.DataNotFoundException;

    /**
     * Determines if a given {@link NewsItem} has been checked out.
     *
     * @param id
     *          Unique identifier of the {@link NewsItem}
     * @return {@code true} if the {@link NewsItem} has been checked out,
     *         otherwise {@code false}
     */
    boolean isCheckedOut(java.lang.Long id);

    /**
     * Saves a {@link NewsItem} in the database without checking it in.
     *
     * @param newsItem
     *          {@link NewsItem} to save
     * @return Saved {@link NewsItem}
     * @throws dk.i2m.converge.ejb.facades.LockingException
     *          If the {@link NewsItem} is not checked-out or checked-out by a
     *          different user
     */
    dk.i2m.converge.core.content.NewsItem save(dk.i2m.converge.core.content.NewsItem newsItem) throws dk.i2m.converge.ejb.facades.LockingException;

    int emptyTrash(String username);

    /**
     * Finds all {@link NewsItem}s with a given {@link WorkflowState} for an
     * {@link Outlet}.
     *
     * @param state
     *          {@link WorkflowState}
     * @param outlet
     *          {@link Outlet}
     * @return {@link List} of {@link NewsItem}s for a given {@link Outlet} with
     *         a given {@link WorkflowState}
     */
    List<NewsItem> findByStateAndOutlet(WorkflowState state, Outlet outlet);

    /**
     * Finds all {@link NewsItem}s with a given {@link WorkflowState} for an
     * {@link Outlet}.
     *
     * @param stateName
     *          Name of the {@link WorkflowState}
     * @param outlet
     *          {@link Outlet}
     * @return {@link List} of {@link NewsItem}s for a given {@link Outlet} with
     *         a given {@link WorkflowState}
     */
    List<NewsItem> findByStateAndOutlet(String stateName, Outlet outlet);

    List<NewsItem> findByStateAndOutlet(WorkflowState state, Outlet outlet, int start, int results);

    /**
     * Finds an {@link NewsItem} by its unique identifier.
     *
     * @param id
     *          Unique identifier of the {@link NewsItem}
     * @return {@link NewsItem} matching the <code>id</code>.
     * @throws DataNotFoundException
     *          If a {@link NewsItem} with the given {@code id} does not exist
     */
    NewsItem findNewsItemById(Long id) throws DataNotFoundException;

    /**
     * Determines if a given {@link NewsItem} it published. If the
     * {@link NewsItem} is published, <code>true</code> is returned, otherwise
     * <code>false</code> is returned. If the {@link NewsItem} does not exist
     * a {@link DataNotFoundException} will be thrown.
     *
     * @param newsItemId
     *          Unique identifier of the {@link NewsItem}
     * @return <code>true</code> if the {@link NewsItem} exist, otherwise
     *         <code>false</code>
     * @throws DataNotFoundException
     *          If the {@link NewsItem} does not exist
     */
    boolean isNewsItemPublished(final Long newsItemId) throws DataNotFoundException;

    /**
     * Check-in a {@link NewsItem} in the database without making a state
     * transition.
     *
     * @param newsItem
     *          {@link NewsItem} to check-in.
     * @return Checked-in {@link NewsItem}
     * @throws LockingException
     *          If the {@link NewsItem} was checked-out by someone else
     */
    NewsItem checkin(NewsItem newsItem) throws LockingException;

    /**
     * Gets all the {@link NewsItem}s where the given user is the active part.
     *
     * @param username
     *          Unique username of the {@link UserAccount}
     * @return {@link List} of {@link NewsItem}s where the given user is the
     *         active part
     */
    List<NewsItem> findByActiveUser(String username);

    /**
     * Finds assignments (open news items) by {@link Outlet}.
     * 
     * @param selectedOutlet
     *          {@link Outlet} for which to find assignments
     * @return {@link List} of assignments
     */
    List<NewsItem> findAssignmentsByOutlet(Outlet selectedOutlet);

    /**
     * Safely deletes an existing {@link NewsItem} from the database. Safe
     * deletion will transition the news item to the trash state of the
     * workflow.
     *
     * @param id
     *          Unique identifier of the {@link NewsItem}
     * @return <code>true</code> if the item was deleted, otherwise <code>false</code>
     */
    boolean deleteNewsItem(Long id);

    /**
     * Deletes an existing {@link NewsItem} from the database.
     *
     * @param id
     *          Unique identifier of the {@link NewsItem}
     * @param safe
     *          Enable safe mode
     * @return <code>true</code> if the item was deleted, otherwise <code>false</code>
     * @see NewsItemFacadeLocal#deleteNewsItem(java.lang.Long, boolean) 
     */
    boolean deleteNewsItem(Long id, boolean safe);

    /**
     * Removes an actor from a {@link NewsItem}.
     *
     * @param actor
     *          Actor to remove from the {@link NewsItem}
     * @return Updated {@link NewsItem}
     */
    NewsItem removeActorFromNewsItem(dk.i2m.converge.core.content.NewsItemActor actor);

    /**
     * Add an actor to a {@link NewsItem}.
     *
     * @param actor
     *          Actor to add to the {@link NewsItem}
     * @return Actor added to the {@link NewsItem}
     */
    dk.i2m.converge.core.content.NewsItemActor addActorToNewsItem(dk.i2m.converge.core.content.NewsItemActor actor);

    /**
     * Determines if the given user is the current actor of the given news item.
     *
     * @param newsItemId
     *          Unique identifier of the NewsItem
     * @param username
     *          Username of the UserAccount
     * @return Level of permission for the given user
     */
    ContentItemPermission getPermission(Long newsItemId, String username);

    /**
     * Finds all the versions of a given {@link NewsItem}.
     *
     * @param newsItemId
     *          Unique identifier of the {@link NewsItem} for which to find the version
     * @return {@link List} of {@link NewsItem}s that are a version of the given {@link NewsItem}.
     */
    java.util.List<dk.i2m.converge.core.content.NewsItem> findVersions(java.lang.Long newsItemId);

    /**
     * Revokes a lock on a {@link NewsItem}.
     *
     * @param id
     *          Unique identifier of the {@link NewsItem}
     * @return {@code true} of the lock was revoked or {@code false} if the news
     *         item was not locked
     */
    boolean revokeLock(Long id);

    /**
     * Revokes all the locks of a particular {@link UserAccount}.
     *
     * @param username
     *          Username of the user
     * @return Number of locks revoked
     */
    int revokeLocks(final String username);

    /**
     * Revokes all news item locks.
     *
     * @return Number of locks revoked
     */
    int revokeAllLocks();

    /**
     * Finds the current assignments of a given user.
     * 
     * @param username
     *          Username of the user
     * @return {@link List} of current assignments
     */
    List<CurrentAssignment> findCurrentAssignments(String username);

    /**
     * Pulls back an assignment from a given state to its previous state.
     * 
     * @param id
     *          Unique identifier of the assignment
     * @throws LockingException
     *          If the assignment was already locked by the recipient
     * @throws WorkflowStateTransitionException
     *          If the state does not support workflow pullback
     */
    void pullback(Long id) throws LockingException, WorkflowStateTransitionException;

    /**
     * Creates a new {@link MediaItem} in the database.
     *
     * @param mediaItem
     *          {@link MediaItem} to create
     * @return Create {@link MediaItem}
     */
    MediaItem create(MediaItem mediaItem);

    /**
     * Creates a new {@link NewsItemMediaAttachment} in the database.
     *
     * @param attachment
     *          {@link NewsItemMediaAttachment} to create
     * @return {@link NewsItemMediaAttachment} created
     */
    NewsItemMediaAttachment create(NewsItemMediaAttachment attachment);

    NewsItemMediaAttachment update(NewsItemMediaAttachment attachment);

    void deleteMediaAttachmentById(Long id);

    NewsItem findNewsItemFromArchive(Long id) throws DataNotFoundException;

    /**
     * Adds a {@link NewsItem} to the next open edition.
     * 
     * @param newsItem
     *          {@link NewsItem} to add to the next open edition
     * @param section
     *          {@link Section} to add the {@link NewsItem}
     * @return Placement of the {@link NewsItem}
     * @throws DataNotFoundException
     *          If an {@link Edition} could not be located
     */
    NewsItemPlacement addToNextEdition(NewsItem newsItem, Section section) throws DataNotFoundException;

    NewsItemPlacement createPlacement(NewsItemPlacement placement);

    NewsItemPlacement updatePlacement(NewsItemPlacement placement);

    NewsItemPlacement updatePlacement(Long placementId, Integer start, Integer position);

    void deletePlacementById(Long id);

    void deletePlacement(NewsItemPlacement placement);

    void updatePrecalculatedFields();

    java.util.List<InboxView> findInbox(java.lang.String username, int start, int limit);

    java.util.List<InboxView> findInbox(java.lang.String username);

    java.util.List<dk.i2m.converge.core.views.InboxView> findOutletBox(java.lang.String username, dk.i2m.converge.core.workflow.Outlet outlet);

    java.util.List<dk.i2m.converge.core.views.InboxView> findOutletBox(java.lang.String username, dk.i2m.converge.core.workflow.Outlet outlet, dk.i2m.converge.core.workflow.WorkflowState state);

    java.util.List<dk.i2m.converge.core.views.InboxView> findOutletBox(java.lang.String username, dk.i2m.converge.core.workflow.Outlet outlet, dk.i2m.converge.core.workflow.WorkflowState state, int start, int results);

    NewsItemPlacement findNewsItemPlacementById(Long id) throws DataNotFoundException;
}
