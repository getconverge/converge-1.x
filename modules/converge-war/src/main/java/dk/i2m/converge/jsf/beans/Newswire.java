/*
 * Copyright 2010 - 2012 Interactive Media Management
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
package dk.i2m.converge.jsf.beans;

import dk.i2m.converge.core.DataNotFoundException;
import dk.i2m.converge.core.content.ContentTag;
import dk.i2m.converge.core.newswire.NewswireBasket;
import dk.i2m.converge.core.newswire.NewswireItem;
import dk.i2m.converge.core.newswire.NewswireService;
import dk.i2m.converge.core.security.SystemPrivilege;
import dk.i2m.converge.core.security.UserAccount;
import dk.i2m.converge.core.workflow.Outlet;
import dk.i2m.converge.domain.search.SearchResult;
import dk.i2m.converge.domain.search.SearchResults;
import dk.i2m.converge.ejb.facades.*;
import dk.i2m.converge.ejb.services.NewswireServiceLocal;
import dk.i2m.converge.jsf.components.tags.DialogAssignment;
import dk.i2m.converge.jsf.components.tags.DialogEventSelection;
import dk.i2m.jsf.JsfUtils;
import static dk.i2m.jsf.JsfUtils.createMessage;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

/**
 * Request-scoped backing bean for the {@code Newswire.jspx} page.
 *
 * @author Allan Lykke Christensen
 */
public class Newswire {

    private static final Logger LOG = Logger.getLogger(Newswire.class.getName());

    @EJB private NewswireServiceLocal newswireService;

    @EJB private OutletFacadeLocal outletFacade;

    @EJB private UserFacadeLocal userFacade;

    @EJB private CalendarFacadeLocal calendarFacade;

    @EJB private NewsItemFacadeLocal newsItemFacade;

    @EJB private WorkflowFacadeLocal workflowFacade;

    @EJB private MetaDataFacadeLocal metaDataFacade;

    private NewswireService selectedService;

    //private String search = "";
    private DataModel newsService = null;

    private DataModel news = null;

    private NewswireItem selectedItem = null;

    private NewswireBasket selectedBasket = null;

    private DialogAssignment dialogAssignment;

    private DialogEventSelection dialogEventSelection;

    private String keyword = "";

    private DataModel searchResults = new ListDataModel();

    private DataModel pages = new ListDataModel(new ArrayList());

    private boolean showResults = false;

    private long resultsFound = 0;

    private String sortField = "score";

    private String sortOrder = "false";

    private boolean displayContentTags = false;

    private int show = 30;

    private String searchPhrase = "";

    private String basketTag = "";

    //  private String searchType = "type:Story";
    private SearchResults results = new SearchResults();

    private List<String> filterQueries = new ArrayList<String>();

    private DataModel baskets = null;

    private Map<String, NewswireService> availableNewswireServices = null;

    private String linkSearch = "";

    @PostConstruct
    public void onInit() {
        displayContentTags = getUserAccount().isDefaultSearchEngineTags();
        onShowTodaysNews(null);
    }

    /**
     * Event handler for showing todays news of the current user.
     * 
     * @param event
     *          Event that invoked the handler
     */
    public void onShowTodaysNews(ActionEvent event) {
        getFilterQueries().clear();
        setSortField("date");
        setSortOrder("false");

        // Ignore search if user hasn't subscribed to any services;
        if (getUserAccount().getActiveNewswireServices().isEmpty()) {
            searchResults = new ListDataModel(new ArrayList());
            pages = new ListDataModel(new ArrayList());
            return;
        }
        
        StringBuilder query = new StringBuilder();
        for (NewswireService service : getUserAccount().getActiveNewswireServices()) {
            if (query.length() > 0) {
                query.append(" || ");
            } else {
                query.append("(");
            }
            query.append("provider-id:").append(service.getId());
        }
        if (query.length() > 0) {
            query.append(") && ");
        }

        query.append("date:[NOW-1DAY TO NOW]");

        this.keyword = query.toString();
        filterQueries.clear();

        conductSearch(this.keyword, 0, getShow());
    }

    public void onNewBasket(ActionEvent event) {
        this.basketTag = "";
        java.util.Calendar now = java.util.Calendar.getInstance();
        this.selectedBasket = new NewswireBasket();
        this.selectedBasket.setTitle("Untitled");
        this.selectedBasket.setCreated(now.getTime());
        this.selectedBasket.setUpdated(now.getTime());
        this.selectedBasket.setOwner(getUserAccount());
        this.selectedBasket.setMailDelivery(false);
        this.selectedBasket.setMailFrequency(8);
        this.selectedBasket.setHourFirstDelivery(8);
    }

    public void onSaveBasket(ActionEvent event) {
        this.basketTag = "";
        if (this.selectedBasket != null) {
            if (this.selectedBasket.getId() == null) {
                this.selectedBasket = newswireService.createBasket(selectedBasket);
            } else {
                this.selectedBasket = newswireService.updateBasket(selectedBasket);
            }
        }
        this.baskets = null;
    }
    
    /**
     * Dispatches the current basket as an e-mail.
     * 
     * @param event Event that invoked the handler
     */
    public void onDispatchBasket(ActionEvent event) {
        if (newswireService.dispatchBasket(selectedBasket.getId())) {
            createMessage("frmPage", FacesMessage.SEVERITY_INFO, Bundle.i18n.name(), "Newswire_BASKET_DISPATCHED");
        } else {
            createMessage("frmPage", FacesMessage.SEVERITY_WARN, Bundle.i18n.name(), "Newswire_BASKET_EMPTY_NOT_DISPATCHED", new Object[]{selectedBasket.getMailFrequency()});
        }
    }

    public void setDeleteTag(ContentTag tag) {
        getSelectedBasket().getTags().remove(tag);
    }

    public void onDeleteBasket(ActionEvent event) {
        if (this.selectedBasket != null) {
            newswireService.deleteBasket(selectedBasket);
        }
        this.baskets = null;
    }

    public void onAddTagSuggestion(ActionEvent event) {
        if (selectedBasket != null) {
            ContentTag tag = metaDataFacade.findOrCreateContentTag(basketTag);
            selectedBasket.getTags().add(tag);
        }
        this.basketTag = "";
    }

    public List<ContentTag> onTagSuggestion(Object suggestion) {
        String tagName = (String) suggestion;
        List<ContentTag> suggested = metaDataFacade.findContentTagLikeName(tagName);
        return suggested;
    }

    /**
     * Gets the {@link DataModel} containing the current news items of the newswire services.
     * 
     * @return {@link DataModel} containing the current news items of the newswire services.
     */
    public DataModel getNews() {
        if (news == null) {
            news = new ListDataModel(newswireService.getNews());
        }
        return news;
    }

    public DataModel getServices() {
        if (newsService == null) {
            newsService = new ListDataModel(getUserAccount().getActiveNewswireServices());
        }

        return newsService;
    }

    public Map<String, NewswireService> getAvailableServices() {
        if (availableNewswireServices == null) {
            availableNewswireServices = new LinkedHashMap<String, NewswireService>();
            List<NewswireService> services = newswireService.findAvailableNewswireServices(getUserAccount().getId());
            for (NewswireService s : services) {
                availableNewswireServices.put(s.getSource(), s);
            }
        }
        return availableNewswireServices;
    }

    public DataModel getBaskets() {
        if (baskets == null) {
            baskets = new ListDataModel(newswireService.findBasketsByUser(getUserAccount().getId()));
        }

        return baskets;
    }

    public NewswireService getSelectedService() {
        return selectedService;
    }

    public void setSelectedService(NewswireService selectedService) {
        this.selectedService = selectedService;
    }

    public void onSelectService(ActionEvent event) {
        if (selectedService != null) {
            Long id = selectedService.getId();
            setSortField("date");
            setSortOrder("false");

            setKeyword("*:*");
            getFilterQueries().clear();
            getFilterQueries().add("provider-id:" + selectedService.getId());
            conductSearch(getKeyword(), 0, getShow());
        }
    }

    public void onSelectBasket(ActionEvent event) {
        if (selectedBasket != null) {
            setSortField("date");
            setSortOrder("false");

            setKeyword("");
            getFilterQueries().clear();
            setKeyword(selectedBasket.getQuery());
            conductSearch(getKeyword(), 0, getShow());
        }
    }

    public void onClear(ActionEvent event) {
        setKeyword("");
        getFilterQueries().clear();
        results = null;
        searchResults = null;
        pages = null;
    }

    public void onShowAll(ActionEvent event) {
        news = new ListDataModel(newswireService.getNews());
    }

    private UserAccount getUserAccount() {
        final String valueExpression = "#{userSession.user}";
        return (UserAccount) JsfUtils.getValueOfValueExpression(valueExpression);
    }

    public NewswireItem getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(NewswireItem selectedItem) {
        this.selectedItem = selectedItem;
    }

    public NewswireBasket getSelectedBasket() {
        return selectedBasket;
    }

    public void setSelectedBasket(NewswireBasket selectedBasket) {
        this.selectedBasket = selectedBasket;
    }

    public void setSelectedSearchResult(SearchResult searchResult) {
        try {
            setSelectedItem(newswireService.findNewswireItemById(searchResult.getId()));
        } catch (DataNotFoundException ex) {
            JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_ERROR, 
                    Bundle.i18n.name(), "Newswire_STORY_NOT_FOUND");
        }
    }
    
    public void setRemoveItem(SearchResult searchResult) {
        newswireService.removeItem(searchResult.getId());
    }

    /**
     * Event handler for saving selected newswire subscriptions.
     * 
     * @param event 
     *          Event that invoked the handler
     */
    public void onSaveSubscriptions(ActionEvent event) {
        userFacade.update(getUserAccount());
        
        // Reset list of current subscriptions
        newsService = null;
        
        // Refresh todays news
        onShowTodaysNews(event);
        
        JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_INFO, 
                Bundle.i18n.name(), "Newswire_MY_NEWSWIRE_SUBSCRIPTIONS_UPDATED");
    }

    public void onAssign(ActionEvent event) {
        dialogAssignment = new DialogAssignment(outletFacade, workflowFacade, userFacade, newsItemFacade, calendarFacade, getOutlets());
        dialogAssignment.showStoryTab();
        dialogAssignment.getAssignment().setAssigned(true);
        dialogAssignment.getAssignment().setAssignedBy(getUserAccount());

        if (getUserAccount().getDefaultOutlet() != null) {
            dialogAssignment.getAssignment().setOutlet(getUserAccount().getDefaultOutlet());
        } else if (!getOutlets().isEmpty()) {
            dialogAssignment.getAssignment().setOutlet(getOutlets().iterator().next());
        }

        dialogAssignment.getAssignment().setTitle(selectedItem.getTitle());
        dialogAssignment.getAssignment().setBrief(selectedItem.getSummary());
        dialogAssignment.getAssignment().setStory(selectedItem.getContent());
        if (selectedItem.getAuthor() != null && !selectedItem.getAuthor().trim().isEmpty()) {
            dialogAssignment.getAssignment().setByLine(selectedItem.getAuthor());
        } else {
            dialogAssignment.getAssignment().setByLine(selectedItem.getNewswireService().getSource());
        }

        dialogAssignment.onChangeOutlet(null);

        dialogAssignment.getAssignment().setDeadline(java.util.Calendar.getInstance());
        dialogAssignment.getAssignment().getDeadline().setTimeZone(getUserAccount().getTimeZone());
        dialogAssignment.getAssignment().getDeadline().set(java.util.Calendar.HOUR_OF_DAY, 15);
        dialogAssignment.getAssignment().getDeadline().set(java.util.Calendar.MINUTE, 0);
        dialogAssignment.getAssignment().getDeadline().set(java.util.Calendar.SECOND, 0);

        dialogEventSelection = new DialogEventSelection(calendarFacade);
        dialogEventSelection.setAssignment(dialogAssignment.getAssignment());
    }

    /**
     * Gets a {@link List} of the {@link Outlet}s where the current user has
     * outlet planning privileges.
     *
     * @return {@link List} of the {@link Outlet}s where the current user has
     *         outlet planning privileges
     */
    public List<Outlet> getOutlets() {
        return getUserAccount().getPrivilegedOutlets(SystemPrivilege.OUTLET_PLANNING);
    }

    public DialogAssignment getDialogAssignment() {
        return dialogAssignment;
    }

    public void setDialogAssignment(DialogAssignment dialogAssignment) {
        this.dialogAssignment = dialogAssignment;
    }

    public DialogEventSelection getDialogEventSelection() {
        return dialogEventSelection;
    }

    public void setDialogEventSelection(DialogEventSelection dialogEventSelection) {
        this.dialogEventSelection = dialogEventSelection;
    }

    public List<String> getFilterQueries() {
        return filterQueries;
    }

    public void setFilterQueries(List<String> filterQueries) {
        this.filterQueries = filterQueries;
    }

    /**
     * Gets the keyword (query) being searched for.
     *
     * @return Keyword (query) being searched for
     */
    public String getKeyword() {
        return keyword;
    }

    /**
     * Sets the keyword (query) to be searched for.
     *
     * @param keyword
     *          Keyword (query) to be searched for
     */
    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Gets the search results.
     *
     * @return {@link DataModel} containing the {@link SearchResult}s
     */
    public DataModel getSearchResults() {
        return searchResults;
    }

    /**
     * Determines if the results should be shown.
     *
     * @return {@code true} if the search results should be shown otherwise
     *         {@code false}
     */
    public boolean isShowResults() {
        return showResults;
    }

    public String getBasketTag() {
        return basketTag;
    }

    public void setBasketTag(String basketTag) {
        this.basketTag = basketTag;
    }

    /**
     * Event handler for starting the search.
     *
     * @param event
     *          Event that invoked the handler
     */
    public void onSearch(ActionEvent event) {
        if (!getKeyword().trim().isEmpty()) {
            conductSearch(keyword, 0, getShow());
        }
    }

    public void onChangePage(ActionEvent event) {
        String changePage = JsfUtils.getRequestParameterMap().get("changePage");
        if (changePage != null) {
            int newPage = Integer.valueOf(changePage);
            int newStart = (newPage - 1) * results.getResultsPerPage();
            int rows = results.getResultsPerPage();
            conductSearch(keyword, newStart, rows);
        }
    }

    public void onAddFacet(ActionEvent event) {
        String filterQuery = JsfUtils.getRequestParameterMap().get("addFacet");
        if (filterQuery != null) {
            filterQueries.add(filterQuery);
            conductSearch(keyword, 0, getShow());
        }
    }

    public void onRemoveFacet(ActionEvent event) {
        String filterQuery = JsfUtils.getRequestParameterMap().get("filterQuery");
        if (filterQuery != null) {
            filterQueries.remove(filterQuery);
            conductSearch(keyword, 0, getShow());
        }
    }

    private void conductSearch(String keyword, int start, int rows) {
        searchResults = new ListDataModel(new ArrayList());
        pages = new ListDataModel(new ArrayList());

        List<String> filters = new ArrayList<String>();
        //   filters.add(searchType);
        filters.addAll(filterQueries);
        if (keyword.equalsIgnoreCase("")) {
            keyword = "*:*";
            this.keyword = "*:*";
        }

        this.results = newswireService.search(keyword, start, rows, sortField, Boolean.valueOf(sortOrder), filters.toArray(new String[filters.size()]));

        if (results.getNumberOfResults() == 0) {
            showResults = false;
            searchResults = new ListDataModel(new ArrayList());
            JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_INFO, 
                    Bundle.i18n.name(), "Newswire_NO_RESULTS_FOUND_FOR_X", 
                    new Object[]{getKeyword()});
        } else {

            for (SearchResult hit : results.getHits()) {
                hit.setLink(MessageFormat.format(hit.getLink(), new Object[]{JsfUtils.getValueOfValueExpression("#{facesContext.externalContext.request.contextPath}")}));
            }
            searchResults = new ListDataModel(results.getHits());

            for (long l = 0; l < results.getNumberOfPages(); l++) {
                if (l >= 20) {
                    break;
                }
                SearchPage page = new SearchPage((l + 1), l * results.getResultsPerPage(), results.getNumberOfPages());
                ((ArrayList) getPages().getWrappedData()).add(page);
            }

            showResults = true;
        }
    }

    public long getResultsFound() {
        return resultsFound;
    }

    public SearchResults getResults() {
        return results;
    }

    public void setResults(SearchResults results) {
        this.results = results;
    }

    public DataModel getPages() {
        return pages;
    }

    public void setPages(DataModel pages) {
        this.pages = pages;
    }

    public String getSortField() {
        return sortField;
    }

    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public int getShow() {
        return show;
    }

    public void setShow(int show) {
        this.show = show;
    }

    public String getLinkSearch() {
        return linkSearch;
    }

    public void setLinkSearch(String linkSearch) {
        this.linkSearch = linkSearch;
        if (this.linkSearch != null) {
            this.keyword = linkSearch;
            onSearch(null);
        }
    }

    public class SearchPage {

        private long page = 1;

        private long start = 0;

        private long show = 0;

        public SearchPage(long page, long start, long show) {
            this.page = page;
            this.start = start;
            this.show = show;
        }

        public long getPage() {
            return page;
        }

        public void setPage(long page) {
            this.page = page;
        }

        public long getShow() {
            return show;
        }

        public void setShow(long show) {
            this.show = show;
        }

        public long getStart() {
            return start;
        }

        public void setStart(long start) {
            this.start = start;
        }
    }

    public boolean isDisplayContentTags() {
        return displayContentTags;
    }

    public void setDisplayContentTags(boolean displayContentTags) {
        this.displayContentTags = displayContentTags;
    }
}
