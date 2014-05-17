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
import dk.i2m.converge.core.calendar.Event;
import dk.i2m.converge.core.calendar.EventCategory;
import dk.i2m.converge.ejb.services.ConfigurationServiceLocal;
import dk.i2m.converge.ejb.services.DaoServiceLocal;
import dk.i2m.converge.ejb.services.QueryBuilder;
import java.net.SocketException;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.*;
import net.fortuna.ical4j.util.UidGenerator;

/**
 * Facade for accessing the calendar.
 *
 * @author Allan Lykke Christensen
 */
@Stateless
public class CalendarFacadeBean implements CalendarFacadeLocal {

    @EJB private DaoServiceLocal daoService;

    @EJB private ConfigurationServiceLocal cfgService;

    private static final Logger LOG = Logger.getLogger(CalendarFacadeBean.class.
            getName());

    /** {@inheritDoc} */
    @Override
    public Event create(Event event) {
        return daoService.create(event);
    }

    /** {@inheritDoc} */
    @Override
    public Event update(Event event) {
        return daoService.update(event);
    }

    /** {@inheritDoc} */
    @Override
    public void delete(Long id) {
        daoService.delete(Event.class, id);
    }

    /** {@inheritDoc} */
    @Override
    public Event findById(Long id) throws DataNotFoundException {
        return daoService.findById(Event.class, id);
    }

    /** {@inheritDoc} */
    @Override
    public List<Event> findAll() {
        return daoService.findAll(Event.class);
    }

    /** {@inheritDoc} */
    @Override
    public List<Event> findByDate(Calendar start, Calendar end) {
        Map params = QueryBuilder.with("start", start).and("end", end).
                parameters();
        return daoService.findWithNamedQuery(Event.FIND_BY_DATE, params);
    }

    /** {@inheritDoc} */
    @Override
    public List<Event> findByStartDate(Calendar start) {
        Map params = QueryBuilder.with("start", start).parameters();
        return daoService.findWithNamedQuery(Event.FIND_BY_START_DATE, params);
    }

    /** {@inheritDoc} */
    @Override
    public List<Event> findUpcoming() {
        return findByStartDate(Calendar.getInstance());
    }

    /** {@inheritDoc} */
    @Override
    public List<Event> findTodaysEvents() {
        Calendar now = Calendar.getInstance();
        Map params = QueryBuilder.with("date", now).parameters();
        return daoService.findWithNamedQuery(Event.FIND_BY_BETWEEN, params);
    }

    /** {@inheritDoc} */
    @Override
    public List<Event> findUpcomingEvents(int days) {
        Calendar now = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.DAY_OF_MONTH, days);
        Map params = QueryBuilder.with("start", now).
                and("end", endDate).parameters();

        return daoService.findWithNamedQuery(Event.FIND_BY_DATE, params);
    }

    /** {@inheritDoc} */
    @Override
    public String generateVCal() {
        net.fortuna.ical4j.model.Calendar vcal =
                new net.fortuna.ical4j.model.Calendar();

        String calendarName = "Converge";
        String appId = cfgService.getLongVersion();

        vcal.getProperties().add(new ProdId("-//" + calendarName + "//" + appId
                + "//EN"));
        vcal.getProperties().add(Version.VERSION_2_0);
        vcal.getProperties().add(CalScale.GREGORIAN);

        UidGenerator ug;
        try {
            ug = new UidGenerator("1");
        } catch (SocketException ex) {
            LOG.log(Level.SEVERE,
                    "Could not initialise UidGenerator for calendar", ex);
            return vcal.toString();
        }

        for (Event event : findAll()) {
            VEvent vevent;

            if (event.isAllDayEvent()) {
                vevent = new VEvent(new Date(event.getStartDate().getTime()), event.
                        getSummary());
            } else {
                vevent = new VEvent(new DateTime(event.getStartDate().getTime()), new DateTime(event.
                        getEndDate().getTime()), event.getSummary());
            }
            vevent.getProperties().add(new Description(event.getDescription()));
            vevent.getProperties().add(new Location(event.getLocation()));
            vevent.getProperties().add(ug.generateUid());

            vcal.getComponents().add(vevent);
        }

        return vcal.toString();
    }

    /** {@inheritDoc} */
    @Override
    public EventCategory create(EventCategory category) {
        return daoService.create(category);
    }

    /** {@inheritDoc} */
    @Override
    public EventCategory update(EventCategory category) {
        return daoService.update(category);
    }

    /** {@inheritDoc} */
    @Override
    public void deleteEventCategory(Long id) {
        daoService.delete(EventCategory.class, id);
    }

    /** {@inheritDoc} */
    @Override
    public EventCategory findEventCategoryById(Long id) throws
            DataNotFoundException {
        return daoService.findById(EventCategory.class, id);
    }

    /** {@inheritDoc} */
    @Override
    public List<EventCategory> findAllCategories() {
        return daoService.findAll(EventCategory.class);
    }
}
