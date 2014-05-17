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
package dk.i2m.converge.domain.search;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Contains the state of a search.
 *
 * @author Allan Lykke Christensen
 */
public class SearchResults {

    private List<SearchResult> hits = new ArrayList<SearchResult>();

    private Map<String, List<SearchFacet>> facets = new LinkedHashMap<String, List<SearchFacet>>();

    private String query = "";

    private long start = 0;

    private long numberOfResults = 0;

    private int resultsPerPage = 10;

    private long searchTime = 0;
    
    private String suggestion = "";

    /**
     * Creates a new instance of {@link SearchResults}.
     */
    public SearchResults() {
    }

    /**
     * Get the number of pages in the search results.
     *
     * @return Number of pages in the search results
     */
    public long getNumberOfPages() {
        if (numberOfResults <= resultsPerPage) {
            return 1;
        } else {
            long pageCount = (numberOfResults + resultsPerPage - 1) / resultsPerPage;
            return pageCount;
        }
    }

    /**
     * Gets the page currently contained in the {@link SearchResults}.
     *
     * @return Page currently contained in the {@link SearchResults}
     */
    public long getCurrentPage() {
        return start / resultsPerPage;
    }

    /**
     * Get the first record displayed in the results (zero-based).
     *
     * @return First record displayed in the results (zero-based)
     */
    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public List<SearchResult> getHits() {
        return hits;
    }

    public void setHits(List<SearchResult> hits) {
        this.hits = hits;
    }

    public long getNumberOfResults() {
        return numberOfResults;
    }

    public void setNumberOfResults(long numberOfResults) {
        this.numberOfResults = numberOfResults;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public int getResultsPerPage() {
        return resultsPerPage;
    }

    public void setResultsPerPage(int resultsPerPage) {
        this.resultsPerPage = resultsPerPage;
    }

    public boolean isFacetsAvailable() {
        return !facets.isEmpty();
    }

    public Map<String, List<SearchFacet>> getFacets() {
        return facets;
    }

    public void setFacets(Map<String, List<SearchFacet>> facets) {
        this.facets = facets;
    }

    public String[] getAvailableFacets() {
        return getFacets().keySet().toArray(new String[getFacets().size()]);
    }

    public long getSearchTime() {
        return searchTime;
    }

    public void setSearchTime(long searchTime) {
        this.searchTime = searchTime;
    }

    public double getSearchTimeInSeconds() {
        return ((double) searchTime) / 1000.0;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }
}
