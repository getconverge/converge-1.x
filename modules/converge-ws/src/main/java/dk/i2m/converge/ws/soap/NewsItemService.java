/*
 * Copyright (C) 2011 - 2012 Interactive Media Management
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
package dk.i2m.converge.ws.soap;

import dk.i2m.converge.core.DataNotFoundException;
import dk.i2m.converge.core.content.NewsItem;
import dk.i2m.converge.core.content.NewsItemPlacement;
import dk.i2m.converge.core.metadata.Concept;
import dk.i2m.converge.core.security.UserAccount;
import dk.i2m.converge.core.views.InboxView;
import dk.i2m.converge.core.workflow.Edition;
import dk.i2m.converge.core.workflow.Section;
import dk.i2m.converge.ejb.facades.*;
import dk.i2m.converge.ws.model.ModelConverter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;

/**
 * {@link WebService} for retrieving news items.
 *
 * @author Allan Lykke Christensen
 */
@WebService(serviceName = "NewsItemService")
public class NewsItemService {

    private static final Logger LOG = Logger.getLogger(NewsItemService.class.
            getName());

    @EJB private NewsItemFacadeLocal newsItemFacade;

    @EJB private CatalogueFacadeLocal mediaDatabaseFacade;

    @EJB private OutletFacadeLocal outletFacade;

    @EJB private UserFacadeLocal userFacade;

    @EJB private MetaDataFacadeLocal metaDataFacade;

    @Resource private WebServiceContext context;

    /**
     * Starts the workflow of a new {@link dk.i2m.converge.core.content.NewsItem}.
     *
     * @param outletId
* Unique identifier of the Outlet where to place the news item
     * @param title
   * Title of the news item
     * @return Unique identifier of the new news item
     * @throws WorkflowStateTransitionException * If the workflow could not be started
     */
    @WebMethod(operationName = "start")
    public Long start(@WebParam(name = "outletId") Long outletId,
            @WebParam(name = "title") String title) throws
            WorkflowStateTransitionException {
        if (context.getUserPrincipal() == null) {
            throw new WorkflowStateTransitionException(
                    "User is not authenticated");
        }

        String username = context.getUserPrincipal().getName();
        UserAccount userAccount = null;
        try {
            userAccount = userFacade.findById(username);
        } catch (DataNotFoundException ex) {
            throw new WorkflowStateTransitionException(ex);
        }

        dk.i2m.converge.core.workflow.Outlet outlet = null;
        try {
            outlet = outletFacade.findOutletById(outletId);
        } catch (DataNotFoundException ex) {
            throw new WorkflowStateTransitionException(ex);
        }

        if (!outlet.isValid()) {
            throw new WorkflowStateTransitionException("Outlet #" + outletId
                    + " has not been configured properly");
        }

        dk.i2m.converge.core.content.NewsItem newsItem =
                new dk.i2m.converge.core.content.NewsItem();
        dk.i2m.converge.core.workflow.Workflow workflow = outlet.getWorkflow();
        dk.i2m.converge.core.content.NewsItemActor nia =
                new dk.i2m.converge.core.content.NewsItemActor();

        nia.setRole(workflow.getStartState().getActorRole());
        nia.setUser(userAccount);
        nia.setNewsItem(newsItem);
        newsItem.getActors().add(nia);
        newsItem.setLanguage(outlet.getLanguage());
        newsItem.setTitle(title);
        newsItem.setOutlet(outlet);
        newsItem = newsItemFacade.start(newsItem);

        return newsItem.getId();
    }

    /**
     * Gets all complete {@link NewsItem}s for a given edition.
     *
     * @param id Unique identifier of the edition
     * @return {@link List} of complete {@link NewsItem}s in the given edition
     */
    @WebMethod(operationName = "getNewsItemsForEdition")
    public List<dk.i2m.converge.ws.model.NewsItem> getNewsItemsForEdition(@WebParam(name =
            "editionId") Long id) {
        List<dk.i2m.converge.ws.model.NewsItem> newsItems =
                new ArrayList<dk.i2m.converge.ws.model.NewsItem>();
        try {
            Edition edition = outletFacade.findEditionById(id);
            for (NewsItemPlacement placement : edition.getPlacements()) {
                if (placement.getNewsItem().isEndState()) {
                    newsItems.add(ModelConverter.toNewsItem(placement));
                }
            }
        } catch (DataNotFoundException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }

        return newsItems;
    }

    /**
     * Gets the assignments for the authenticated user.
     *
     * @return {@link List} of {@link NewsItem}s representing
     * the current assignments of the authenticated user
     */
    @WebMethod(operationName = "getAssignments")
    public List<dk.i2m.converge.ws.model.NewsItem> getAssignments() {
        List<dk.i2m.converge.ws.model.NewsItem> output =
                new ArrayList<dk.i2m.converge.ws.model.NewsItem>();

        if (context.getUserPrincipal() == null) {
            LOG.log(Level.WARNING, "User is not authenticated");
            return output;
        }

        String username = context.getUserPrincipal().getName();
        LOG.log(Level.INFO, "Fetching assignments for {0}", username);

        List<InboxView> assignments = newsItemFacade.findInbox(username);
        LOG.log(Level.INFO, "{0} items for {1}", new Object[]{assignments.size(),
                    username});

        for (InboxView assignment : assignments) {
            try {
                // TODO: Inefficient to check out each item. Create query similar to findInbox that will fetch required fields
                dk.i2m.converge.core.content.NewsItem newsItem = newsItemFacade.
                        findNewsItemById(assignment.getId());

                for (dk.i2m.converge.core.content.NewsItemPlacement nip :
                        newsItem.getPlacements()) {
                    dk.i2m.converge.ws.model.NewsItem ni = ModelConverter.
                            toNewsItem(nip);
                    output.add(ni);
                }

            } catch (DataNotFoundException ex) {
                LOG.log(Level.SEVERE,
                        "NewsItem in InboxView could not be found in database",
                        ex);
            }
        }

        return output;
    }

    /**
     * Gets the privileged outlets for the authenticated user.
     *
     * @return {@link List} of privileged {@link Outlet}s for the
     * authenticated user
     */
    @WebMethod(operationName = "getOutlets")
    public List<dk.i2m.converge.ws.model.Outlet> getOutlets() {
        List<dk.i2m.converge.ws.model.Outlet> output =
                new ArrayList<dk.i2m.converge.ws.model.Outlet>();

        if (context.getUserPrincipal() == null) {
            LOG.log(Level.WARNING, "User is not authenticated");
            return output;
        }

        String username = context.getUserPrincipal().getName();
        UserAccount userAccount;
        try {
            userAccount = userFacade.findById(username);
        } catch (DataNotFoundException ex) {
            LOG.log(Level.SEVERE, null, ex);
            return output;
        }

        for (dk.i2m.converge.core.workflow.Outlet outlet : userAccount.
                getPrivilegedOutlets()) {
            output.add(ModelConverter.toOutlet(outlet));
        }

        return output;
    }

    @WebMethod(operationName = "addNewsItemToEdition")
    public void addNewsItemToEdition(
            @WebParam(name = "newsItemId") Long newsItemId, @WebParam(name =
            "editionId") Long editionId,
            @WebParam(name = "sectionId") Long sectionId, @WebParam(name =
            "start") Integer start,
            @WebParam(name = "position") Integer position) throws
            DataNotFoundException {
        Section section = null;

        Edition edition = outletFacade.findEditionById(editionId);
        NewsItem newsItem = newsItemFacade.findNewsItemById(newsItemId);

        try {
            section = outletFacade.findSectionById(sectionId);
        } catch (DataNotFoundException ex) {
        }

        NewsItemPlacement placement = new NewsItemPlacement();
        placement.setEdition(edition);
        placement.setNewsItem(newsItem);
        placement.setSection(section);
        placement.setStart(start);
        placement.setPosition(position);
        placement.setOutlet(edition.getOutlet());

        newsItemFacade.createPlacement(placement);
    }

    @WebMethod(operationName = "addConceptToNewsItem")
    public void addConceptToNewsItem(
            @WebParam(name = "newsItemId") Long newsItemId, @WebParam(name =
            "conceptId") Long conceptId) {
        try {
            NewsItemHolder newsItemHolder = newsItemFacade.checkout(newsItemId);

            Concept concept = metaDataFacade.findConceptById(conceptId);
            newsItemHolder.getNewsItem().getConcepts().add(concept);
            //newsItemFacade.save(newsItemHolder.getNewsItem());
            newsItemFacade.checkin(newsItemHolder.getNewsItem());

        } catch (LockingException ex) {
            Logger.getLogger(NewsItemService.class.getName()).log(Level.SEVERE,
                    null, ex);
        } catch (DataNotFoundException ex) {
            Logger.getLogger(NewsItemService.class.getName()).log(Level.SEVERE,
                    null, ex);
        }

    }

    /**
     * Checks out a {@link NewsItem} using its unique identifier. Upon
     * checking out the {@link NewsItem} it becomes locked for editing
     * by other users.
     *
     * @param id Unique identifier of the {@link NewsItem}
     * @return {@link dk.i2m.converge.ws.model.NewsItem} matching the unique identifier
     * @throws NewsItemNotFoundException If the requested {@link NewsItem} could not be found
     * @throws NewsItemLockingException  If the requested {@link NewsItem} is locked by another user
     * @throws NewsItemReadOnlyException If the requested {@link NewsItem} is not in a state for being checked out
     */
    @WebMethod(operationName = "checkout")
    public dk.i2m.converge.ws.model.NewsItem checkout(Long id) throws
            NewsItemNotFoundException, NewsItemLockingException,
            NewsItemReadOnlyException {
        dk.i2m.converge.ws.model.NewsItem output = null;

        if (context.getUserPrincipal() == null) {
            LOG.log(Level.WARNING, "User is not authenticated");
            return output;
        }
        try {
            NewsItemHolder nih = newsItemFacade.checkout(id);

            if (!nih.isCheckedOut()) {
                throw new NewsItemLockingException(id
                        + " is checked out by another user");
            }

            if (nih.isReadOnly()) {
                throw new NewsItemReadOnlyException(
                        id
                        + " cannot be checked out. You do not have permission to edit the story.");
            }

            for (NewsItemPlacement nip : nih.getNewsItem().getPlacements()) {
                // Override the last one - keep the latest
                output = ModelConverter.toNewsItem(nip);
            }

            if (output == null) {
                output = ModelConverter.toNewsItem(nih.getNewsItem());
            }

        } catch (DataNotFoundException ex) {
            throw new NewsItemNotFoundException(id
                    + " does not exist in the database");
        }

        return output;
    }

    /**
     * Checks in a {@link NewsItem}.
     *
     * @param newsItem {@link dk.i2m.converge.ws.model.NewsItem} News item to check-in
     * @throws NewsItemNotFoundException If a corresponding {@link NewsItem} could not be found
     * @throws NewsItemLockingException  If the corresponding {@link NewsItem} is not locked, one can only check-in an item that has been checked-out
     */
    @WebMethod(operationName = "checkin")
    public void checkin(dk.i2m.converge.ws.model.NewsItem newsItem) throws
            NewsItemLockingException, NewsItemNotFoundException {

        if (context.getUserPrincipal() == null) {
            LOG.log(Level.WARNING, "User is not authenticated");
        }
        try {
            dk.i2m.converge.core.content.NewsItem ni = newsItemFacade.
                    findNewsItemById(newsItem.getId());
            ni.setTitle(newsItem.getTitle());
            ni.setByLine(newsItem.getByLine());
            ni.setStory(newsItem.getStory());
            ni.setBrief(newsItem.getBrief());

            try {
                newsItemFacade.checkin(ni);
            } catch (LockingException ex) {
                throw new NewsItemLockingException(ex.getMessage());
            }
        } catch (DataNotFoundException ex) {
            throw new NewsItemNotFoundException(newsItem.getId()
                    + " does not exist in the database");
        }
    }

    /**
     * Workflow step for the given NewsItem.
     *
     * @param newsItemId   Unique identifier of the {@link NewsItem} to step
     * @param workflowStep Unique identifier of the workflow step to take
     * @param comment      Comment to attach to the workflow step
     * @throws NewsItemNotFoundException If a corresponding {@link NewsItem} could not be found
     * @throws NewsItemWorkflowException If the workflow step is illegal
     */
    @WebMethod(operationName = "step")
    public void step(@WebParam(name = "newsItemId") Long newsItemId,
            @WebParam(name = "workflowStepId") Long workflowStep,
            @WebParam(name = "comment") String comment) throws
            NewsItemNotFoundException, NewsItemWorkflowException {

        if (context.getUserPrincipal() == null) {
            LOG.log(Level.WARNING, "User is not authenticated");
        }

        try {
            NewsItemHolder checkout = newsItemFacade.checkout(newsItemId);
            try {
                newsItemFacade.step(checkout.getNewsItem(), workflowStep,
                        comment);
            } catch (WorkflowStateTransitionException ex) {
                throw new NewsItemWorkflowException(ex);
            }
        } catch (DataNotFoundException ex) {
            throw new NewsItemNotFoundException(newsItemId
                    + " does not exist in the database");
        }
    }
}
