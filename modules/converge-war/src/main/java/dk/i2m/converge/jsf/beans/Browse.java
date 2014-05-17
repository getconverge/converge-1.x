/*
 *  Copyright (C) 2010 Interactive Media Management
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

import dk.i2m.converge.domain.search.SearchResult;
import dk.i2m.converge.domain.search.SearchResults;
import dk.i2m.converge.ejb.facades.SearchEngineLocal;
import dk.i2m.jsf.JsfUtils;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import org.apache.commons.lang.StringUtils;

/**
 * JSF backing bean for browsing pages.
 *
 * @author Allan Lykke Christensen
 */
public class Browse {

    @EJB private SearchEngineLocal searchEngine;

    private DataModel searchResults = new ListDataModel();

    private DataModel pages = new ListDataModel(new ArrayList());

    private SearchResults results = new SearchResults();

    private List<String> filterQueries = new ArrayList<String>();

    private int resultsPerPage = 50;

    private String selectedFacet = "subject";

    public Browse() {
    }

    @PostConstruct
    public void onInit() {
        conductSearch("*:*", 0, resultsPerPage);
    }

    public Map<String, String> getFacets() {
        Map<String, String> facets = new LinkedHashMap<String, String>();
        for (String facet : results.getAvailableFacets()) {
            facets.put(StringUtils.capitalize(facet), facet);
        }
        return facets;
    }

    public String getSelectedFacet() {
        return selectedFacet;
    }

    public void setSelectedFacet(String selectedFacet) {
        this.selectedFacet = selectedFacet;
    }
    

    /**
     * Gets the search results.
     *
     * @return {@link DataModel} containing the {@link SearchResult}s
     */
    public DataModel getSearchResults() {
        return searchResults;
    }

    public void onChangePage(ActionEvent event) {
        String changePage = JsfUtils.getRequestParameterMap().get("changePage");
        if (changePage != null) {
            int newPage = Integer.valueOf(changePage);
            int newStart = (newPage - 1)     * results.getResultsPerPage();
            int rows = results.getResultsPerPage();
            conductSearch("*:*", newStart, rows);
        }
    }

    public void onAddFacet(ActionEvent event) {
        String filterQuery = JsfUtils.getRequestParameterMap().get("filterQuery");
        if (filterQuery != null) {
            filterQueries.add(filterQuery);
            conductSearch("*:*", 0, resultsPerPage);
        }
    }

    public void onRemoveFacet(ActionEvent event) {
        String filterQuery = JsfUtils.getRequestParameterMap().get("filterQuery");
        if (filterQuery != null) {
            filterQueries.remove(filterQuery);
            conductSearch("*:*", 0, resultsPerPage);
        }
    }

    private void conductSearch(String keyword, int start, int rows) {
        searchResults = new ListDataModel(new ArrayList());
        pages = new ListDataModel(new ArrayList());

        this.results = searchEngine.search(keyword, start, rows, filterQueries.toArray(new String[filterQueries.size()]));

        if (results.getNumberOfResults() == 0) {
            searchResults = new ListDataModel(new ArrayList());
        } else {

            for (SearchResult hit : results.getHits()) {
                hit.setLink(MessageFormat.format(hit.getLink(), new Object[]{JsfUtils.getValueOfValueExpression("#{facesContext.externalContext.request.contextPath}")}));
            }
            searchResults = new ListDataModel(results.getHits());

            for (long l = 0; l < results.getNumberOfPages(); l++) {
                SearchPage page = new SearchPage((l + 1), l * results.getResultsPerPage(), results.getNumberOfPages());
                ((ArrayList) getPages().getWrappedData()).add(page);
            }
        }
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
