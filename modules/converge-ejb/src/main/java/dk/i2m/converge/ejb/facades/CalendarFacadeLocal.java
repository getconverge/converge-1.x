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

import dk.i2m.converge.core.DataNotFoundException;
import dk.i2m.converge.core.calendar.Event;
import dk.i2m.converge.core.calendar.EventCategory;
import java.util.Calendar;
import java.util.List;
import javax.ejb.Local;

/**
 * Local interface for the calendar facade enterprise bean.
 *
 * @author Allan Lykke Christensen
 */
@Local
public interface CalendarFacadeLocal {

    /**
     * Creates a new event in the database.
     *
     * @param event
     *          {@link Event} to create
     * @return  Created {@link Event}
     */
    Event create(Event event);

    /**
     * Deletes an existing event from the database.
     *
     * @param id
     *          ID of the event to delete
     */
    void delete(Long id);

    /**
     * Finds all events from the database.
     *
     * @return {@link List} of all events from the database {@link Event}s
     */
    List<Event> findAll();

    /**
     * Finds all events that falls in between two dates.
     *
     * @param start
     *          Start time
     * @param end
     *          End time
     * @return {@link List} of {@link Event}s that fall between the
     *         <code>start</code> and <code>end</code> date
     */
    List<Event> findByDate(Calendar start, Calendar end);

    /**
     * Finds a single event in the database by its ID.
     *
     * @param id
     *          ID of the event
     * @return {@link Event} matching the <code>id</code>
     * @exception DataNotFoundException
     *              If the {@link Event} could not be found
     */
    Event findById(Long id) throws DataNotFoundException;

    /**
     * Updates an existing event in the database.
     *
     * @param event
     *          {@link Event} to update
     * @return Updated {@link Event}
     */
    Event update(Event event);

    /**
     * Finds all events from a given date onwards.
     *
     * @param start
     *          Start time
     * @return {@link List} of {@link Event}s from a given date onwards
     */
    List<Event> findByStartDate(Calendar start);

    /**
     * Finds all upcoming events
     *
     * @return {@link List} of upcoming {@link Event}s
     */
    List<Event> findUpcoming();

    /**
     * Generates a VCal calendar with all the events stored in the database.
     *
     * @return {@link String} containing a VCal formatted calendar
     */
    String generateVCal();

    /**
     * Finds todays events.
     *
     * @return {@link List} of todays {@link Event}s
     */
    List<Event> findTodaysEvents();

    /**
     * Finds upcoming events.
     *
     * @param days
     *          Number of days to include in the upcoming events
     *
     * @return {@link List} of upcoming {@link Event}s <code>x</code> days in
     *         the future
     */
    List<Event> findUpcomingEvents(int days);

    /**
     * Creates a new {@link EventCategory}.
     *
     * @param category
     *          {@link EventCategory} to create
     * @return Created {@link EventCategory}
     */
    EventCategory create(EventCategory category);

    /**
     * Updates an existing {@link EventCategory}.
     *
     * @param category
     *          {@link EventCategory} to update
     * @return Updated {@link EventCategory}
     */
    EventCategory update(EventCategory category);

    /**
     * Delete an existing {@link EventCategory}.
     *
     * @param id
     *          Unique identifier of the {@link EventCategory}
     */
    void deleteEventCategory(Long id);

    /**
     * Finds an existing {@link EventCategory}.
     *
     * @param id
     *          Unique identifier of the {@link EventCategory}
     * @return {@link EventCategory} matching the <code>id</code>
     * @throws DataNotFoundException
     *          If the {@link EventCategory} could not be found.
     */
    EventCategory findEventCategoryById(Long id) throws
            DataNotFoundException;

    /**
     * Finds all the event categories.
     *
     * @return {@link List} of event categories
     */
    List<EventCategory> findAllCategories();
}
