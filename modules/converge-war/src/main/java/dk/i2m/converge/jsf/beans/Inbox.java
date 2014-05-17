/*
 * Copyright (C) 2010 - 2012 Interactive Media Management
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later 
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT 
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more 
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package dk.i2m.converge.jsf.beans;

import dk.i2m.converge.core.DataNotFoundException;
import dk.i2m.converge.core.content.AssignmentType;
import dk.i2m.converge.core.content.NewsItem;
import dk.i2m.converge.core.content.NewsItemActor;
import dk.i2m.converge.core.content.NewsItemPlacement;
import dk.i2m.converge.core.content.catalogue.Catalogue;
import dk.i2m.converge.core.content.catalogue.MediaItem;
import dk.i2m.converge.core.content.catalogue.MediaItemStatus;
import dk.i2m.converge.core.security.SystemPrivilege;
import dk.i2m.converge.core.security.UserAccount;
import dk.i2m.converge.core.views.InboxView;
import dk.i2m.converge.core.workflow.Outlet;
import dk.i2m.converge.core.workflow.WorkflowState;
import dk.i2m.converge.ejb.facades.CatalogueFacadeLocal;
import dk.i2m.converge.ejb.facades.DuplicateExecutionException;
import dk.i2m.converge.ejb.facades.NewsItemFacadeLocal;
import dk.i2m.converge.ejb.facades.WorkflowStateTransitionException;
import dk.i2m.converge.jsf.components.tags.DialogSelfAssignment;
import dk.i2m.jsf.JsfUtils;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import org.richfaces.component.html.HtmlTree;
import org.richfaces.event.NodeSelectedEvent;
import org.richfaces.model.TreeNode;
import org.richfaces.model.TreeNodeImpl;

/**
 * Backing bean for {@code /Inbox.jspx}.
 *
 * @author Allan Lykke Christensen
 */
public class Inbox {

    private static final Logger LOG = Logger.getLogger(Inbox.class.getName());

    /** ResourceBundle containing internationalised messages. */
    private ResourceBundle bundle = JsfUtils.getResourceBundle("i18n");

    @EJB private NewsItemFacadeLocal newsItemFacade;

    @EJB private CatalogueFacadeLocal catalogueFacade;

    private NewsItem selectedNewsItem;

    private DataModel newsItems = null;

    private DataModel mediaItems = null;

    private TreeNode outletsNode = null;

    private String inboxTitle = "";

    private NewsItem duplicateNewsItem;

    private DialogSelfAssignment newAssignment;

    private boolean showNewsItem = true;

    private boolean catalogueEditor = false;

    private String newAssignmentType = "tabStory";

    private String createdItemLink;

    /**
     * Creates a new instance of {@link Inbox}.
     */
    public Inbox() {
    }

    @PostConstruct
    public void onInit() {
        onShowMyAssignments(null);
    }

    /**
     * Gets the title of the Inbox. The title changes depending on the selected
     * folder/state.
     *
     * @return Title of the inbox
     */
    public String getInboxTitle() {
        return JsfUtils.getMessage("i18n", "Inbox_INBOX_X", new Object[]{
                    inboxTitle});
    }

    public String getNewAssignmentType() {
        return newAssignmentType;
    }

    public void setNewAssignmentType(String newAssignmentType) {
        this.newAssignmentType = newAssignmentType;
    }

    /**
     * Gets a {@link DataModel} containing the user's {@link NewsItem}s.
     *
     * @return {@link DataModel} containing the user's {@link NewsItem}s
     */
    public DataModel getNewsItems() {
        if (newsItems == null) {
            newsItems = new ListDataModel(new ArrayList());
        }
        return newsItems;
    }

    public DataModel getMediaItems() {
        if (mediaItems == null) {
            mediaItems = new ListDataModel(new ArrayList());
        }
        return mediaItems;
    }

    /**
     * Action listeners for preparing the creation of a new assignment.
     *
     * @param event
     * {@link ActionEvent} that invoked the listener.
     */
    public void onNewAssignment(ActionEvent event) {
        try {
            switch (getUser().getDefaultAssignmentType()) {
                case MEDIA_ITEM:
                    this.newAssignmentType = "tabMedia";
                    break;
                case NEWS_ITEM:
                    this.newAssignmentType = "tabStory";
                    break;
            }
        } catch (NullPointerException ex) {
            // Default assignment type not set
            this.newAssignmentType = "tabStory";
        }

        newAssignment = new DialogSelfAssignment();

        newAssignment.getAssignment().setDeadline(
                java.util.Calendar.getInstance());
        newAssignment.getAssignment().getDeadline().setTimeZone(getUser().
                getTimeZone());
        if (newAssignment.getAssignment().getDeadline().get(
                java.util.Calendar.HOUR_OF_DAY) >= 15) {
            newAssignment.getAssignment().getDeadline().add(
                    java.util.Calendar.DAY_OF_MONTH, 1);
        }
        newAssignment.getAssignment().getDeadline().set(
                java.util.Calendar.HOUR_OF_DAY, 15);
        newAssignment.getAssignment().getDeadline().set(
                java.util.Calendar.MINUTE, 0);
        newAssignment.getAssignment().getDeadline().set(
                java.util.Calendar.SECOND, 0);
        newAssignment.getNewsItem().setOutlet(getUser().getDefaultOutlet());
        if (newAssignment.getNewsItem().getOutlet() != null) {
            newAssignment.getNewsItem().setLanguage(newAssignment.getNewsItem().
                    getOutlet().getLanguage());
        }
        newAssignment.getAssignment().setType(
                getUser().getDefaultAssignmentType());
        newAssignment.getMediaItem().setCatalogue(getUser().
                getDefaultMediaRepository());
        newAssignment.setNextEdition(getUser().isDefaultAddNextEdition());
    }

    /**
     * Event handler for creating a new assignment.
     * <p/>
     * @param event Event that invoked the handler
     */
    public void onAddAssignment(ActionEvent event) {

        if (newAssignmentType.equalsIgnoreCase("tabStory")) {
            newAssignment.getAssignment().setType(AssignmentType.NEWS_ITEM);
        } else {
            newAssignment.getAssignment().setType(AssignmentType.MEDIA_ITEM);
        }

        switch (newAssignment.getAssignment().getType()) {
            case NEWS_ITEM:
                if (newAssignment.getNewsItem().getOutlet() == null) {
                    JsfUtils.createMessage("frmInbox",
                            FacesMessage.SEVERITY_ERROR, "i18n",
                            "Inbox_NEWS_ITEM_OUTLET_REQUIRED", new Object[]{});
                    return;
                }

                try {
                    selectedNewsItem = newAssignment.getNewsItem();
                    NewsItemActor nia = new NewsItemActor();
                    nia.setRole(selectedNewsItem.getOutlet().getWorkflow().
                            getStartState().getActorRole());
                    nia.setUser(getUser());
                    nia.setNewsItem(selectedNewsItem);
                    selectedNewsItem.getActors().add(nia);
                    selectedNewsItem.setDeadline(newAssignment.getAssignment().
                            getDeadline());

                    if (selectedNewsItem.getOutlet() != null) {
                        selectedNewsItem.setLanguage(selectedNewsItem.getOutlet().
                                getLanguage());
                    }
                    selectedNewsItem.setTitle(newAssignment.getTitle());
                    selectedNewsItem = newsItemFacade.start(selectedNewsItem);
                    this.createdItemLink = "NewsItem.xhtml?id="
                            + selectedNewsItem.getId();

                    if (newAssignment.isNextEdition()) {
                        try {
                            NewsItemPlacement placement = newsItemFacade.
                                    addToNextEdition(selectedNewsItem, getUser().
                                    getDefaultSection());
                            selectedNewsItem = placement.getNewsItem();
                        } catch (DataNotFoundException ex) {
                            LOG.log(Level.INFO, "Could not find next edition");
                        }
                    }

                    JsfUtils.createMessage("frmInbox",
                            FacesMessage.SEVERITY_INFO, Bundle.i18n.name(),
                            "Inbox_ASSIGNMENT_CREATED");
                } catch (DuplicateExecutionException ex) {
                    // Double click prevention - stamp in log
                    LOG.log(Level.INFO, ex.getMessage());
                } catch (WorkflowStateTransitionException ex) {
                    JsfUtils.createMessage("frmInbox",
                            FacesMessage.SEVERITY_ERROR, Bundle.i18n.name(),
                            "Inbox_ASSIGNMENT_CREATION_ERROR");
                    LOG.log(Level.SEVERE, ex.getMessage(), ex);
                }
                break;
            case MEDIA_ITEM:
                if (newAssignment.getMediaItem().getCatalogue() == null) {
                    JsfUtils.createMessage("frmInbox",
                            FacesMessage.SEVERITY_ERROR, Bundle.i18n.name(),
                            "Inbox_MEDIA_ITEM_CATELOGUE_REQUIRED");
                    return;
                }

                newAssignment.getMediaItem().setStatus(
                        MediaItemStatus.UNSUBMITTED);
                newAssignment.getMediaItem().setTitle(newAssignment.getTitle());
                newAssignment.getMediaItem().setOwner(getUser());
                newAssignment.getMediaItem().setByLine(getUser().getFullName());
                MediaItem newItem = catalogueFacade.create(newAssignment.
                        getMediaItem());
                this.createdItemLink = "MediaItemDetails.xhtml?id=" + newItem.
                        getId();
                JsfUtils.createMessage("frmInbox", FacesMessage.SEVERITY_INFO,
                        Bundle.i18n.name(), "Inbox_ASSIGNMENT_CREATED");
                showNewsItem = false;
                break;
        }
    }

    public String getCreatedItemLink() {
        return this.createdItemLink;
    }

    /**
     * Action Listener for removing articles marked as deleted.
     *
     * @param event
     * {@link ActionEvent} that invoked the listener
     */
    public void onEmptyTrash(ActionEvent event) {
        int deleted = newsItemFacade.emptyTrash(getUser().getUsername());
        onShowMyAssignments(event);

        JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_INFO,
                Bundle.i18n.name(), "Inbox_X_ITEMS_DELETED", new Object[]{
                    deleted});
    }

    /**
     * Event handler for showing the current assignments of the user.
     * <p/>
     * @param event * Event that invoked the handler
     */
    public void onShowMyAssignments(ActionEvent event) {
        showNewsItem = true;
        this.inboxTitle = bundle.getString("Inbox_MY_ASSIGNMENTS");
        List<InboxView> inboxView = newsItemFacade.findInbox(getUser().
                getUsername());
        this.newsItems = new ListDataModel(inboxView);
    }

    public NewsItem getSelectedNewsItem() {
        return selectedNewsItem;
    }

    public void setSelectedNewsItem(NewsItem selectedNewsItem) {
        this.selectedNewsItem = selectedNewsItem;
    }

    public Map<String, Outlet> getPrivilegedOutlets() {
        Map<String, Outlet> outlets = new LinkedHashMap<String, Outlet>();

        for (Outlet outlet : getUser().getPrivilegedOutlets(
                SystemPrivilege.MY_NEWS_ITEMS)) {
            outlets.put(outlet.getTitle(), outlet);
        }

        return outlets;
    }

    public TreeNode getOutletsNode() {
        if (outletsNode == null) {
            outletsNode = new TreeNodeImpl();
            List<Outlet> outlets = getUser().getPrivilegedOutlets(
                    SystemPrivilege.MY_NEWS_ITEMS);

            for (Outlet outlet : outlets) {
                if (outlet.isValid()) {
                    TreeNode node = new TreeNodeImpl();
                    node.setData(new OutletNode(outlet, null, outlet.getClass().
                            getName()));

                    List<WorkflowState> states =
                            outlet.getWorkflow().getStates();

                    for (WorkflowState state : states) {
                        TreeNode subNode = new TreeNodeImpl();
                        subNode.setData(new OutletNode(state, outlet, state.
                                getClass().getName()));
                        node.addChild(state.getId(), subNode);
                    }

                    outletsNode.addChild("O" + outlet.getId(), node);
                }
            }

            List<Catalogue> myCatalogues = catalogueFacade.findCataloguesByUser(
                    getUser().getUsername());

            for (Catalogue myCatalogue : myCatalogues) {
                TreeNode node = new TreeNodeImpl();
                node.setData(new OutletNode(myCatalogue, null, myCatalogue.
                        getClass().getName()));

                for (MediaItemStatus status : MediaItemStatus.values()) {
                    TreeNode subNode = new TreeNodeImpl();
                    subNode.setData(new OutletNode(status, myCatalogue,
                            MediaItemStatus.class.getName()));
                    node.addChild(status.name(), subNode);
                }

                outletsNode.addChild("M" + myCatalogue.getId(), node);
            }
        }

        return outletsNode;
    }

    /**
     * Event handler for handling selection of outlet and catalogue folders.
     *
     * @param event 
     *          Event that invoked the handler
     */
    public void onOutletFolderSelect(NodeSelectedEvent event) {
        this.catalogueEditor = false;

        HtmlTree tree = (HtmlTree) event.getComponent();
        OutletNode node = (OutletNode) tree.getRowData();

        if (node.getData() instanceof Outlet) {
            showNewsItem = true;
            mediaItems = new ListDataModel();
            Outlet outlet = (Outlet) node.getData();
            inboxTitle = outlet.getTitle();
            newsItems = new ListDataModel(newsItemFacade.findOutletBox(getUser().
                    getUsername(), outlet));
        } else if (node.getData() instanceof WorkflowState) {
            showNewsItem = true;
            mediaItems = new ListDataModel();
            WorkflowState state = (WorkflowState) node.getData();
            Outlet outlet = (Outlet) node.getParentData();
            inboxTitle = outlet.getTitle() + " - " + state.getName();
            if (state.equals(outlet.getWorkflow().getEndState())) {
                newsItems =
                        new ListDataModel(newsItemFacade.findOutletBox(getUser().
                        getUsername(), outlet, state, 0, 100));
            } else {
                newsItems =
                        new ListDataModel(newsItemFacade.findOutletBox(getUser().
                        getUsername(), outlet, state));
            }
        } else if (node.getData() instanceof Catalogue) {
            showNewsItem = false;
            newsItems = new ListDataModel();
            Catalogue repository = (Catalogue) node.getData();
            if (getUser().getUserRoles().contains(repository.getEditorRole())) {
                this.catalogueEditor = true;
            }
            inboxTitle = repository.getName();
            mediaItems =
                    new ListDataModel(catalogueFacade.findCurrentMediaItems(
                    getUser(), repository.getId()));
        } else if (node.getData() instanceof MediaItemStatus) {
            showNewsItem = false;
            newsItems = new ListDataModel();
            Catalogue catalogue = (Catalogue) node.getParentData();
            if (getUser().getUserRoles().contains(catalogue.getEditorRole())) {
                this.catalogueEditor = true;
            }
            MediaItemStatus status = (MediaItemStatus) node.getData();

            String catalogueStatus = JsfUtils.getMessage(Bundle.i18n.name(),
                    "Generic_MEDIA_ITEM_STATUS_"
                    + status.name(), new Object[]{});
            inboxTitle = JsfUtils.getMessage(Bundle.i18n.name(),
                    "Inbox_CATALOGUE_STATUS", new Object[]{catalogue.getName(),
                        catalogueStatus});
            mediaItems =
                    new ListDataModel(catalogueFacade.findCurrentMediaItems(
                    getUser(), status, catalogue.getId()));
        }
    }

    /**
     * Event handler for updating the state of an {@link MediaItem} from the
     * list of {@link MediaItem}s in a {@link Catalogue} folder.
     * <p/>
     * @param item {@link MediaItem} to update
     */
    public void setUpdateMediaItem(MediaItem item) {
        // The media item must be removed and added to keep its version 
        // identifier current. If it is not updated in the datamodel, it will 
        // throw an internal server error upon the second update

        // Remove media item from data model
        ((List<MediaItem>) getMediaItems().getWrappedData()).remove(item);

        // Update the media item in the database
        item = catalogueFacade.update(item);

        // Add the media item back in the data model
        ((List<MediaItem>) getMediaItems().getWrappedData()).add(item);
    }

    public boolean isCatalogueEditor() {
        return this.catalogueEditor;
    }

    public NewsItem getDuplicateNewsItem() {
        return duplicateNewsItem;
    }

    public void setDuplicateNewsItem(NewsItem duplicateNewsItem) {
        this.duplicateNewsItem = duplicateNewsItem;

        if (this.duplicateNewsItem != null) {
            onNewAssignment(null);

            newAssignment.getNewsItem().setTitle(
                    getDuplicateNewsItem().getTitle());
            newAssignment.setTitle(getDuplicateNewsItem().getTitle());
            newAssignment.getNewsItem().setBrief(
                    getDuplicateNewsItem().getBrief());
            newAssignment.getNewsItem().setStory(
                    getDuplicateNewsItem().getStory());
            newAssignment.getNewsItem().setVersionOf(getDuplicateNewsItem());
        }
    }

    public DialogSelfAssignment getNewAssignment() {
        return newAssignment;
    }

    public void setNewAssignment(DialogSelfAssignment newAssignment) {
        this.newAssignment = newAssignment;
    }

    private UserAccount getUser() {
        final String valueExpression = "#{userSession.user}";
        return (UserAccount) JsfUtils.getValueOfValueExpression(valueExpression);
    }

    public boolean isShowNewsItem() {
        return showNewsItem;
    }

    public boolean isShowMediaItem() {
        return !showNewsItem;
    }

    public class OutletNode {

        private Object data;

        private Object parentData;

        private String type;

        public OutletNode(Object data, Object parentData, String type) {
            this.data = data;
            this.parentData = parentData;
            this.type = type;
        }

        public Object getParentData() {
            return parentData;
        }

        public void setParentData(Object parentData) {
            this.parentData = parentData;
        }

        public Object getData() {
            return data;
        }

        public void setData(Object data) {
            this.data = data;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final OutletNode other = (OutletNode) obj;
            if (this.data != other.data && (this.data == null || !this.data.
                    equals(other.data))) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 53 * hash + (this.data != null ? this.data.hashCode() : 0);
            return hash;
        }
    }

    public class MediaRepositoryNode {

        private Object data;

        private Object parentData;

        private String type;

        public MediaRepositoryNode(Object data, Object parentData, String type) {
            this.data = data;
            this.parentData = parentData;
            this.type = type;
        }

        public Object getParentData() {
            return parentData;
        }

        public void setParentData(Object parentData) {
            this.parentData = parentData;
        }

        public Object getData() {
            return data;
        }

        public void setData(Object data) {
            this.data = data;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final MediaRepositoryNode other = (MediaRepositoryNode) obj;
            if (this.data != other.data && (this.data == null || !this.data.
                    equals(other.data))) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 53 * hash + (this.data != null ? this.data.hashCode() : 0);
            return hash;
        }
    }
}
