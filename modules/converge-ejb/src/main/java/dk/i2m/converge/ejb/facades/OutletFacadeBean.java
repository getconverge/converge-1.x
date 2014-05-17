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
import dk.i2m.converge.core.content.NewsItemPlacement;
import dk.i2m.converge.core.dto.EditionAssignmentView;
import dk.i2m.converge.core.dto.EditionView;
import dk.i2m.converge.core.dto.OutletActionView;
import dk.i2m.converge.core.security.UserRole;
import dk.i2m.converge.core.subscriber.OutletSubscriber;
import dk.i2m.converge.core.utils.BeanComparator;
import dk.i2m.converge.core.workflow.*;
import dk.i2m.converge.ejb.messaging.EditionServiceMessageBean;
import dk.i2m.converge.ejb.messaging.OutletServiceMessageBean;
import dk.i2m.converge.ejb.services.DaoServiceLocal;
import dk.i2m.converge.ejb.services.QueryBuilder;
import dk.i2m.converge.utils.CalendarUtils;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.*;
import javax.jms.*;

/**
 * Stateless session bean providing a facade to working with {@link Outlet}s.
 *
 * @author Allan Lykke Christensen
 */
@Stateless
public class OutletFacadeBean implements OutletFacadeLocal {

    private static final Logger LOG = Logger.getLogger(OutletFacadeBean.class.
            getName());

    @EJB private DaoServiceLocal daoService;

    @Resource(mappedName = "jms/editionServiceQueue") private Destination destination;

    @Resource(mappedName = "jms/outletServiceQueue") private Destination outletServiceQueue;

    @Resource(mappedName = "jms/connectionFactory") private ConnectionFactory jmsConnectionFactory;

    @Resource private SessionContext ctx;

    /**
     * Creates a new instance of {@link OutletFacadeBean}.
     */
    public OutletFacadeBean() {
    }

    /**
     * Ensures that the necessary data has been set on the {@link Outlet}.
     * <p/>
     * @param outlet {@link Outlet} to validate
     */
    private void validateOutlet(Outlet outlet) {
        // Ensure that the necessary roles have been added to the outlet
        if (outlet.getWorkflow() != null) {
            List<UserRole> mandatory = outlet.getWorkflow().
                    getUserRolesInWorkflowStates();
            for (UserRole role : mandatory) {
                if (!outlet.getRoles().contains(role)) {
                    outlet.getRoles().add(role);
                }
            }
        }
    }

    /**
     * Creates a new {@link Outlet}.
     *
     * @param outlet {@link Outlet} to create in the database
     * @return {@link Outlet} containing generated values from the database
     */
    @Override
    public Outlet createOutlet(Outlet outlet) {
        validateOutlet(outlet);
        return daoService.create(outlet);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Outlet> findAllOutlets() {
        return daoService.findAll(Outlet.class);
    }

    /**
     * Finds an {@link Outlet} in the database by its unique id.
     *
     * @param id Unique id of the {@link Outlet} to find
     * @return {@link Outlet} matching the {@code id}
     * @throws DataNotFoundException If an {@link Outlet} could not be found with the given {@code id}
     */
    @Override
    public Outlet findOutletById(Long id) throws DataNotFoundException {
        return daoService.findById(Outlet.class, id);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Outlet updateOutlet(Outlet outlet) {
        validateOutlet(outlet);
        return daoService.update(outlet);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void deleteOutletById(Long id) {
        daoService.delete(Outlet.class, id);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Department createDepartment(Department department) {
        return daoService.create(department);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Department findDepartmentById(Long id) throws DataNotFoundException {
        return daoService.findById(Department.class, id);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void updateDepartment(Department department) {
        daoService.update(department);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void deleteDepartment(Long id) {
        //TODO: Determine what to do about news item current in this department
        daoService.delete(Department.class, id);
    }

    /**
     * Create a new subscriber of an {@link Outlet}.
     * <p/>
     * @param subscriber New subscriber
     * @return Created subscriber
     */
    @Override
    public OutletSubscriber createSubscriber(OutletSubscriber subscriber) {
        return daoService.create(subscriber);
    }

    /**
     * Update an existing subscriber.
     * <p/>
     * @param subscriber Subscriber to update
     * @return Updated subscriber
     */
    @Override
    public OutletSubscriber updateSubscriber(OutletSubscriber subscriber) {
        // Check if there was a subscription change
        try {
            Date now = Calendar.getInstance().getTime();
            OutletSubscriber original = findSubscriberById(subscriber.getId());
            if (original.isSubscribed() && !subscriber.isSubscribed()) {
                subscriber.setUnsubscriptionDate(now);
            } else if (!original.isSubscribed() && subscriber.isSubscribed()) {
                subscriber.setSubscriptionDate(now);
            }
        } catch (DataNotFoundException ex) {
            LOG.log(Level.SEVERE, "Original subscriber does not exist");
        }

        return daoService.update(subscriber);
    }

    /**
     * Finds an existing subscriber by id.
     * <p/>
     * @param id Unique identifier of the subscriber
     * @return Subscriber matching the {@code id}
     * @throws DataNotFoundException If a subscriber with the given {@code id} could not be found
     */
    @Override
    public OutletSubscriber findSubscriberById(Long id) throws
            DataNotFoundException {
        return daoService.findById(OutletSubscriber.class, id);
    }

    /**
     * Deletes a subscriber from the database.
     * <p/>
     * @param id Unique identifier of the subscriber
     */
    @Override
    public void deleteSubscriberById(Long id) {
        daoService.delete(OutletSubscriber.class, id);
    }

    @Override
    public List<OutletSubscriber> findOutletSubscribers(int start, int results) {
        return daoService.findAll(OutletSubscriber.class, start, results);
    }

    @Override
    public List<OutletSubscriber> findOutletSubscribers(Long outletId, int start,
            int results) {
        try {
            Outlet outlet = findOutletById(outletId);
            Map<String, Object> params = QueryBuilder.with(
                    OutletSubscriber.OUTLET_PARAMETER, outlet).parameters();

            return daoService.
                    findWithNamedQuery(OutletSubscriber.FIND_BY_OUTLET,
                    params, start, results);
        } catch (DataNotFoundException ex) {
            return Collections.EMPTY_LIST;
        }
    }

    /**
     * Creates an {@link Edition} for an {@link Outlet}.
     *
     * @param edition {@link Edition} to create
     */
    @Override
    public Edition createEdition(Edition edition) {
        return daoService.create(edition);
    }

    /**
     * Creates an {@link Edition} for the given outlet.
     *
     * @param outletId
     * Unique identifier of the {@link Outlet}
     * @param open
     * Determines if the {@link Edition} is open for placements
     * @param publicationDate
     * Date of publication of the {@link Edition}
     * @param expirationDate
     * Date of expiration of the {@link Edition}
     * @param closeDate
     * Date when the {@link Edition} closes for additions
     */
    @Override
    public Edition createEdition(Long outletId, Boolean open,
            Date publicationDate, Date expirationDate, Date closeDate) {
        Calendar pubDate = Calendar.getInstance();
        pubDate.setTime(publicationDate);

        Calendar expDate = Calendar.getInstance();
        expDate.setTime(expirationDate);

        Edition edition = new Edition();
        edition.setCloseDate(closeDate);
        edition.setPublicationDate(pubDate);
        edition.setExpirationDate(expDate);
        edition.setNumber(0);
        edition.setVolume(0);
        edition.setOpen(open);

        try {
            Outlet outlet = daoService.findById(Outlet.class, outletId);
            edition.setOutlet(outlet);
        } catch (DataNotFoundException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }

        return daoService.create(edition);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int closeOverdueEditions() {
        List<Edition> overdue = daoService.findWithNamedQuery(
                Edition.FIND_OVERDUE);
        int closed = 0;

        for (Edition edition : overdue) {
            closed++;
            edition.setOpen(false);
            updateEdition(edition);
            scheduleActions(edition.getId());
        }

        return closed;
    }

    /**
     * Creates an {@link Edition} for an {@link Outlet} from an
     * {@link EditionCandidate}.
     *
     * @param editionCandidate {@link EditionCandidate} from which to create the {@link Edition}
     * @return {@link Edition} created from the {@link EditionCandidat}
     */
    @Override
    public Edition createEdition(EditionCandidate editionCandidate) {
        Edition edition = new Edition();
        edition.setCloseDate(editionCandidate.getCloseDate());
        edition.setExpirationDate(Calendar.getInstance());
        edition.getExpirationDate().
                setTime(editionCandidate.getExpirationDate());
        edition.setPublicationDate(Calendar.getInstance());
        edition.getPublicationDate().setTime(
                editionCandidate.getPublicationDate());
        try {
            edition.setOutlet(findOutletById(editionCandidate.getOutletId()));
        } catch (DataNotFoundException ex) {
            LOG.log(Level.INFO, ex.getMessage());
        }
        return createEdition(edition);
    }

    /**
     * Finds an {@link Edition} by its unique {@code id}.
     *
     * @param id Unique id of the {@link Edition}
     * @return {@link Edition} matching the {@code id}
     * @throws DataNotFoundException If an {@link Edition} could not be found with the given {@code id}
     */
    @Override
    public Edition findEditionById(long id) throws DataNotFoundException {
        return daoService.findById(Edition.class, id);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Edition> findEditionByOutletAndDate(long outletId, Calendar date) {
        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        startDate.setTime(date.getTime());
        startDate.set(Calendar.HOUR_OF_DAY, 0);
        startDate.set(Calendar.MINUTE, 0);
        startDate.set(Calendar.SECOND, 0);

        endDate.setTime(date.getTime());
        endDate.set(Calendar.HOUR_OF_DAY, 23);
        endDate.set(Calendar.MINUTE, 59);
        endDate.set(Calendar.SECOND, 59);
        Outlet outlet = null;
        try {
            outlet = findOutletById(outletId);
        } catch (DataNotFoundException ex) {
            return new ArrayList<Edition>();
        }
        Map params = QueryBuilder.with("outlet", outlet).
                and("start_date", startDate).
                and("end_date", endDate).parameters();

        return daoService.findWithNamedQuery(Edition.FIND_BY_OUTLET_AND_DATE,
                params);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Edition> findEditionsByStatus(boolean status, Outlet outlet) {
        Map<String, Object> params = QueryBuilder.with("outlet", outlet).and(
                "status", status).parameters();
        return daoService.findWithNamedQuery(Edition.FIND_BY_STATUS, params);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Edition findNextEdition(Outlet outlet) throws DataNotFoundException {
        Calendar endSearch = Calendar.getInstance();
        endSearch.add(Calendar.MONTH, 1);
        Edition match = null;
        Calendar now = Calendar.getInstance();

        while (now.before(endSearch)) {
            List<Edition> candidates = findEditionsByDate(outlet, now);

            for (Edition candidate : candidates) {
                if (candidate.isOpen()) {
                    if (match == null) {
                        match = candidate;
                    }
                    if (candidate.getPublicationDate().after(now) && match.
                            getPublicationDate().after(candidate.
                            getPublicationDate())) {
                        match = candidate;
                    }
                }
            }
            now.add(Calendar.DAY_OF_MONTH, 1);
        }

        if (match == null) {
            throw new DataNotFoundException();
        }

        return match;
    }

    @Override
    public List<Edition> findEditionsByDate(Outlet outlet, Calendar date) {
        // 1. Check if there are any editions in the database matching the date and outlet
        Calendar startDate = CalendarUtils.getStartOfDay(date);
        Calendar endDate = CalendarUtils.getEndOfDay(date);

        Map<String, Object> params = QueryBuilder.with("outlet", outlet).and(
                "start_date", startDate).and("end_date", endDate).parameters();
        List<Edition> editions = daoService.findWithNamedQuery(
                Edition.FIND_BY_OUTLET_AND_DATE, params);

        // 2. Generate editions based on pattern.
        List<EditionPattern> relavantPatterns = new ArrayList<EditionPattern>();
        for (EditionPattern pattern : outlet.getEditionPatterns()) {
            if (pattern.getDay() == date.get(java.util.Calendar.DAY_OF_WEEK)) {
                boolean add = true;
                for (Edition ex : editions) {
                    if (ex.getPublicationDate().get(
                            java.util.Calendar.HOUR_OF_DAY) == pattern.
                            getStartHour() && ex.getPublicationDate().get(
                            java.util.Calendar.MINUTE)
                            == pattern.getStartMinute()) {
                        add = false;
                    }
                }

                if (add) {
                    relavantPatterns.add(pattern);
                }
            }
        }

        for (EditionPattern relavantPattern : relavantPatterns) {
            Calendar start = (Calendar) date.clone();
            start.set(Calendar.HOUR_OF_DAY, relavantPattern.getStartHour());
            start.set(Calendar.MINUTE, relavantPattern.getStartMinute());
            start.set(Calendar.SECOND, 0);
            start.set(Calendar.MILLISECOND, 0);

            Calendar end = (Calendar) start.clone();
            end.add(Calendar.HOUR_OF_DAY, relavantPattern.getEndHour());
            end.add(Calendar.MINUTE, relavantPattern.getEndMinute());
            end.set(Calendar.SECOND, 0);
            end.set(Calendar.MILLISECOND, 0);

            Edition edition = new Edition();
            edition.setOutlet(outlet);
            edition.setPublicationDate(start);
            edition.setExpirationDate(end);

            if (start != null) {
                Calendar closeDate = (Calendar) start.clone();
                closeDate.add(Calendar.HOUR_OF_DAY,
                        relavantPattern.getCloseHour());
                closeDate.add(Calendar.MINUTE, relavantPattern.getCloseMinute());
                edition.setCloseDate(closeDate.getTime());
                if (closeDate.before(Calendar.getInstance())) {
                    edition.setOpen(false);
                } else {
                    edition.setOpen(true);
                }

            } else {
                edition.setOpen(true);
            }

            edition.setVolume(0);
            edition.setNumber(0);
            editions.add(edition);
        }

        Collections.sort(editions, new BeanComparator("publicationDate"));

        return editions;
    }

    @Override
    public List<EditionView> findEditionViewsByDate(Long outletId, Date date,
            boolean includeOpen, boolean includeClosed) {
        // 1. Check if there are any editions in the database matching the date and outlet
        Calendar startDate = CalendarUtils.getStartOfDay(date);
        Calendar endDate = CalendarUtils.getEndOfDay(date);

        Outlet outlet;
        try {
            outlet = daoService.findById(Outlet.class, outletId);
        } catch (DataNotFoundException ex) {
            LOG.log(Level.SEVERE, ex.getMessage());
            return Collections.EMPTY_LIST;
        }

        Map<String, Object> params = QueryBuilder.with("outlet", outlet).and(
                "start_date", startDate).and("end_date", endDate).parameters();
        List<EditionView> editions = daoService.findWithNamedQuery(
                Edition.VIEW_EDITION_PLANNING, params);

        // Load stories for editions
        for (EditionView edition : editions) {
            if (edition.isOpen() && includeOpen || !edition.isOpen()
                    && includeClosed) {
                Map<String, Object> assignmentParams = QueryBuilder.with(
                        "edition", edition.getId()).parameters();
                List<EditionAssignmentView> assignments = daoService.
                        findWithNamedQuery(
                        NewsItemPlacement.VIEW_EDITION_ASSIGNMENTS,
                        assignmentParams);
                edition.setAssignments(assignments);
            }
        }


        // 2. Generate editions based on pattern.
        List<EditionPattern> relavantPatterns = new ArrayList<EditionPattern>();
        for (EditionPattern pattern : outlet.getEditionPatterns()) {
            if (pattern.isMatchDay(date)) {
                boolean add = true;
                for (EditionView ev : editions) {
                    if (pattern.isMatchPublicationDate(ev.getPublicationDate())) {
                        add = false;
                    }
                }

                if (add) {
                    relavantPatterns.add(pattern);
                }
            }
        }

        for (EditionPattern relavantPattern : relavantPatterns) {
            Calendar start = Calendar.getInstance();
            start.setTime(date);

            start.set(Calendar.HOUR_OF_DAY, relavantPattern.getStartHour());
            start.set(Calendar.MINUTE, relavantPattern.getStartMinute());
            start.set(Calendar.SECOND, 0);
            start.set(Calendar.MILLISECOND, 0);

            Calendar end = Calendar.getInstance();
            end.setTime(date);
            end.add(Calendar.HOUR_OF_DAY, relavantPattern.getEndHour());
            end.add(Calendar.MINUTE, relavantPattern.getEndMinute());
            end.set(Calendar.SECOND, 0);
            end.set(Calendar.MILLISECOND, 0);

            EditionView edition = new EditionView();
            edition.setOutletId(outletId);
            edition.setOutletName(outlet.getTitle());
            edition.setPublicationDate(start.getTime());
            edition.setExpirationDate(end.getTime());

            if (start != null) {
                Calendar closeDate = (Calendar) start.clone();
                closeDate.add(Calendar.HOUR_OF_DAY,
                        relavantPattern.getCloseHour());
                closeDate.add(Calendar.MINUTE, relavantPattern.getCloseMinute());
                edition.setCloseDate(closeDate.getTime());
                if (closeDate.before(Calendar.getInstance())) {
                    edition.setOpen(false);
                } else {
                    edition.setOpen(true);
                }

            } else {
                edition.setOpen(true);
            }

            editions.add(edition);
        }

        Collections.sort(editions, new BeanComparator("publicationDate"));

        return editions;
    }

    @Override
    public List<EditionCandidate> findEditionCandidatesByDate(Outlet outlet,
            Calendar date, boolean includeClosed) {
        List<Edition> editions = findEditionsByDate(outlet, date);
        List<EditionCandidate> candidates = new ArrayList<EditionCandidate>();

        for (Edition edition : editions) {
            if (includeClosed || edition.isOpen()) {
                candidates.add(new EditionCandidate(edition));
            }
        }

        return candidates;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Edition updateEdition(Edition edition) {
        return daoService.update(edition);
    }

    /**
     * Updates an existing {@link Edition} in the database.
     *
     * @param editionId       Unique identifier of the {@link Edition} to update
     * @param open            Determines if the {@link Edition} should be open
     * @param publicationDate New publication date of the {@link Edition}
     * @param expirationDate  New expiration date of the {@link Edition}
     * @param closeDate       New close date of the {@link Edition}
     * @return Update {@link Edition}
     * @throws DataNotFoundException If no {@link Edition} exist with the given {@code editionId}
     */
    @Override
    public Edition updateEdition(Long editionId, Boolean open,
            Date publicationDate, Date expirationDate, Date closeDate) throws
            DataNotFoundException {
        Edition e = daoService.findById(Edition.class, editionId);
        e.setOpen(open);

        Calendar pubDate = Calendar.getInstance();
        pubDate.setTime(publicationDate);
        e.setPublicationDate(pubDate);

        Calendar expDate = Calendar.getInstance();
        expDate.setTime(expirationDate);
        e.setExpirationDate(expDate);

        e.setCloseDate(closeDate);

        return daoService.update(e);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void deleteEdition(Long id) {
        daoService.delete(Edition.class, id);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Section findSectionById(Long id) throws DataNotFoundException {
        return daoService.findById(Section.class, id);
    }

    @Override
    public List<Section> findSectionByName(Long outletId, String sectionName)
            throws DataNotFoundException {
        Outlet outlet = findOutletById(outletId);
        Map<String, Object> params = QueryBuilder.with("outlet", outlet).and(
                "sectionName", sectionName).parameters();
        return daoService.findWithNamedQuery(Section.FIND_BY_OUTLET_AND_NAME,
                params);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Section createSection(Section section) {
        return daoService.create(section);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Section updateSection(Section section) {
        return daoService.update(section);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteSection(Long id) throws EntityReferenceException {
        // TODO: Check if in use
        daoService.delete(Section.class, id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OutletEditionAction createOutletAction(OutletEditionAction action) {
        return daoService.create(action);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OutletEditionAction updateOutletAction(OutletEditionAction action) {
        return daoService.update(action);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteOutletActionById(Long id) {
        daoService.delete(OutletEditionAction.class, id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteEditionPatternById(Long id) {
        daoService.delete(EditionPattern.class, id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EditionPattern createEditionPattern(EditionPattern editionPattern) {
        return daoService.create(editionPattern);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EditionPattern updateEditionPattern(EditionPattern editionPattern) {
        return daoService.update(editionPattern);
    }

    /**
     * Schedules the execution of an edition action.
     *
     * @param editionId Unique identifier of the edition
     * @param actionId  Unique identifier of the action
     */
    @Override
    public void scheduleAction(Long editionId, Long actionId) {
        Connection connection = null;
        try {
            connection = jmsConnectionFactory.createConnection();
            Session session = connection.createSession(true,
                    Session.AUTO_ACKNOWLEDGE);

            MessageProducer producer = session.createProducer(destination);

            MapMessage message = session.createMapMessage();
            message.
                    setLongProperty(EditionServiceMessageBean.Property.EDITION_ID.
                    name(), editionId);
            message.
                    setLongProperty(EditionServiceMessageBean.Property.ACTION_ID.
                    name(), actionId);
            producer.send(message);

            session.close();
            connection.close();
        } catch (JMSException ex) {
            Logger.getLogger(OutletFacadeBean.class.getName()).log(Level.SEVERE,
                    null, ex);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) {
                }
            }
        }
    }

    /**
     * Schedules the execution of all actions of an edition
     *
     * @param editionId Unique identifier of the edition
     */
    @Override
    public void scheduleActions(Long editionId) {
        Connection connection = null;
        try {
            Edition edition = daoService.findById(Edition.class, editionId);
            List<OutletEditionAction> actions = edition.getOutlet().
                    getAutomaticEditionActions();

            connection = jmsConnectionFactory.createConnection();
            Session session = connection.createSession(true,
                    Session.AUTO_ACKNOWLEDGE);

            MessageProducer producer = session.createProducer(destination);

            for (OutletEditionAction action : actions) {
                MapMessage message = session.createMapMessage();
                message.
                        setLongProperty(EditionServiceMessageBean.Property.EDITION_ID.
                        name(), editionId);
                message.
                        setLongProperty(EditionServiceMessageBean.Property.ACTION_ID.
                        name(), action.getId());
                producer.send(message);
            }
            session.close();
            connection.close();

        } catch (DataNotFoundException ex) {
            LOG.log(Level.WARNING, ex.getMessage());
        } catch (JMSException ex) {
            Logger.getLogger(OutletFacadeBean.class.getName()).log(Level.SEVERE,
                    null, ex);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) {
                }
            }
        }
    }

    /**
     * Schedules the execution of all actions of {@link NewsItemPlacement} in
     * an {@link Edition}
     *
     * @param editionId           Unique identifier of the {@link Edition}
     * @param newsItemPlacementId Unique identifier of the {@link NewsItemPlacement}
     */
    @Override
    public void scheduleNewsItemPlacementActions(Long editionId,
            Long newsItemPlacementId) {

        Connection connection = null;
        try {
            Edition edition = daoService.findById(Edition.class, editionId);
            Long outletId = edition.getOutlet().getId();

            List<OutletActionView> actions =
                    findOutletPlacementActions(outletId);

            connection = jmsConnectionFactory.createConnection();
            Session session = connection.createSession(true,
                    Session.AUTO_ACKNOWLEDGE);

            MessageProducer producer = session.createProducer(destination);

            for (OutletActionView action : actions) {
                MapMessage message = session.createMapMessage();
                message.
                        setLongProperty(EditionServiceMessageBean.Property.EDITION_ID.
                        name(), editionId);
                message.
                        setLongProperty(EditionServiceMessageBean.Property.ACTION_ID.
                        name(), action.getId());
                message.
                        setLongProperty(EditionServiceMessageBean.Property.NEWS_ITEM_PLACEMENT_ID.
                        name(), newsItemPlacementId);
                message.
                        setString(EditionServiceMessageBean.Property.USER_ACCOUNT_ID.
                        name(), ctx.getCallerPrincipal().getName());
                producer.send(message);
            }
            session.close();
            connection.close();

        } catch (DataNotFoundException ex) {
            LOG.log(Level.WARNING, ex.getMessage());
        } catch (JMSException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) {
                }
            }
        }
    }

    /**
     * Schedules the execution of a single action on a {@link NewsItemPlacement}
     * in an {@link Edition}
     *
     * @param editionId           Unique identifier of the {@link Edition}
     * @param actionId            Unique identifier of the {@link OutletEditionAction}
     * @param newsItemPlacementId Unique identifier of the {@link NewsItemPlacement}
     */
    @Override
    public void scheduleNewsItemPlacementAction(Long editionId, Long actionId,
            Long newsItemPlacementId) {
        LOG.log(Level.INFO, "Called by user: {0}", ctx.getCallerPrincipal().
                getName());
        Connection connection = null;
        try {
            connection = jmsConnectionFactory.createConnection();
            Session session = connection.createSession(true,
                    Session.AUTO_ACKNOWLEDGE);

            MessageProducer producer = session.createProducer(destination);

            MapMessage message = session.createMapMessage();
            message.
                    setLongProperty(EditionServiceMessageBean.Property.EDITION_ID.
                    name(), editionId);
            message.
                    setLongProperty(EditionServiceMessageBean.Property.ACTION_ID.
                    name(), actionId);
            message.
                    setLongProperty(EditionServiceMessageBean.Property.NEWS_ITEM_PLACEMENT_ID.
                    name(), newsItemPlacementId);
            message.
                    setStringProperty(EditionServiceMessageBean.Property.USER_ACCOUNT_ID.
                    name(), ctx.getCallerPrincipal().getName());
            producer.send(message);

            session.close();
            connection.close();

        } catch (JMSException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) {
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void scheduleActionsOnOutlet(Long outletId) {
        Connection connection = null;
        try {
            Outlet outlet = findOutletById(outletId);

            List<Edition> editions = findEditionsByStatus(false, outlet);

            connection = jmsConnectionFactory.createConnection();
            Session session = connection.createSession(true,
                    Session.AUTO_ACKNOWLEDGE);
            MessageProducer producer = session.createProducer(destination);

            for (Edition edition : editions) {

                List<OutletEditionAction> actions = edition.getOutlet().
                        getAutomaticEditionActions();

                for (OutletEditionAction action : actions) {
                    MapMessage message = session.createMapMessage();
                    message.
                            setLongProperty(EditionServiceMessageBean.Property.EDITION_ID.
                            name(), edition.getId());
                    message.
                            setLongProperty(EditionServiceMessageBean.Property.ACTION_ID.
                            name(), action.getId());
                    producer.send(message);
                }
            }
            session.close();
            connection.close();

        } catch (DataNotFoundException ex) {
            LOG.log(Level.WARNING, ex.getMessage());
        } catch (JMSException ex) {
            Logger.getLogger(OutletFacadeBean.class.getName()).log(Level.SEVERE,
                    null, ex);
        } finally {
            if (connection != null) {
                try {

                    connection.close();
                } catch (Exception e) {
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void scheduleActionOnOutlet(Long outletId, Long actionId) {
        Connection connection = null;

        try {
            connection = jmsConnectionFactory.createConnection();
            Session session = connection.createSession(true,
                    Session.AUTO_ACKNOWLEDGE);
            MessageProducer producer = session.
                    createProducer(outletServiceQueue);

            MapMessage message = session.createMapMessage();
            message.setLongProperty(OutletServiceMessageBean.Property.OUTLET_ID.
                    name(), outletId);
            message.setLongProperty(OutletServiceMessageBean.Property.ACTION_ID.
                    name(), actionId);
            producer.send(message);

            session.close();
            connection.close();
        } catch (JMSException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) {
                }
            }
        }
    }

    @Override
    public List<OutletActionView> findOutletActions(Long id) throws
            DataNotFoundException {
        Outlet o = findOutletById(id);
        List<OutletEditionAction> actions = o.getEditionActions();
        List<OutletActionView> outletActions = new ArrayList<OutletActionView>();
        for (OutletEditionAction action : actions) {
            try {
                if (action.getAction().isSupportEditionExecute()) {
                    String actionName = action.getAction().getName();

                    OutletActionView a = new OutletActionView(action.getId(),
                            action.getLabel(), actionName,
                            action.isManualAction());
                    outletActions.add(a);
                }
            } catch (EditionActionException ex) {
                LOG.log(Level.SEVERE,
                        "Could not extract information about OutletEditionAction",
                        ex);

            }
        }
        return outletActions;
    }

    /**
     * Gets a {@link List} of actions that can be executed on
     * {@link NewsItemPlacement}s of an {@link Outlet}.
     *
     * @param id Unique identifier of the {@link Outlet}
     * @return {@link List} of actions that can be executed on
     *         {@link NewsItemPlacement}s
     * @throws DataNotFoundException If the {@link Outlet} does not exist
     *
     */
    @Override
    public List<OutletActionView> findOutletPlacementActions(Long id) throws
            DataNotFoundException {
        Outlet o = findOutletById(id);
        List<OutletEditionAction> actions = o.getEditionActions();
        List<OutletActionView> outletActions = new ArrayList<OutletActionView>();
        for (OutletEditionAction action : actions) {
            try {
                if (action.getAction().isSupportPlacementExecute()) {
                    String actionName = action.getAction().getName();

                    OutletActionView a = new OutletActionView(action.getId(),
                            action.getLabel(), actionName,
                            action.isManualAction());
                    outletActions.add(a);
                }
            } catch (EditionActionException ex) {
                LOG.log(Level.SEVERE,
                        "Could not extract information about OutletEditionAction",
                        ex);

            }

        }
        return outletActions;
    }
}
