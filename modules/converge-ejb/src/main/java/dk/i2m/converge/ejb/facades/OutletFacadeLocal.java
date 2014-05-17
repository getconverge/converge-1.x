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

import dk.i2m.converge.core.dto.OutletActionView;
import dk.i2m.converge.core.workflow.*;
import dk.i2m.converge.core.DataNotFoundException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;

/**
 * {@link Local} interface of the {@link OutletFacadeBean}.
 *
 * @author Allan Lykke Christensen
 */
@Local
public interface OutletFacadeLocal {

    Edition createEdition(Edition edition);

    Edition createEdition(Long outletId, Boolean open, Date publicationDate,
            Date expirationDate, Date closeDate);

    Edition createEdition(EditionCandidate editionCandidate);

    Outlet createOutlet(Outlet outlet);

    /**
     * Deletes an existing {@link Department} by its unique
     * <code>id</code>.
     *
     * @param id
     * Unique id of the {@link Department}
     */
    void deleteDepartment(Long id);

    /**
     * Delete an existing {@link Edition} by its unique
     * <code>id</code>.
     *
     * @param id
     * Unique id of the {@link Edition}
     */
    void deleteEdition(Long id);

    /**
     * Deletes an existing {@link Outlet} from the database.
     *
     * @param id
     * Unique ID of the {@link Outlet}
     */
    void deleteOutletById(Long id);

    /**
     * Finds all {@link Outlet}s in the system.
     *
     * @return {@link List} of all {@link Outlet}s
     */
    List<Outlet> findAllOutlets();

    /**
     * Finds a {@link Department} by its unique
     * <code>id</code>.
     *
     * @param if
     * Unique id of the {@link department}
     * @return {@link Department} matching the
     * <code>id</code>
     * @throws DataNotFoundException
     * If the {@link Department} could not be found
     */
    Department findDepartmentById(Long id) throws DataNotFoundException;

    Edition findEditionById(long id) throws DataNotFoundException;

    /**
     * Find editions of an {@link Outlet} by their status. Status is either open
     * (
     * <code>true</code>) or closed (
     * <code>false</code>).
     *
     * @param status
     * Status of the {@link Edition}s to find
     * @param outlet
     * {@link Outlet} for which to obtain the {@link Edition}s
     *
     * @return {@link List} of {@link Edition} for a given {@link Outlet}
     * filtered by
     * <code>status</code>
     */
    List<Edition> findEditionsByStatus(boolean status, Outlet outlet);

    /**
     * Finds the next edition of a given {@link Outlet}. The next possible
     * edition
     * is only sought 1 month ahead. That means, if the date today is 1st
     * January,
     * it will only look for editions up till 1st February. If a new edition has
     * not come up between 1st January and 1st February, a {@link DataNotFoundException}
     * is thrown.
     *
     * @param outlet
     * {@link Outlet} for which to find the next edition
     * @return Next {@link Edition} based on the date and status
     * @throws DataNotFoundException
     * If the edition could not be found
     */
    Edition findNextEdition(Outlet outlet) throws DataNotFoundException;

    /**
     * Find {@link Edition}s by date.
     *
     * @param outlet
     * {@link Outlet} for which to find {@link Edition}s
     * @param date
     * Dates for which to find {@link Edition}s
     * @return {@link List} of {@link Edition}s for the given {@link Outlet} and
     * date
     */
    List<Edition> findEditionsByDate(Outlet outlet, Calendar date);

    /**
     * Find {@link EditionCandidate}s by date.
     *
     * @param outlet
     * {@link Outlet} for which to find {@link Edition}s
     * @param date
     * Dates for which to find {@link Edition}s
     * @param includeClosed
     * Include closed editions
     * @return {@link List} of {@link EditionCandidate}s for the given {@link Outlet}
     * and
     * date
     */
    List<EditionCandidate> findEditionCandidatesByDate(Outlet outlet,
            Calendar date, boolean includeClosed);

    /**
     * Finds an {@link Outlet} in the database by its unique id.
     *
     * @param id
     * Unique id of the {@link Outlet} to find
     * @return {@link Outlet} matching the
     * <code>id</code>
     * @ * dk.i2m.dao.DataNotFoundException
     * If an {@link Outlet} could not be found with the given
     * <code>id</code>
     */
    Outlet findOutletById(Long id) throws DataNotFoundException;

    /**
     * Creates a new {@link Department}.
     *
     * @param department
     * {@link Department} to update
     * @return {@link Department} containing generated values from the database
     */
    Department createDepartment(Department department);

    /**
     * Updates an existing {@link Department}.
     *
     * @param department
     * {@link Department} to update
     */
    void updateDepartment(Department department);

    /**
     * Updates an existing {@link Edition}.
     *
     * @param edition
     * {@link Edition} to update
     * @return Updated edition
     */
    Edition updateEdition(Edition edition);

    Edition updateEdition(Long editionId, Boolean open, Date publicationDate,
            Date expirationDate, Date closeDate) throws DataNotFoundException;

    /**
     * Updates an existing {@link Outlet} in the database.
     *
     * @param outlet
     * {@link Outlet} to update in the database
     * @return Updated {@link Outlet}
     */
    Outlet updateOutlet(Outlet outlet);

    /**
     * Close all open {@link Edition}s that are overdue.
     *
     * @return Number of {@link Edition}s closed
     */
    int closeOverdueEditions();

    /**
     * Finds a {@link List} of {@link Edition}s to be published for a given
     * {@link Outlet} on a given date.
     *
     * @param outletId
     * Unique identifier of the {@link Outlet}
     * @param date
     * Publication date of the {@link Edition}
     * @return {@link List} of {@link Edition}s published for the given
     *         {@link Outlet} on the given date
     */
    List<Edition> findEditionByOutletAndDate(long outletId, Calendar date);

    /**
     * Finds a {@link Section} by its unique identifier.
     *
     * @param id
     * Unique identifier of the {@link Section}
     * @return {@link Section} matching the {@code id}
     * @throws DataNotFoundException
     * If no {@link Section} could be matched
     */
    Section findSectionById(Long id) throws DataNotFoundException;

    /**
     * Creates a new {@link Section}.
     *
     * @param section
     * {@link Section} to create
     * @return {@link Section} created
     */
    Section createSection(Section section);

    /**
     * Updates an existing {@link Section}.
     *
     * @param section
     * {@link Section} to update
     * @return {@link Section} updated
     */
    Section updateSection(Section section);

    /**
     * Deletes an existing {@link Section} by its unique identifier.
     *
     * @param id
     * Unique identifier of the {@link Section}
     * @throws EntityReferenceException
     * If there are references to this {@link Section} and can
     * therefore not be deleted
     */
    void deleteSection(Long id) throws EntityReferenceException;

    /**
     * Stores a new {@link OutletEditionAction} in the database.
     *
     * @param action
     * {@link OutletEditionAction} to store in the database
     * @return {@link OutletEditionAction} created
     */
    OutletEditionAction createOutletAction(OutletEditionAction action);

    /**
     * Updates an {@link OutletEditionAction} in the database.
     *
     * @param action
     * {@link OutletEditionAction} to update in the database
     * @return {@link OutletEditionAction} updated
     */
    OutletEditionAction updateOutletAction(OutletEditionAction action);

    /**
     * Deletes an {@link OutletEditionAction} from the database.
     *
     * @param id
     * Unique identifier of the {@link OutletEditionAction} to delete
     */
    void deleteOutletActionById(Long id);

    /**
     * Deletes an existing {@link EditionPattern} by its unique identifier.
     *
     * @param id
     * Unique identifier of the {@link EditionPattern}
     */
    void deleteEditionPatternById(Long id);

    /**
     * Creates a new {@link EditionPattern}.
     *
     * @param editionPattern
     * {@link EditionPattern} to create
     * @return {@link EditionPattern} created
     */
    EditionPattern createEditionPattern(EditionPattern editionPattern);

    /**
     * Updates an existing {@link EditionPattern}.
     *
     * @param editionPattern
     * {@link EditionPattern} to update
     * @return {@link EditionPattern} updated
     */
    EditionPattern updateEditionPattern(EditionPattern editionPattern);

    void scheduleAction(Long editionId, Long actionId);

    void scheduleActions(Long editionId);

    void scheduleNewsItemPlacementActions(Long editionId,
            Long newsItemPlacementId);

    void scheduleNewsItemPlacementAction(Long editionId, Long actionId,
            Long newsItemPlacementId);

    void scheduleActionOnOutlet(Long outletId, Long actionId);

    void scheduleActionsOnOutlet(Long outletId);

    java.util.List<dk.i2m.converge.core.dto.EditionView> findEditionViewsByDate(
            java.lang.Long outletId, java.util.Date date, boolean includeOpen,
            boolean includeClosed);

    java.util.List<dk.i2m.converge.core.dto.OutletActionView> findOutletActions(
            java.lang.Long id) throws DataNotFoundException;

    List<OutletActionView> findOutletPlacementActions(Long id) throws
            DataNotFoundException;

    List<Section> findSectionByName(Long outletId, String sectionName) throws
            DataNotFoundException;

    dk.i2m.converge.core.subscriber.OutletSubscriber createSubscriber(
            dk.i2m.converge.core.subscriber.OutletSubscriber subscriber);

    dk.i2m.converge.core.subscriber.OutletSubscriber updateSubscriber(
            dk.i2m.converge.core.subscriber.OutletSubscriber subscriber);

    dk.i2m.converge.core.subscriber.OutletSubscriber findSubscriberById(
            java.lang.Long id)
            throws dk.i2m.converge.core.DataNotFoundException;

    void deleteSubscriberById(java.lang.Long id);

    java.util.List<dk.i2m.converge.core.subscriber.OutletSubscriber> findOutletSubscribers(
            int start,
            int results);

    java.util.List<dk.i2m.converge.core.subscriber.OutletSubscriber> findOutletSubscribers(
            java.lang.Long outletId,
            int start, int results);
}
