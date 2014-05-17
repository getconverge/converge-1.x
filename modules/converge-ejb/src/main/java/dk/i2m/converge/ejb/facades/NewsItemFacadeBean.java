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
import dk.i2m.converge.core.content.*;
import dk.i2m.converge.core.content.catalogue.MediaItem;
import dk.i2m.converge.core.logging.LogSeverity;
import dk.i2m.converge.core.plugin.WorkflowAction;
import dk.i2m.converge.core.search.QueueEntryOperation;
import dk.i2m.converge.core.search.QueueEntryType;
import dk.i2m.converge.core.security.SystemPrivilege;
import dk.i2m.converge.core.security.UserAccount;
import dk.i2m.converge.core.security.UserRole;
import dk.i2m.converge.core.utils.BeanComparator;
import dk.i2m.converge.core.views.CurrentAssignment;
import dk.i2m.converge.core.views.InboxView;
import dk.i2m.converge.core.workflow.*;
import dk.i2m.converge.ejb.services.*;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.*;
import javax.persistence.OptimisticLockException;

/**
 * Enterprise session bean providing a facade to working with {@link NewsItem}s.
 *
 * @author Allan Lykke Christensen
 */
@Stateless
public class NewsItemFacadeBean implements NewsItemFacadeLocal {

    private static final Logger LOG = Logger.getLogger(NewsItemFacadeBean.class.
            getName());

    @EJB private ConfigurationServiceLocal cfgService;

    @EJB private DaoServiceLocal daoService;

    @EJB private UserFacadeLocal userFacade;

    @EJB private UserServiceLocal userService;

    @EJB private NotificationServiceLocal notificationService;

    @EJB private OutletFacadeLocal outletFacade;

    @EJB private SearchEngineLocal searchEngine;

    @EJB private PluginContextBeanLocal pluginContext;

    @EJB private SystemFacadeLocal systemFacade;

    @Resource private SessionContext ctx;

    /**
     * Starts a new {@link NewsItem}.
     *
     * @param newsItem {@link NewsItem} to start.
     * @return Started {@link NewsItem}
     * @throws WorkflowStateTransitionException If the workflow could not be started for the
     * <code>newsItem</code>
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public NewsItem start(NewsItem newsItem) throws
            WorkflowStateTransitionException {
        if (newsItem == null) {
            throw new WorkflowStateTransitionException("NewsItem not available");
        }

        if (newsItem.getId() != null) {
            throw new DuplicateExecutionException("NewsItem #"
                    + newsItem.getId() + " already started");
        }

        if (newsItem.getOutlet() == null) {
            throw new WorkflowStateTransitionException("Outlet must be selected");
        }


        WorkflowState startState = newsItem.getOutlet().getWorkflow().
                getStartState();
        boolean actorSet = false;
        UserRole requiredRole = startState.getActorRole();
        for (NewsItemActor actor : newsItem.getActors()) {
            if (actor.getRole().equals(requiredRole)) {
                actorSet = true;
                break;
            }
        }

        if (!actorSet) {
            throw new MissingActorException("Actor with role " + requiredRole.
                    getName() + " is missing", requiredRole);
        }

        try {
            daoService.findById(NewsItem.class, newsItem.getId());
            throw new DuplicateExecutionException("NewsItem #"
                    + newsItem.getId() + " already exist");
        } catch (DataNotFoundException ex) {
        }

        String uid = ctx.getCallerPrincipal().getName();
        UserAccount ua = null;
        try {
            ua = userService.findById(uid);
        } catch (Exception ex) {
            throw new WorkflowStateTransitionException(
                    "Could not resolve transition initator", ex);
        }

        try {
            Calendar now = Calendar.getInstance();

            Outlet outlet = newsItem.getOutlet();

            // Ensure that the outlet is set
//            if (outlet != null) {
//                WorkflowState startState = outlet.getWorkflow().getStartState();
            WorkflowStateTransition transition = new WorkflowStateTransition(
                    newsItem, now, startState, ua);
            transition.setStoryVersion("");
            transition.setComment("");
            newsItem.getHistory().add(transition);
            newsItem.setCurrentState(startState);
//            }

            newsItem.setUpdated(now);
            newsItem.setCreated(now);
            newsItem.setPrecalculatedWordCount(newsItem.getWordCount());
            newsItem.setPrecalculatedCurrentActor(newsItem.getCurrentActor());

            newsItem = daoService.create(newsItem);

            List<UserAccount> usersToNotify = new ArrayList<UserAccount>();
            WorkflowState state = newsItem.getCurrentState();

            switch (state.getPermission()) {
                case USER:
                    for (NewsItemActor actor : newsItem.getActors()) {
                        if (actor.getRole().equals(state.getActorRole())) {
                            usersToNotify.add(actor.getUser());
                        }
                    }
                    break;
                case GROUP:
                    for (UserAccount actor : userService.findAll()) {
                        if (actor.getUserRoles().contains(state.getActorRole())) {
                            usersToNotify.add(actor);
                        }
                    }
                    break;
            }

            for (UserAccount userToNotify : usersToNotify) {
                if (!userToNotify.equals(ua)) {
                    String msgPattern = cfgService.getMessage(
                            "notification_MSG_STORY_ASSIGNED");
                    SimpleDateFormat sdf = new SimpleDateFormat(cfgService.
                            getMessage("FORMAT_SHORT_DATE_AND_TIME"));
                    sdf.setTimeZone(userToNotify.getTimeZone());

                    String date = sdf.format(newsItem.getDeadline().getTime());

                    Object[] args = new Object[]{newsItem.getTitle(), date, ua.
                        getFullName()};
                    String msg = MessageFormat.format(msgPattern, args);

                    notificationService.create(userToNotify, msg);
                }
            }
            return newsItem;

        } catch (Exception ex) {
            throw new WorkflowStateTransitionException(ex);
        }
    }

    /**
     * Promotes the {@link NewsItem} in the workflow.
     *
     * @param newsItem {@link NewsItem} to promote
     * @param step     Unique identifier of the next step
     * @param comment  Comment from the sender
     * @return Promoted {@link NewsItem}
     * @throws WorkflowStateTransitionException If the next step is not legal
     */
    @Override
    public NewsItem step(NewsItem newsItem, Long step, String comment) throws
            WorkflowStateTransitionException {

        // Get current user
        String uid = ctx.getCallerPrincipal().getName();
        UserAccount ua = null;
        try {
            ua = userService.findById(uid);
            pluginContext.setCurrentUserAccount(ua);
        } catch (Exception ex) {
            throw new WorkflowStateTransitionException(
                    "Could not resolve transition initator", ex);
        }

        // Log the workflow step
        pluginContext.log(LogSeverity.INFO, "Promoting news item #{0} to {1}",
                new Object[]{newsItem.getId(), step}, newsItem, newsItem.getId());

        Calendar now = Calendar.getInstance();

        WorkflowStep transitionStep;
        try {
            transitionStep = daoService.findById(WorkflowStep.class, step);
        } catch (DataNotFoundException ex) {
            throw new WorkflowStateTransitionException("Transition (WorkflowStep) #"
                    + step + " does not exist", ex);
        }

        WorkflowState nextState = transitionStep.getToState();

        // Checking validity of step
        WorkflowState state = newsItem.getCurrentState();
        boolean legalStep = false;
        for (WorkflowStep nextWorkflowStep : state.getNextStates()) {

            if (nextWorkflowStep.getToState().equals(nextState)) {

                boolean isInRole = !Collections.disjoint(nextWorkflowStep.
                        getValidFor(), ua.getUserRoles());

                if (nextWorkflowStep.isValidForAll() || isInRole) {
                    legalStep = true;
                }
                break;
            }
        }

        if (!legalStep) {
            throw new WorkflowStateTransitionException("Illegal transition from "
                    + state.getId() + " to " + nextState.getId());
        }

        WorkflowStateTransition transition = new WorkflowStateTransition(
                newsItem, now, nextState, ua);
        transition.setStoryVersion(newsItem.getStory());
        transition.setHeadlineVersion(newsItem.getTitle());
        transition.setBriefVersion(newsItem.getBrief());
        transition.setComment(comment);
        transition.setSubmitted(transitionStep.isTreatAsSubmitted());
        // Strip unwanted characters
        newsItem.setStory(newsItem.getStory().replaceAll("\\p{Cntrl}", " "));
        newsItem.setCurrentState(nextState);
        newsItem.getHistory().add(transition);
        newsItem.setUpdated(now);
        newsItem.setPrecalculatedWordCount(newsItem.getWordCount());
        newsItem.setPrecalculatedCurrentActor(newsItem.getCurrentActor());

        try {
            newsItem = checkin(newsItem);
        } catch (LockingException ex) {
            throw new WorkflowStateTransitionException(ex);
        }

        // Actions
        pluginContext.log(LogSeverity.INFO, "Executing workflow step actions",
                newsItem, newsItem.getId());
        //LOG.log(Level.INFO, "Executing workflow step actions");

        for (WorkflowStepAction action : transitionStep.getActions()) {
            try {
                WorkflowAction act = action.getAction();
                act.execute(pluginContext, newsItem, action, ua);
            } catch (WorkflowActionException ex) {
                //LOG.log(Level.SEVERE, "Could not execute action {0}", action.getLabel());
                pluginContext.log(LogSeverity.SEVERE,
                        "Could not execute action {0}", new Object[]{action.
                            getLabel()}, newsItem, newsItem.getId());
            }
        }

        return newsItem;
    }

    /** {@inheritDoc} */
    @Override
    public NewsItem step(NewsItem newsItem, WorkflowState state, String comment)
            throws WorkflowStateTransitionException {
        LOG.log(Level.INFO, "Promoting NewsItem #{0} from {0} to {1}",
                new Object[]{newsItem.getId(), newsItem.getCurrentState().
                    getName(), state.getName()});

        String uid = ctx.getCallerPrincipal().getName();
        UserAccount ua = null;
        try {
            ua = userService.findById(uid);
        } catch (Exception ex) {
            throw new WorkflowStateTransitionException(
                    "Could not resolve transition initator", ex);
        }

        Calendar now = Calendar.getInstance();

        WorkflowState nextState;
        try {
            nextState = daoService.findById(WorkflowState.class, state.getId());
        } catch (DataNotFoundException ex) {
            throw new WorkflowStateTransitionException("Invalid workflow state",
                    ex);
        }

        WorkflowStateTransition transition = new WorkflowStateTransition(
                newsItem, now, nextState, ua);
        transition.setStoryVersion(newsItem.getStory());
        transition.setHeadlineVersion(newsItem.getTitle());
        transition.setBriefVersion(newsItem.getBrief());
        transition.setComment(comment);
        newsItem.setCurrentState(nextState);
        newsItem.getHistory().add(transition);
        newsItem.setUpdated(now);
        try {
            newsItem = checkin(newsItem);
        } catch (LockingException ex) {
            throw new WorkflowStateTransitionException(ex);
        }

        return newsItem;
    }

    /**
     * Finds the current assignments of a given user.
     * <p/>
     * @param username
* Username of the user
     * @return {@link List} of current assignments
     */
    @Override
    public List<CurrentAssignment> findCurrentAssignments(String username) {
        try {
            UserAccount ua = userFacade.findById(username);
            Map params = QueryBuilder.with("user", ua).and("permission",
                    WorkflowStatePermission.GROUP).parameters();
            return daoService.findWithNamedQuery(
                    NewsItem.VIEW_CURRENT_ASSIGNMENTS, params);
        } catch (DataNotFoundException ex) {
            LOG.log(Level.WARNING, "Specified user {0} is unknown", username);
            return Collections.EMPTY_LIST;
        }
    }

    /**
     * Gets items in the inbox for a particular user.
     * <p/>
     * @param username
* Username of the {@link UserAccount}
     * @param start
   * First record to retrieve
     * @param limit
   * Number of records to retrieve
     * @return
     */
    @Override
    public List<InboxView> findInbox(String username, int start, int limit) {
        try {
            UserAccount ua = userFacade.findById(username);
            Map params = QueryBuilder.with("user", ua).and("permission",
                    WorkflowStatePermission.GROUP).parameters();
            return daoService.findWithNamedQuery(NewsItem.VIEW_INBOX, params,
                    start, limit);
        } catch (DataNotFoundException ex) {
            LOG.log(Level.SEVERE, null, ex);
            return Collections.EMPTY_LIST;
        }
    }

    /**
     * Gets items in the inbox for a particular user.
     * <p/>
     * @param username
* Username of the {@link UserAccount}
     * @return {@link List} of inbox items
     */
    @Override
    public List<InboxView> findInbox(String username) {
        try {
            UserAccount ua = userFacade.findById(username);
            Map params = QueryBuilder.with("user", ua).and("permission",
                    WorkflowStatePermission.GROUP).parameters();
            return daoService.findWithNamedQuery(NewsItem.VIEW_INBOX, params);
        } catch (DataNotFoundException ex) {
            LOG.log(Level.SEVERE, null, ex);
            return Collections.EMPTY_LIST;
        }
    }

    /** {@inheritDoc } */
    @Override
    public List<NewsItem> findByActiveUser(String username) {
        List<NewsItem> items = new ArrayList<NewsItem>();

        try {
            UserAccount ua = userFacade.findById(username);

            // Go through each privileged outlet
            List<Outlet> outlets = ua.getPrivilegedOutlets();

            for (Outlet outlet : outlets) {

                // Look at each story in the outlet
                Map params = QueryBuilder.with("outlet", outlet).parameters();
                List<NewsItem> itemsInOutlet =
                        daoService.findWithNamedQuery(NewsItem.FIND_BY_OUTLET,
                        params);

                for (NewsItem ni : itemsInOutlet) {

                    WorkflowState state = ni.getCurrentState();

                    // Ignore completed and trashed items
                    if (!state.isWorkflowTrash() && !state.isWorkflowEnd()) {

                        // Check if the NewsItem is currently open for a group or
                        // for a group attached to the story
                        switch (state.getPermission()) {
                            case GROUP:
                                if (ua.getUserRoles().contains(state.
                                        getActorRole())) {
                                    items.add(ni);
                                }
                                break;

                            case USER:
                                // Check all the actors for the news item
                                for (NewsItemActor actor : ni.getActors()) {
                                    // If the actor has the role of the state, he is a current actor
                                    if (actor.getRole().equals(state.
                                            getActorRole()) && actor.getUser().
                                            equals(ua)) {
                                        items.add(ni);
                                    }
                                }
                                break;
                        }
                    }
                }
            }
        } catch (DataNotFoundException ex) {
            LOG.log(Level.WARNING, "Unknown user", ex);
        }
        return items;
    }

    @Override
    public List<InboxView> findOutletBox(String username, Outlet outlet) {
        try {
            UserAccount ua = userFacade.findById(username);
            Map params = QueryBuilder.with("outlet", outlet).and("user", ua).
                    parameters();
            return daoService.findWithNamedQuery(NewsItem.VIEW_OUTLET_BOX,
                    params);
        } catch (DataNotFoundException ex) {
            LOG.log(Level.WARNING, "Could not find user {0}", username);
            return Collections.EMPTY_LIST;
        }
    }

    @Override
    public List<InboxView> findOutletBox(String username, Outlet outlet,
            WorkflowState state) {
        try {
            UserAccount ua = userFacade.findById(username);
            Map params = QueryBuilder.with("outlet", outlet).and("state", state).
                    and("user", ua).parameters();
            return daoService.findWithNamedQuery(NewsItem.VIEW_OUTLET_BOX_STATE,
                    params);
        } catch (DataNotFoundException ex) {
            LOG.log(Level.WARNING, "Could not find user {0}", username);
            return Collections.EMPTY_LIST;
        }
    }

    @Override
    public List<InboxView> findOutletBox(String username, Outlet outlet,
            WorkflowState state, int start, int results) {
        try {
            UserAccount ua = userFacade.findById(username);
            Map params = QueryBuilder.with("outlet", outlet).and("state", state).
                    and("user", ua).parameters();
            return daoService.findWithNamedQuery(NewsItem.VIEW_OUTLET_BOX_STATE,
                    params, start, results);
        } catch (DataNotFoundException ex) {
            LOG.log(Level.WARNING, "Could not find user {0}", username);
            return Collections.EMPTY_LIST;
        }
    }

    /**
     * Permanently remove {@link NewsItem}s in the trash state by a given user.
     *
     * @param username Username of the {@link UserAccount}
     * @return Number of {@link NewsItem}s deleted
     */
    @Override
    public int emptyTrash(String username) {
        int deleted = 0;
        try {
            UserAccount ua = userFacade.findById(username);
            Map<String, Object> params = QueryBuilder.with("user", ua).
                    parameters();
            List<NewsItem> newsItems = daoService.findWithNamedQuery(
                    NewsItem.FIND_TRASH, params);

            for (NewsItem item : newsItems) {
                if (isOriginalOwner(item, username)) {
                    deleteNewsItem(item.getId(), false);
                    deleted++;
                }
            }
        } catch (DataNotFoundException ex) {
            LOG.log(Level.WARNING, "Unknown user #{0}", new Object[]{username});
        }

        return deleted;
    }

    /**
     * Determine if the given user is the original owner of the
     * given {@link NewsItem}.
     * <p/>
     * @param item     {@link NewsItem} to check
     * @param username Username of the {@link UserAccount} to check
     * @return {@code true} if {@code username} is the original owner of {@code item}
     */
    private boolean isOriginalOwner(NewsItem item, String username) {
        WorkflowState start = item.getOutlet().getWorkflow().getStartState();
        UserRole role = start.getActorRole();
        UserAccount user;
        try {
            user = userService.findById(username);
        } catch (Exception ex) {
            return false;
        }

        for (NewsItemActor nia : item.getActors()) {
            if (nia.getRole().equals(role) && nia.getUser().equals(user)) {
                return true;
            }
        }

        return false;
    }

    /** {@inheritDoc } */
    @Override
    public List<NewsItem> findByStateAndOutlet(WorkflowState state,
            Outlet outlet) {
        Map params = QueryBuilder.with("outlet", outlet).and("state", state).
                parameters();
        return daoService.findWithNamedQuery(NewsItem.FIND_BY_OUTLET_AND_STATE,
                params);
    }

    /** {@inheritDoc } */
    @Override
    public List<NewsItem> findByStateAndOutlet(String stateName, Outlet outlet) {
        Map params = QueryBuilder.with("outlet", outlet).and("stateName",
                stateName).parameters();
        return daoService.findWithNamedQuery(
                NewsItem.FIND_BY_OUTLET_AND_STATE_NAME, params);
    }

    /** {@inheritDoc } */
    @Override
    public List<NewsItem> findByStateAndOutlet(WorkflowState state,
            Outlet outlet, int start, int results) {
        Map params = QueryBuilder.with("outlet", outlet).and("state", state).
                parameters();
        return daoService.findWithNamedQuery(NewsItem.FIND_BY_OUTLET_AND_STATE,
                params, start, results);
    }

    /** {@inheritDoc } */
    @Override
    public boolean isNewsItemPublished(final Long newsItemId) throws
            DataNotFoundException {
        NewsItem newsItem = findNewsItemById(newsItemId);

        if (newsItem.getCurrentState().equals(newsItem.getOutlet().getWorkflow().
                getEndState())) {
            return true;
        } else {
            return false;
        }
    }

    /** {@inheritDoc } */
    @Override
    public List<NewsItem> findAssignmentsByOutlet(Outlet selectedOutlet) {
        Map<String, Object> params = QueryBuilder.with("outlet", selectedOutlet).
                parameters();
        return daoService.findWithNamedQuery(NewsItem.FIND_ASSIGNMENTS_BY_OUTLET,
                params);
    }

    /** {@inheritDoc } */
    @Override
    public boolean deleteNewsItem(Long id) {
        try {
            NewsItem item = daoService.findById(NewsItem.class, id);
            Workflow workflow = item.getOutlet().getWorkflow();
            WorkflowState trashState = workflow.getTrashState();
            step(item, trashState, "");
            return true;
        } catch (DataNotFoundException ex) {
            LOG.log(Level.WARNING, "Could not find news item to delete. {0}",
                    ex.getMessage());
            LOG.log(Level.FINE, "", ex);
            return false;
        } catch (WorkflowStateTransitionException ex) {
            LOG.log(Level.WARNING, "Could not delete news item. {0}", ex.
                    getMessage());
            LOG.log(Level.FINE, "", ex);
            return false;
        }
    }

    /** {@inheritDoc } */
    @Override
    public boolean deleteNewsItem(Long id, boolean safe) {
        if (safe) {
            return deleteNewsItem(id);
        } else {
            try {
                NewsItem item = daoService.findById(NewsItem.class, id);
                daoService.delete(NewsItem.class, id);
                // Schedule removal of news item from search engine
                searchEngine.addToIndexQueue(QueueEntryType.NEWS_ITEM, id,
                        QueueEntryOperation.REMOVE);
                return true;
            } catch (DataNotFoundException ex) {
                LOG.log(Level.WARNING, "Could not find news item to delete. {0}",
                        ex.getMessage());
                LOG.log(Level.FINE, "", ex);
                return false;
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public NewsItemActor addActorToNewsItem(NewsItemActor actor) {
        return daoService.create(actor);
    }

    /** {@inheritDoc} */
    @Override
    public NewsItem removeActorFromNewsItem(NewsItemActor actor) {
        Long newsItemId = actor.getNewsItem().getId();
        daoService.delete(NewsItemActor.class, actor.getId());
        try {
            return daoService.findById(NewsItem.class, newsItemId);
        } catch (DataNotFoundException ex) {
            LOG.log(Level.SEVERE, null, ex);
            return null;
        }
    }

    /** {@inheritDoc} */
    @Override
    public ContentItemPermission getPermission(Long newsItemId, String username) {
        try {
            NewsItem item = findNewsItemById(newsItemId);
            UserAccount user = userFacade.findById(username);
            return getPermission(item, user);
        } catch (DataNotFoundException ex) {
            LOG.log(Level.SEVERE, null, ex);
            return ContentItemPermission.UNAUTHORIZED;
        }
    }

    private ContentItemPermission getPermission(NewsItem item, UserAccount user) {
        UserRole currentRole = item.getCurrentState().getActorRole();

        // Super users always have access
        if (user.isPrivileged(SystemPrivilege.SUPER_USER)) {
            return ContentItemPermission.USER;
        }

        if (item.getCurrentState().isGroupPermission()) {
            // NewsItem has been sent to a group - check if the user is part
            // of the group
            if (user.getUserRoles().contains(currentRole)) {
                return ContentItemPermission.ROLE;
            }

        } else {
            // NewsItem has been sent to selected users, check if the user
            // is among the selected users
            for (NewsItemActor actor : item.getActors()) {
                if (actor.getUser().equals(user) && actor.getRole().equals(
                        currentRole)) {
                    return ContentItemPermission.USER;
                }
            }
        }

        // If user is neither in the current role or among the current
        // actors, check if the user is among the other actors
        for (NewsItemActor actor : item.getActors()) {
            if (actor.getUser().equals(user)) {
                return ContentItemPermission.ACTOR;
            }
        }

        // If the user originally assigned the story, he should have access like
        // an actor.
        if (item.isAssigned() && item.getAssignedBy().equals(user)) {
            return ContentItemPermission.USER;
        }

        // If the user is an outlet planner, he has access to the story as an
        // actor
        if (user.isPrivileged(SystemPrivilege.OUTLET_PLANNING)) {
            return ContentItemPermission.ACTOR;
        }

        return ContentItemPermission.UNAUTHORIZED;
    }

    /** {@inheritDoc} */
    @Override
    public List<NewsItem> findVersions(Long newsItemId) {
        try {
            NewsItem newsItem = daoService.findById(NewsItem.class, newsItemId);
            return findVersions(newsItem);
        } catch (DataNotFoundException ex) {
            LOG.log(Level.SEVERE, null, ex);
            return Collections.EMPTY_LIST;
        }
    }

    private List<NewsItem> findVersions(NewsItem newsItem) {
        Map<String, Object> parameters = QueryBuilder.with("newsItem", newsItem).
                parameters();
        return daoService.findWithNamedQuery(NewsItem.FIND_VERSIONS, parameters);
    }

    /** {@inheritDoc } */
    @Override
    public NewsItem save(NewsItem newsItem) throws LockingException {
        try {
            Calendar now = Calendar.getInstance();
            newsItem.setUpdated(now);
            newsItem.setPrecalculatedWordCount(newsItem.getWordCount());
            newsItem.setPrecalculatedCurrentActor(newsItem.getCurrentActor());
            return daoService.update(newsItem);
        } catch (Throwable t) {
            throw new LockingException(t);
        }
    }

    /** {@inheritDoc } */
    @Override
    public NewsItem findNewsItemById(Long id) throws DataNotFoundException {
        return daoService.findById(NewsItem.class, id);
    }

    @Override
    public NewsItemPlacement findNewsItemPlacementById(Long id) throws
            DataNotFoundException {
        return daoService.findById(NewsItemPlacement.class, id);
    }

    /** {@inheritDoc } */
    @Override
    public boolean isCheckedOut(Long id) {
        return !daoService.findWithNamedQuery(NewsItem.FIND_CHECKED_IN_NEWS_ITEM,
                QueryBuilder.with("id", id).parameters()).isEmpty();
    }

    /** {@inheritDoc } */
    @Override
    public NewsItem checkin(NewsItem newsItem) throws LockingException {

        try {
            UserAccount updaterUser = userService.findById(ctx.
                    getCallerPrincipal().getName());
            NewsItem orig =
                    daoService.findById(NewsItem.class, newsItem.getId());

            if (!orig.isLocked()) {
                throw new LockingException(
                        "News Item #" + newsItem.getId()
                        + " is not checked-out and can therefore not be checked-in");
            } else if (orig.isLocked() && !orig.getCheckedOutBy().equals(
                    updaterUser)) {
                throw new LockingException("News Item #" + newsItem.getId()
                        + " is already checked-out by " + orig.getCheckedOutBy());
            }

            String oldBriefing = orig.getAssignmentBriefing();
            Calendar now = Calendar.getInstance();
            newsItem.setUpdated(now);
            newsItem.setCheckedOut(null);
            newsItem.setCheckedOutBy(null);
            newsItem.setPrecalculatedWordCount(newsItem.getWordCount());
            newsItem.setPrecalculatedCurrentActor(newsItem.getCurrentActor());
            NewsItem updated = daoService.update(newsItem);

            // Briefing has changed - notify relevant users
            if (!oldBriefing.equalsIgnoreCase(updated.getAssignmentBriefing())) {

                // Find out which users to notify of the update
                List<UserAccount> usersToNotify = new ArrayList<UserAccount>();
                WorkflowState state = newsItem.getCurrentState();

                // Check if the NewsItem is currently open for a group or
                // for a group attached to the story
                if (state.getPermission().equals(WorkflowStatePermission.GROUP)) {
                    List<UserAccount> users = userService.findAll();
                    for (UserAccount ua : users) {
                        if (ua.getUserRoles().contains(state.getActorRole())) {
                            usersToNotify.add(ua);
                        }
                    }
                } else if (state.getPermission().equals(
                        WorkflowStatePermission.USER)) {

                    // Check all the actors for the news item
                    for (NewsItemActor actor : updated.getActors()) {
                        // If the actor has the role of the state, he is a current actor
                        if (actor.getRole().equals(state.getActorRole())) {
                            usersToNotify.add(actor.getUser());
                        }
                    }
                }

                String msg = cfgService.getMessage(
                        "notification_MSG_STORY_BRIEFING_UPDATED");
                String notifyMsg = MessageFormat.format(msg, newsItem.getTitle(),
                        updaterUser.getFullName());

                for (UserAccount ua : usersToNotify) {
                    notificationService.create(ua, notifyMsg);
                }
            }

            return updated;
        } catch (UserNotFoundException ex) {
            LOG.log(Level.WARNING, "Unknown user", ex);
            throw new LockingException(ex);
        } catch (DirectoryException ex) {
            LOG.log(Level.WARNING, "Could not connect to directory", ex);
            throw new LockingException(ex);

        } catch (DataNotFoundException ex) {
            LOG.log(Level.WARNING, "Unknown entity", ex);
            throw new LockingException(ex);
        }
    }

    /**
     * Checks-out a {@link NewsItem} from the database.
     *
     * @param id Unique identifier of the {@link NewsItem}
     * @return Checked-out {@link NewsItem} in a {@link NewsItemHolder} matching the given {@code id}
     * @throws DataNotFoundException If no {@link NewsItem} could be found with the given {@code id}
     */
    @Override
    public NewsItemHolder checkout(Long id) throws DataNotFoundException {
        LOG.log(Level.INFO, "Checking out news item #{0}", id);
        boolean checkedOut = false;
        boolean readOnly = false;
        boolean pullbackAvailable = false;

        UserAccount user = null;

        try {
            user = userService.findById(ctx.getCallerPrincipal().getName());
            pluginContext.setCurrentUserAccount(user);
        } catch (UserNotFoundException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } catch (DirectoryException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }

        NewsItem newsItem = daoService.findById(NewsItem.class, id);
        ContentItemPermission permission = getPermission(newsItem, user);

        LOG.log(Level.INFO, "Permission of #{0} for {1} is {2}", new Object[]{id,
                    user, permission.toString()});

        if (newsItem.isLocked() && !newsItem.getCheckedOutBy().equals(user)) {
            // The item has been checked out and the check-out user is not the same as the one who has already checked it out
            //LOG.log(Level.INFO, "News Item #{0} is locked by {1}", new Object[]{id, newsItem.getCheckedOutBy()});
            pluginContext.log(LogSeverity.INFO,
                    "News Item #{0} is locked by {1}", new Object[]{id,
                        newsItem.getCheckedOutBy().getFullName()}, newsItem, id);
            checkedOut = false;
            readOnly = true;
        } else if (newsItem.isLocked()
                && newsItem.getCheckedOutBy().equals(user)) {
            //LOG.log(Level.INFO, "News Item #{0} is already locked by {1}", new Object[]{id, newsItem.getCheckedOutBy()});
            pluginContext.log(LogSeverity.INFO,
                    "News Item #{0} is already locked by {1}", new Object[]{id,
                        newsItem.getCheckedOutBy().getFullName()}, newsItem, id);
            // The item has been checked out but the same user asking to check it out again
            checkedOut = true;
            readOnly = false;
        } else {
            //LOG.log(Level.INFO, "News Item #{0} is not locked", new Object[]{id});
            if (permission == ContentItemPermission.USER || permission
                    == ContentItemPermission.ROLE) {
                pluginContext.log(LogSeverity.INFO,
                        "Locking News Item #{0} for {1}", new Object[]{id, user.
                            getFullName()}, newsItem, id);
                //LOG.log(Level.INFO, "Locking News Item #{0} for {1}", new Object[]{id, user});
                // Check-out user is the same as the current user or role of the content item
                newsItem.setCheckedOut(Calendar.getInstance());
                newsItem.setCheckedOutBy(user);
                newsItem = daoService.update(newsItem);
                checkedOut = true;
                readOnly = false;
            } else {
                // Check-out user is an actor of the content item, but not the current actor
                checkedOut = false;
                readOnly = true;
            }
        }

        List<NewsItem> versions = findVersions(newsItem);

        // Determine if pullback is available
        if (newsItem.getLatestTransition().getUser().equals(user) && newsItem.
                getCurrentState().isPullbackEnabled()) {
            pullbackAvailable = true;
        }

        // Determine which fields should be visible
        Map<String, Boolean> fieldVisibility = new HashMap<String, Boolean>();

        List<NewsItemFieldVisible> visibleFields = newsItem.getCurrentState().
                getVisibleFields();

        for (NewsItemField field : NewsItemField.values()) {

            // Assume that the field is not visible
            fieldVisibility.put(field.name(), Boolean.FALSE);

            // Go through visible fields and check if it should be visible
            for (NewsItemFieldVisible visibleField : visibleFields) {
                if (visibleField.getField() == field) {
                    fieldVisibility.put(field.name(), Boolean.TRUE);
                    break;
                }
            }
        }

        return new NewsItemHolder(newsItem, versions, permission, readOnly,
                checkedOut, pullbackAvailable, fieldVisibility);
    }

    /** {@inheritDoc } */
    @Override
    public boolean revokeLock(final Long id) {
        int affected = daoService.executeQuery(NewsItem.REVOKE_LOCK,
                QueryBuilder.with("id", id));

        if (affected > 0) {
            return true;
        } else {
            return false;
        }
    }

    /** {@inheritDoc } */
    @Override
    public int revokeLocks(final String username) {
        try {
            UserAccount ua = userService.findById(username);
            return daoService.executeQuery(NewsItem.REVOKE_LOCKS, QueryBuilder.
                    with("user", ua));
        } catch (UserNotFoundException ex) {
            LOG.log(Level.SEVERE, null, ex);
            return 0;
        } catch (DirectoryException ex) {
            LOG.log(Level.SEVERE, null, ex);
            return 0;
        }
    }

    /** {@inheritDoc } */
    @Override
    public int revokeAllLocks() {
        return daoService.executeQuery(NewsItem.REVOKE_ALL_LOCKS);
    }

    /** {@inheritDoc } */
    @Override
    public void pullback(Long id) throws LockingException,
            WorkflowStateTransitionException {
        LOG.log(Level.INFO, "Pulling back news item #{0}", new Object[]{id});

        try {
            NewsItemHolder nih = checkout(id);
            NewsItem ni = nih.getNewsItem();

            if (ni.isLocked()) {
                throw new LockingException(
                        "News item is locked and can't be pulled back");
            }

            if (!ni.getCurrentState().isPullbackEnabled()) {
                throw new WorkflowStateTransitionException(ni.getCurrentState().
                        getName() + " does not support pullback");
            }

            if (ni.getHistory().size() < 2) {
                throw new WorkflowStateTransitionException(
                        "Not enough history to pullback");
            }

            String uid = ctx.getCallerPrincipal().getName();
            UserAccount ua = null;

            try {
                ua = userService.findById(uid);
            } catch (Exception ex) {
                throw new WorkflowStateTransitionException(
                        "Could not resolve initator", ex);
            }

            Calendar now = Calendar.getInstance();

            Collections.sort(ni.getHistory(), new BeanComparator("timestamp",
                    false));
            WorkflowStateTransition oldTransition = ni.getHistory().get(1);

            WorkflowState nextState = daoService.findById(WorkflowState.class,
                    oldTransition.getState().getId());

            WorkflowStateTransition transition = new WorkflowStateTransition(ni,
                    now, nextState, ua);
            transition.setStoryVersion(ni.getStory());
            transition.setHeadlineVersion(ni.getTitle());
            transition.setBriefVersion(ni.getBrief());
            transition.setComment("");
            ni.setCurrentState(nextState);
            ni.getHistory().add(transition);
            ni.setUpdated(now);

            try {
                if (nih.getPermission() == ContentItemPermission.USER) {
                    ni = checkin(ni);
                } else {
                    ni = save(ni);
                }
            } catch (LockingException ex) {
                throw new WorkflowStateTransitionException(ex);
            }
        } catch (DataNotFoundException ex) {
            throw new LockingException(ex);
        }
    }

    /** {@inheritDoc } */
    @Override
    public MediaItem create(MediaItem mediaItem) {
        mediaItem.setCreated(java.util.Calendar.getInstance());
        return daoService.create(mediaItem);
    }

    /** {@inheritDoc } */
    @Override
    public NewsItemMediaAttachment create(NewsItemMediaAttachment attachment) {
        return daoService.create(attachment);
    }

    /** {@inheritDoc } */
    @Override
    public NewsItemMediaAttachment update(NewsItemMediaAttachment attachment) {
        return daoService.update(attachment);
    }

    /** {@inheritDoc } */
    @Override
    public void deleteMediaAttachmentById(Long id) {
        daoService.delete(NewsItemMediaAttachment.class, id);
    }

    /** {@inheritDoc } */
    @Override
    public NewsItem findNewsItemFromArchive(Long id) throws
            DataNotFoundException {
        NewsItem ni = findNewsItemById(id);

        if (ni.getCurrentState().equals(
                ni.getOutlet().getWorkflow().getEndState())) {
            return ni;
        } else {
            throw new DataNotFoundException();
        }
    }

    @Override
    public NewsItemPlacement addToNextEdition(NewsItem newsItem, Section section)
            throws DataNotFoundException {
        Edition nextEdition = outletFacade.findNextEdition(newsItem.getOutlet());

        if (nextEdition.getId() == null) {
            nextEdition = outletFacade.createEdition(nextEdition);
        }

        NewsItemPlacement placement = new NewsItemPlacement();
        placement.setEdition(nextEdition);
        placement.setSection(section);
        placement.setOutlet(newsItem.getOutlet());
        placement.setNewsItem(newsItem);
        return daoService.create(placement);
    }

    @Override
    public NewsItemPlacement createPlacement(NewsItemPlacement placement) {
        return daoService.create(placement);
    }

    @Override
    public NewsItemPlacement updatePlacement(NewsItemPlacement placement) {
        try {
            return daoService.update(placement);
        } catch (OptimisticLockException ex) {
            LOG.log(Level.WARNING,
                    "OptimsticLockException occured upon updating the NewsItemPlacement #{0}",
                    new Object[]{placement.getId()});
            return placement;
        }
    }

    @Override
    public NewsItemPlacement updatePlacement(Long placementId, Integer start,
            Integer position) {
        try {
            NewsItemPlacement nip;
            nip = daoService.findById(NewsItemPlacement.class, placementId);
            nip.setStart(start);
            nip.setPosition(position);
            return daoService.update(nip);
        } catch (DataNotFoundException ex) {
            return null;
        }
    }

    @Override
    public void deletePlacement(NewsItemPlacement placement) {
        deletePlacementById(placement.getId());
    }

    @Override
    public void deletePlacementById(Long id) {
        daoService.delete(NewsItemPlacement.class, id);
    }

    @Override
    public void updatePrecalculatedFields() {
        for (NewsItem ni : daoService.findAll(NewsItem.class)) {
            ni.setPrecalculatedCurrentActor(ni.getCurrentActor());
            ni.setPrecalculatedWordCount(ni.getWordCount());
        }
    }
}
