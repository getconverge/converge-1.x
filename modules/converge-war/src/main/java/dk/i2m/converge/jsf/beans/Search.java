/*
 *  Copyright (C) 2010 - 2012 Interactive Media Management
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package dk.i2m.converge.jsf.beans;

import dk.i2m.converge.core.security.UserAccount;
import dk.i2m.converge.domain.search.SearchResult;
import dk.i2m.converge.domain.search.SearchResults;
import dk.i2m.converge.ejb.facades.SearchEngineLocal;
import dk.i2m.jsf.JsfUtils;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;

/**
 * JSF backing bean for {@code /search.jspx}.
 *
 * @author Allan Lykke Christensen
 */
public class Search {

    @EJB private SearchEngineLocal searchEngine;

    private String keyword = "";

    private DataModel searchResults = new ListDataModel();

    private DataModel pages = new ListDataModel(new ArrayList());

    private boolean showResults = false;

    private long resultsFound = 0;

    private String sortField = "score";

    private String sortOrder = "false";

    private String searchType = "type:Story";

    private SearchResults results = new SearchResults();

    private List<String> filterQueries = new ArrayList<String>();

    private Date dateFrom;

    private Date dateTo;

    private boolean criteriaDate = false;

    private boolean criteriaType = true;
    
    private long archiveSize = -1;

    public Search() {
    }

    @PostConstruct
    public void onInit() {
        reset();
    }

    public long getArchiveSize() {
        if (archiveSize < 0) {
            archiveSize = searchEngine.search("*:*", 1, 1).getNumberOfResults();
        }
        return archiveSize;
    }
    
    

    private void reset() {
        this.keyword = "";
        this.searchResults = new ListDataModel();
        this.pages = new ListDataModel(new ArrayList());
        this.showResults = false;
        this.resultsFound = 0;

        this.sortField = getUser().getDefaultSearchResultsSortBy();
        if (this.sortField == null) {
            this.sortField = "score";
        }

        this.sortOrder = Boolean.toString(
                getUser().isDefaultSearchResultsOrder());

        this.searchType = "type:Story";
        this.results = new SearchResults();
        this.filterQueries = new ArrayList<String>();
        this.dateFrom = null;
        this.dateTo = null;
        this.criteriaDate = false;
        this.criteriaType = true;
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
     * Keyword (query) to be searched for
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

    public void onGenerateOverview(ActionEvent event) {
        // Fetch 1000 results max
        conductSearch(getKeyword(), 0, 1000);
        byte[] output = searchEngine.generateReport(this.results);

        String filename = JsfUtils.getResourceBundle(Bundle.i18n.name()).getString(
                "Search_OVERVIEW_REPORT_FILENAME");

        HttpServletResponse response = (HttpServletResponse) FacesContext.
                getCurrentInstance().getExternalContext().getResponse();
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-disposition", "attachment; filename="
                + filename);
        try {
            ServletOutputStream out = response.getOutputStream();
            out.write(output);
            out.flush();
            out.close();
        } catch (IOException ex) {

            JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_ERROR, Bundle.i18n.name(), "Search_COULD_NOT_OVERVIEW_GENERATE_REPORT", new Object[]{ex.
                    getMessage()});
        }

        FacesContext faces = FacesContext.getCurrentInstance();
        faces.responseComplete();
    }

    /**
     * Event handler for starting the search.
     *
     * @param event Event that invoked the handler
     */
    public void onSearch(ActionEvent event) {
        if (StringUtils.isEmpty(getKeyword())) {
            setKeyword("*:*");
        }
        filterQueries = new ArrayList<String>();
        conductSearch(keyword, 0, 10);
    }

    public void onClear(ActionEvent event) {
        reset();
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
            conductSearch(keyword, 0, 10);
        }
    }

    public void onRemoveFacet(ActionEvent event) {
        String filterQuery =
                JsfUtils.getRequestParameterMap().get("filterQuery");
        if (filterQuery != null) {
            filterQueries.remove(filterQuery);
            conductSearch(keyword, 0, 10);
        }
    }

    private void conductSearch(String keyword, int start, int rows) {
        searchResults = new ListDataModel(new ArrayList());
        pages = new ListDataModel(new ArrayList());

        List<String> filters = new ArrayList<String>();
        if (isCriteriaType()) {
            filters.add(searchType);
        }
        filters.addAll(filterQueries);

        this.results = searchEngine.search(keyword, start, rows, sortField,
                Boolean.valueOf(sortOrder), getDateFrom(), getDateTo(), filters.
                toArray(new String[filters.size()]));

        if (results.getNumberOfResults() == 0) {
            showResults = false;
            searchResults = new ListDataModel(new ArrayList());
            JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_INFO, Bundle.i18n.name(),
                    "Search_NO_RESULTS_FOUND", new Object[]{getKeyword()});
        } else {

            for (SearchResult hit : results.getHits()) {
                hit.setLink(
                        MessageFormat.format(hit.getLink(),
                        new Object[]{
                            JsfUtils.getValueOfValueExpression(
                            "#{facesContext.externalContext.request.contextPath}")}));
            }
            searchResults = new ListDataModel(results.getHits());

            for (long l = 0; l < results.getNumberOfPages(); l++) {
                if (l >= 20) {
                    break;
                }
                SearchPage page = new SearchPage((l + 1), l * results.
                        getResultsPerPage(), results.getNumberOfPages());
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

    public String getSearchType() {
        return searchType;
    }

    public void setSearchType(String searchType) {
        this.searchType = searchType;
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

    /**
     * Gets the {@link Date} for which the search results must not be earlier.
     *
     * @return {@link Date} for which the search results must not be earlier
     */
    public Date getDateFrom() {
        return dateFrom;
    }

    /**
     * Sets the {@link Date} for which the search results must not be earlier.
     *
     * @param dateFrom {@link Date} for which the search results must not be
     * earlier
     */
    public void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
    }

    /**
     * Gets the {@link Date} for which the search results must not be older.
     *
     * @return {@link Date} for which the search results must not be older
     */
    public Date getDateTo() {
        return dateTo;
    }

    /**
     * Sets the {@link Date} for which the search results must not be older.
     *
     * @param dateTo {@link Date} for which the search results must not be older
     */
    public void setDateTo(Date dateTo) {
        this.dateTo = dateTo;
    }

    public boolean isCriteriaDate() {
        return criteriaDate;
    }

    public void setCriteriaDate(boolean criteriaDate) {
        this.criteriaDate = criteriaDate;
    }

    public boolean isCriteriaType() {
        return criteriaType;
    }

    public void setCriteriaType(boolean criteriaType) {
        this.criteriaType = criteriaType;
    }

    private UserAccount getUser() {
        return (UserAccount) JsfUtils.getValueOfValueExpression(
                "#{userSession.user}");
    }
    
    /**
     * Event handler for removing an item from the search engine.
     * 
     * @param searchResult {@link SearchResult} to remove from the search engine
     */
    public void setRemoveItem(SearchResult searchResult) {
        searchEngine.removeItem(searchResult.getId());
        JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_INFO, "i18n",
                    "Search_REMOVE_CONFIRM", new Object[]{});
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
}
