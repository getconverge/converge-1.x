/*
 *  Copyright (C) 2010 - 2013 Interactive Media Management
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
package dk.i2m.converge.ejb.facades;

import dk.i2m.converge.core.ConfigurationKey;
import dk.i2m.converge.core.DataNotFoundException;
import dk.i2m.converge.core.content.NewsItem;
import dk.i2m.converge.core.content.NewsItemActor;
import dk.i2m.converge.core.content.NewsItemPlacement;
import dk.i2m.converge.core.content.catalogue.MediaItem;
import dk.i2m.converge.core.content.catalogue.MediaItemRendition;
import dk.i2m.converge.core.metadata.*;
import dk.i2m.converge.core.search.IndexQueueEntry;
import dk.i2m.converge.core.search.QueueEntryOperation;
import dk.i2m.converge.core.search.QueueEntryType;
import dk.i2m.converge.core.search.SearchEngineIndexingException;
import dk.i2m.converge.core.security.UserAccount;
import dk.i2m.converge.core.utils.BeanComparator;
import dk.i2m.converge.domain.search.IndexField;
import dk.i2m.converge.domain.search.SearchFacet;
import dk.i2m.converge.domain.search.SearchResult;
import dk.i2m.converge.domain.search.SearchResults;
import dk.i2m.converge.ejb.services.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.*;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFHeader;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.usermodel.HeaderFooter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest.METHOD;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.BinaryRequestWriter;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.tika.Tika;

/**
 * Stateless session bean implementing a search engine service.
 *
 * @author <a href="mailto:allan@i2m.dk">Allan Lykke Christensen</a>
 */
@Stateless
public class SearchEngineBean implements SearchEngineLocal {

    private static final Logger LOG = Logger.getLogger(SearchEngineBean.class.getName());
    @EJB
    private ConfigurationServiceLocal cfgService;
    @EJB
    private UserFacadeLocal userFacade;
    @EJB
    private DaoServiceLocal daoService;
    @EJB
    private NewsItemFacadeLocal newsItemFacade;
    @EJB
    private CatalogueFacadeLocal catalogueFacade;
    @EJB
    private MetaDataServiceLocal metaDataService;
    @Resource
    private SessionContext ctx;
    private DateFormat solrDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    @Override
    public IndexQueueEntry addToIndexQueue(QueueEntryType type, Long id, QueueEntryOperation operation) {
        IndexQueueEntry entry = new IndexQueueEntry(type, id, operation);

        Map<String, Object> params = QueryBuilder.
                with("entryId", entry.getId()).
                and("type", entry.getType()).
                and("operation", entry.getOperation()).parameters();

        List<IndexQueueEntry> entries = daoService.findWithNamedQuery(IndexQueueEntry.FIND_BY_TYPE_ID_AND_OPERATION, params);

        if (entries.isEmpty()) {
            return daoService.create(entry);
        } else {
            return entries.iterator().next();
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public List<IndexQueueEntry> getIndexQueue() {
        List<IndexQueueEntry> queue = daoService.findAll(IndexQueueEntry.class);
        Collections.sort(queue, new BeanComparator("added", false));
        return queue;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void removeFromQueue(Long id) {
        daoService.delete(IndexQueueEntry.class, id);
    }

    /**
     * Remove an item from the search engine.
     *
     * @param id Unique identifier of the item to remove
     */
    @Override
    public void removeItem(Long id) {
        try {
            getSolrServer().deleteById(String.valueOf(id));
        } catch (SolrServerException ex) {
            LOG.log(Level.SEVERE, ex.getMessage());
            LOG.log(Level.FINEST, "", ex);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, ex.getMessage());
            LOG.log(Level.FINEST, "", ex);
        }
    }

    @Override
    public void processIndexingQueue() {
        SolrServer solrServer = getSolrServer();
        List<IndexQueueEntry> items = getIndexQueue();
        for (IndexQueueEntry entry : items) {
            if (entry.getOperation().equals(QueueEntryOperation.REMOVE)) {
                try {
                    solrServer.deleteById(String.valueOf(entry.getEntryId()));
                    removeFromQueue(entry.getId());
                } catch (Exception ex) {
                    LOG.log(Level.WARNING, "{0} #{1} could not be removed from index", new Object[]{entry.getType().name(), entry.getEntryId()});
                    LOG.log(Level.FINEST, "", ex);
                }
            } else {
                switch (entry.getType()) {
                    case NEWS_ITEM:
                        try {
                            NewsItem newsItem = newsItemFacade.findNewsItemById(entry.getEntryId());
                            index(newsItem, solrServer);
                            removeFromQueue(entry.getId());
                        } catch (DataNotFoundException ex) {
                            LOG.log(Level.WARNING, "NewsItem #{0} does not exist in the database. Skipping indexing.", entry.getEntryId());
                            removeFromQueue(entry.getId());
                        } catch (SearchEngineIndexingException ex) {
                            LOG.log(Level.WARNING, "NewsItem #{0} could not be indexed. {1}", new Object[]{entry.getEntryId(), ex.getMessage()});
                            LOG.log(Level.FINEST, "", ex);
                        }
                        break;
                    case MEDIA_ITEM:
                        try {
                            MediaItem mediaItem = catalogueFacade.findMediaItemById(entry.getEntryId());
                            index(mediaItem, solrServer);
                            removeFromQueue(entry.getId());
                        } catch (DataNotFoundException ex) {
                            LOG.log(Level.WARNING, "MediaItem #{0} does not exist in the database. Skipping indexing.", entry.getEntryId());
                            removeFromQueue(entry.getId());
                        } catch (SearchEngineIndexingException ex) {
                            LOG.log(Level.WARNING, "MediaItem #{0} could not be indexed. {1}", new Object[]{entry.getEntryId(), ex.getMessage()});
                            LOG.log(Level.FINEST, "", ex);
                        }
                        break;
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SearchResults search(String query, int start, int rows, String... filterQueries) {
        return search(query, start, rows, "score", false, filterQueries);
    }

    @Override
    public SearchResults search(String query, int start, int rows, String sortField, boolean sortOrder, String... filterQueries) {
        return search(query, start, rows, "score", false, null, null, filterQueries);
    }

    /**
     * Queries the search engine.
     *
     * @param query Query string
     * @param start First record to retrieve
     * @param rows Number of rows to retrieve
     * @param sortField Field to sort by
     * @param sortOrder Ascending ({@code true}) or descending ({@code false})
     * @param dateFrom Search results must not be older than this date
     * @param dateTo Search results must not be newer than this date
     * @param filterQueries Filter queries to include in the search
     * @return {@link SearchResults} matching the {@code query}
     */
    @Override
    public SearchResults search(String query, int start, int rows, String sortField, boolean sortOrder, Date dateFrom, Date dateTo, String... filterQueries) {
        long startTime = System.currentTimeMillis();
        SearchResults searchResults = new SearchResults();
        try {
            final DateFormat ORIGINAL_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            final DateFormat NEW_FORMAT = new SimpleDateFormat("MMMM yyyy");

            SolrQuery solrQuery = new SolrQuery();
            solrQuery.setStart(start);
            solrQuery.setRows(rows);

            StringBuilder queryString = new StringBuilder(query);

            // Check if the query has date restrictions
            if (dateFrom != null || dateTo != null) {

                // Construct date query
                if (!query.isEmpty()) {
                    queryString.append(" AND date:");
                }

                if (dateFrom == null) {
                    queryString.append("[* TO ");
                } else {
                    queryString.append("[");
                    queryString.append(solrDateFormat.format(dateFrom));
                    queryString.append(" TO ");
                }

                if (dateTo == null) {
                    queryString.append("*]");
                } else {
                    queryString.append(solrDateFormat.format(dateTo));
                    queryString.append("]");
                }
            }

            solrQuery.setQuery(queryString.toString());

            solrQuery.setFacet(true);

            if (sortOrder) {
                solrQuery.setSortField(sortField, SolrQuery.ORDER.asc);
            } else {
                solrQuery.setSortField(sortField, SolrQuery.ORDER.desc);
            }

            solrQuery.addFacetField(IndexField.TYPE.getName());
            solrQuery.addFacetField(IndexField.OUTLET.getName());
            solrQuery.addFacetField(IndexField.REPOSITORY.getName());
            solrQuery.addFacetField(IndexField.SECTION.getName());
            solrQuery.addFacetField(IndexField.SUBJECT.getName());
            solrQuery.addFacetField(IndexField.ORGANISATION.getName());
            solrQuery.addFacetField(IndexField.PERSON.getName());
            solrQuery.addFacetField(IndexField.LOCATION.getName());
            solrQuery.addFacetField(IndexField.POINT_OF_INTEREST.getName());



            solrQuery.addFilterQuery(filterQueries);
            solrQuery.setFacetMinCount(1);
            solrQuery.setIncludeScore(true);
            solrQuery.setHighlight(true).setHighlightSnippets(1); //set other params as needed
            solrQuery.setParam("hl.fl", "title,story,caption");
            solrQuery.setParam("hl.fragsize", "500");
            solrQuery.setParam("hl.simple.pre", "<span class=\"searchHighlight\">");
            solrQuery.setParam("hl.simple.post", "</span>");
            solrQuery.setParam("facet.date", "date");
            solrQuery.setParam("facet.date.start", "NOW/YEAR-10YEAR");
            solrQuery.setParam("facet.date.end", "NOW");
            solrQuery.setParam("facet.date.gap", "+1MONTH");

            SolrServer srv = getSolrServer();

            // POST is used to support UTF-8
            QueryResponse qr = srv.query(solrQuery, METHOD.POST);
            SolrDocumentList sdl = qr.getResults();
            searchResults.setNumberOfResults(sdl.getNumFound());

            for (SolrDocument d : sdl) {

                // Copy all fields to map for easy access
                HashMap<String, Object> values = new HashMap<String, Object>();

                for (Iterator<Map.Entry<String, Object>> i = d.iterator(); i.hasNext();) {
                    Map.Entry<String, Object> e2 = i.next();
                    values.put(e2.getKey(), e2.getValue());
                }

                String type = (String) values.get("type");

                SearchResult hit = null;
                if ("Story".equalsIgnoreCase(type)) {
                    hit = generateStoryHit(qr, values);
                } else if ("Media".equalsIgnoreCase(type)) {
                    hit = generateMediaHit(qr, values);
                }

                generateTags(hit, qr, values);

                if (hit != null) {
                    hit.setScore((Float) d.getFieldValue("score"));
                    searchResults.getHits().add(hit);
                }
            }

            List<FacetField> facets = qr.getFacetFields();

            for (FacetField facet : facets) {
                List<FacetField.Count> facetEntries = facet.getValues();
                if (facetEntries != null) {
                    for (FacetField.Count fcount : facetEntries) {
                        if (!searchResults.getFacets().containsKey(facet.getName())) {
                            searchResults.getFacets().put(facet.getName(), new ArrayList<SearchFacet>());
                        }

                        SearchFacet sf = new SearchFacet(fcount.getName(), fcount.getAsFilterQuery(), fcount.getCount());

                        // Check if the filter query is already active
                        for (String fq : filterQueries) {
                            if (fq.equals(fcount.getAsFilterQuery())) {
                                sf.setSelected(true);
                            }
                        }

                        // Ensure that the facet is not already there
                        if (!searchResults.getFacets().get(facet.getName()).contains(sf)) {
                            searchResults.getFacets().get(facet.getName()).add(sf);
                        }
                    }
                }
            }

            for (FacetField facet : qr.getFacetDates()) {
                List<FacetField.Count> facetEntries = facet.getValues();
                if (facetEntries != null) {
                    for (FacetField.Count fcount : facetEntries) {
                        if (fcount.getCount() != 0) {
                            if (!searchResults.getFacets().containsKey(facet.getName())) {
                                searchResults.getFacets().put(facet.getName(), new ArrayList<SearchFacet>());
                            }

                            String facetLabel = "";
                            try {
                                Date facetDate = ORIGINAL_FORMAT.parse(fcount.getName());
                                facetLabel = NEW_FORMAT.format(facetDate);
                            } catch (ParseException ex) {
                                LOG.log(Level.SEVERE, ex.getMessage());
                                LOG.log(Level.FINEST, "", ex);
                                facetLabel = fcount.getName();
                            }

                            String realFilterQuery = "date:[" + fcount.getName() + " TO " + fcount.getName() + "+1MONTH]";

                            SearchFacet sf = new SearchFacet(facetLabel, realFilterQuery, fcount.getCount());

                            // Check if the filter query is already active
                            for (String fq : filterQueries) {
                                if (fq.equals(realFilterQuery)) {
                                    sf.setSelected(true);
                                }
                            }

                            // Ensure that the facet is not already there
                            if (!searchResults.getFacets().get(facet.getName()).contains(sf)) {
                                searchResults.getFacets().get(facet.getName()).add(sf);
                            }
                        }
                    }
                }
            }

        } catch (SolrServerException ex) {
            LOG.log(Level.SEVERE, ex.getMessage());
            LOG.log(Level.FINEST, "", ex);
        }
        long endTime = System.currentTimeMillis();

        searchResults.setSearchTime(endTime - startTime);
        searchResults.setStart(start);
        searchResults.setResultsPerPage(rows);

        return searchResults;
    }

    /**
     * Generates an overview reports of a set of {@link SearchResults}. The
     * search results will be extracted (fetched) so that it is not just the
     * partial set of {@link SearchResults} that will be included in the report.
     * <p/>
     * @param results {@link SearchResults} for which to generate the report
     * @return Binary data representing the report
     */
    @Override
    public byte[] generateReport(SearchResults results) {
        ResourceBundle i18n;
        try {
            String uid = ctx.getCallerPrincipal().getName();
            UserAccount user = userFacade.findById(uid);
            Locale userLocale = user.getPreferredLocale();
            i18n = ResourceBundle.getBundle("dk.i2m.converge.i18n.ServiceMessages", userLocale);
        } catch (DataNotFoundException ex) {
            i18n = ResourceBundle.getBundle("dk.i2m.converge.i18n.ServiceMessages");
        }

        String lblSheetName = i18n.getString("SearchEngineBean_generateReport_SHEET_NAME");
        String lblHeaderLeft = i18n.getString("SearchEngineBean_generateReport_HEADER_LEFT");
        String lblHeaderRight = i18n.getString("SearchEngineBean_generateReport_HEADER_RIGHT");
        String lblFooterLeft = i18n.getString("SearchEngineBean_generateReport_FOOTER_LEFT");
        String lblFooterRight = i18n.getString("SearchEngineBean_generateReport_FOOTER_RIGHT");
        String lblDateFormat = i18n.getString("SearchEngineBean_generateReport_DATE_FORMAT");
        String lblRowHeaderId = i18n.getString("SearchEngineBean_generateReport_ROW_HEADER_ID");
        String lblRowHeaderDate = i18n.getString("SearchEngineBean_generateReport_ROW_HEADER_DATE");
        String lblRowHeaderTitle = i18n.getString("SearchEngineBean_generateReport_ROW_HEADER_TITLE");
        String lblRowHeaderOutlet = i18n.getString("SearchEngineBean_generateReport_ROW_HEADER_OUTLET");
        String lblRowHeaderSection = i18n.getString("SearchEngineBean_generateReport_ROW_HEADER_SECTION");

        HSSFWorkbook wb = new HSSFWorkbook();

        String sheetName = WorkbookUtil.createSafeSheetName(lblSheetName);
        int overviewSheetRow = 0;

        Font storyFont = wb.createFont();
        storyFont.setFontHeightInPoints((short) 12);
        storyFont.setBoldweight(Font.BOLDWEIGHT_NORMAL);

        // Create style with borders
        CellStyle style = wb.createCellStyle();
        style.setBorderBottom(CellStyle.BORDER_THIN);
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderLeft(CellStyle.BORDER_THIN);
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderRight(CellStyle.BORDER_THIN);
        style.setRightBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderTop(CellStyle.BORDER_THIN);
        style.setTopBorderColor(IndexedColors.BLACK.getIndex());

        // Create style for date cells
        CreationHelper createHelper = wb.getCreationHelper();
        CellStyle dateStyle = wb.createCellStyle();
        dateStyle.setDataFormat(createHelper.createDataFormat().getFormat(lblDateFormat));
        dateStyle.setBorderBottom(CellStyle.BORDER_THIN);
        dateStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        dateStyle.setBorderLeft(CellStyle.BORDER_THIN);
        dateStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        dateStyle.setBorderRight(CellStyle.BORDER_THIN);
        dateStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
        dateStyle.setBorderTop(CellStyle.BORDER_THIN);
        dateStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());

        HSSFSheet overviewSheet = wb.createSheet(sheetName);

        // Create sheet header
        HSSFHeader sheetHeader = overviewSheet.getHeader();
        sheetHeader.setLeft(lblHeaderLeft);
        sheetHeader.setRight(lblHeaderRight);

        // Create sheet footer
        Footer footer = overviewSheet.getFooter();
        String footerLeft = MessageFormat.format(lblFooterLeft, new Object[]{HeaderFooter.page(), HeaderFooter.numPages()});
        String footerRight = MessageFormat.format(lblFooterRight, new Object[]{HeaderFooter.date(), HeaderFooter.time()});
        footer.setLeft(footerLeft);
        footer.setRight(footerRight);

        // Freeze the header row
        overviewSheet.createFreezePane(0, 1, 0, 1);

        Row row = overviewSheet.createRow(0);
        row.createCell(0).setCellValue(lblRowHeaderId);
        row.getCell(0).setCellStyle(style);
        row.createCell(1).setCellValue(lblRowHeaderDate);
        row.getCell(1).setCellStyle(style);
        row.createCell(2).setCellValue(lblRowHeaderTitle);
        row.getCell(2).setCellStyle(style);
        row.createCell(3).setCellValue(lblRowHeaderOutlet);
        row.getCell(3).setCellStyle(style);
        row.createCell(4).setCellValue(lblRowHeaderSection);
        row.getCell(4).setCellStyle(style);

        overviewSheetRow++;
        for (SearchResult result : results.getHits()) {
            try {
                NewsItem newsItem = newsItemFacade.findNewsItemFromArchive(result.getId());

                if (newsItem.getPlacements().isEmpty()) {
                    row = overviewSheet.createRow(overviewSheetRow);
                    row.createCell(0).setCellValue(result.getId());
                    row.getCell(0).setCellStyle(style);
                    row.createCell(1).setCellValue(newsItem.getUpdated());
                    row.getCell(1).setCellStyle(dateStyle);
                    row.createCell(2).setCellValue(newsItem.getTitle());
                    row.getCell(2).setCellStyle(style);
                    row.createCell(3).setCellValue(newsItem.getOutlet().getTitle());
                    row.getCell(3).setCellStyle(style);
                    row.createCell(4).setCellValue("");
                    row.getCell(4).setCellStyle(style);
                } else {
                    for (NewsItemPlacement nip : newsItem.getPlacements()) {
                        try {
                            row = overviewSheet.createRow(overviewSheetRow);
                            row.createCell(0).setCellValue(result.getId());
                            row.getCell(0).setCellStyle(style);
                            row.createCell(1).setCellValue(nip.getEdition().getPublicationDate());
                            row.getCell(1).setCellStyle(dateStyle);
                            row.createCell(2).setCellValue(newsItem.getTitle());
                            row.getCell(2).setCellStyle(style);
                            row.createCell(3).setCellValue(nip.getOutlet().getTitle());
                            row.getCell(3).setCellStyle(style);
                            row.createCell(4).setCellValue(nip.getSection().getFullName());
                            row.getCell(4).setCellStyle(style);
                        } catch (Exception ex) {
                            LOG.log(Level.INFO, "Failed to output line in report. {0}", ex.getMessage());
                            LOG.log(Level.FINEST, "", ex);
                        }
                    }
                }

                overviewSheetRow++;
            } catch (DataNotFoundException ex) {
            }
        }

        // Auto-size
        for (int i = 0; i <= 2; i++) {
            overviewSheet.autoSizeColumn(i);
        }

        wb.setRepeatingRowsAndColumns(0, 0, 0, 0, 0);
        overviewSheet.setFitToPage(true);
        overviewSheet.setAutobreaks(true);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            wb.write(baos);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, ex.getMessage());
            LOG.log(Level.FINEST, "", ex);
        }

        return baos.toByteArray();
    }

    /**
     * Communicate to the Solr server that the search engine index should be
     * updated.
     * <p/>
     * @throws SearchEngineIndexingException If an unexpected response was
     * received from the Solr server
     */
    @Override
    public void optimizeIndex() throws SearchEngineIndexingException {
        try {
            getSolrServer().optimize();
        } catch (SolrServerException ex) {
            throw new SearchEngineIndexingException(ex);
        } catch (IOException ex) {
            throw new SearchEngineIndexingException(ex);
        }
    }

    /**
     * Generates a {link SearchResult} for a media item.
     *
     * @param qr QueryResponse from Solr
     * @param values Fields available
     * @return {@link SearchResult}
     */
    private SearchResult generateMediaHit(QueryResponse qr, HashMap<String, Object> values) {
        String id = (String) values.get(IndexField.ID.getName());

        StringBuilder caption = new StringBuilder("");
        StringBuilder title = new StringBuilder("");
        StringBuilder note = new StringBuilder("");

        Map<String, List<String>> highlighting = qr.getHighlighting().get(id);

        boolean highlightingExist = highlighting != null;

        if (highlightingExist && highlighting.get(IndexField.STORY.getName()) != null) {
            for (String hl : highlighting.get(IndexField.STORY.getName())) {
                caption.append(hl);
            }
        } else if (highlighting.get(IndexField.STORY.getName()) != null) {
            caption.append(StringUtils.abbreviate((String) values.get(IndexField.STORY.getName()), 500));
        } else {
            caption.append(StringUtils.abbreviate((String) values.get(IndexField.CAPTION.getName()), 500));
        }

        if (highlightingExist && highlighting.get(IndexField.TITLE.getName()) != null) {
            for (String hl : qr.getHighlighting().get(id).get(IndexField.TITLE.getName())) {
                title.append(hl);
            }
        } else {
            title.append((String) values.get(IndexField.TITLE.getName()));
        }

        String format = (String) values.get(IndexField.MEDIA_FORMAT.getName());

        note.append((String) values.get(IndexField.TYPE.getName()));
        note.append(" - ");
        note.append(format);
        note.append(" - ");
        note.append((String) values.get(IndexField.REPOSITORY.getName()));

        SearchResult hit = new SearchResult();
        hit.setId(Long.valueOf(id));
        hit.setTitle(title.toString());
        hit.setDescription(caption.toString());
        hit.setNote(note.toString());
        hit.setLink("{0}/MediaItemArchive.xhtml?id=" + values.get(IndexField.ID.getName()));
        hit.setType((String) values.get(IndexField.TYPE.getName()));
        hit.setFormat(format);

        if (values.containsKey(IndexField.THUMB_URL.getName())) {
            hit.setPreview(true);
            hit.setPreviewLink((String) values.get(IndexField.THUMB_URL.getName()));
            hit.setDirectLink((String) values.get(IndexField.DIRECT_URL.getName()));

            try {
                Tika tika = new Tika();
                String contentType = tika.detect(new URL(hit.getPreviewLink()));
                hit.setPreviewContentType(contentType);
            } catch (IOException ex) {
                LOG.log(Level.WARNING, "Could not set the content type of the preview link. {0}", new Object[]{ex.getMessage()});
            }

        } else {
            hit.setPreview(false);
        }

        if (values.containsKey(IndexField.DATE.getName())) {
            if (values.get(IndexField.DATE.getName()) instanceof List) {
                hit.setDates((List<Date>) values.get(IndexField.DATE.getName()));
            } else {
                hit.addDate((Date) values.get(IndexField.DATE.getName()));
            }
        }

        return hit;
    }

    /**
     * Generates a {link SearchResult} for a story.
     *
     * @param qr QueryResponse from Solr
     * @param values Fields available
     * @return {@link SearchResult}
     */
    private SearchResult generateStoryHit(QueryResponse qr, HashMap<String, Object> values) {
        String id = (String) values.get(IndexField.ID.getName());

        StringBuilder story = new StringBuilder();
        StringBuilder title = new StringBuilder();
        StringBuilder note = new StringBuilder();

        Map<String, List<String>> highlighting = qr.getHighlighting().get(id);

        boolean highlightingExist = highlighting != null;

        if (highlightingExist && highlighting.get(IndexField.STORY.getName()) != null) {
            for (String hl : highlighting.get(IndexField.STORY.getName())) {
                story.append(hl);
            }
        } else {
            story.append(StringUtils.abbreviate((String) values.get(IndexField.STORY.getName()), 500));
        }

        if (highlightingExist && highlighting.get(IndexField.TITLE.getName()) != null) {
            for (String hl : qr.getHighlighting().get(id).get(IndexField.TITLE.getName())) {
                title.append(hl);
            }
        } else {
            title.append((String) values.get(IndexField.TITLE.getName()));
        }

        note.append((String) values.get(IndexField.TYPE.getName()));
        note.append(" - Words: ");

        if (values.containsKey(IndexField.WORD_COUNT.getName())) {
            note.append(String.valueOf(values.get(IndexField.WORD_COUNT.getName())));
        } else {
            note.append("Unknown");
        }


        note.append("<br/>");

        if (values.containsKey(IndexField.PLACEMENT.getName())) {
            if (values.get(IndexField.PLACEMENT.getName()) instanceof String) {
                note.append(values.get(IndexField.PLACEMENT.getName()));
            } else if (values.get(IndexField.PLACEMENT.getName()) instanceof List) {
                List<String> placements = (List<String>) values.get(IndexField.PLACEMENT.getName());
                for (String placement : placements) {
                    note.append(placement);
                    note.append("<br/>");
                }
            } else {
                LOG.warning("Unexpected value returned from search engine");
            }
        }

        SearchResult hit = new SearchResult();
        hit.setId(Long.valueOf(id));
        hit.setTitle(title.toString());
        hit.setDescription(story.toString());
        hit.setNote(note.toString());
        hit.setLink("{0}/NewsItemArchive.xhtml?id=" + id);
        hit.setType((String) values.get(IndexField.TYPE.getName()));


        return hit;
    }

    /**
     * Gets the instance of the Apache Solr server used for indexing.
     *
     * @return Instance of the Apache Solr server
     * @throws IllegalStateException If the search engine is not properly
     * configured
     */
    private SolrServer getSolrServer() {
        try {
            String url = cfgService.getString(ConfigurationKey.SEARCH_ENGINE_URL);
            Integer socketTimeout = cfgService.getInteger(ConfigurationKey.SEARCH_ENGINE_SOCKET_TIMEOUT);
            Integer connectionTimeout = cfgService.getInteger(ConfigurationKey.SEARCH_ENGINE_CONNECTION_TIMEOUT);
            Integer maxTotalConnectionsPerHost = cfgService.getInteger(ConfigurationKey.SEARCH_ENGINE_MAX_TOTAL_CONNECTIONS_PER_HOST);
            Integer maxTotalConnections = cfgService.getInteger(ConfigurationKey.SEARCH_ENGINE_MAX_TOTAL_CONNECTIONS);
            Integer maxRetries = cfgService.getInteger(ConfigurationKey.SEARCH_ENGINE_MAX_RETRIES);
            Boolean followRedirects = cfgService.getBoolean(ConfigurationKey.SEARCH_ENGINE_FOLLOW_REDIRECTS);
            Boolean allowCompression = cfgService.getBoolean(ConfigurationKey.SEARCH_ENGINE_ALLOW_COMPRESSION);

            CommonsHttpSolrServer solrServer = new CommonsHttpSolrServer(url);
            solrServer.setRequestWriter(new BinaryRequestWriter());
            solrServer.setSoTimeout(socketTimeout);
            solrServer.setConnectionTimeout(connectionTimeout);
            solrServer.setDefaultMaxConnectionsPerHost(maxTotalConnectionsPerHost);
            solrServer.setMaxTotalConnections(maxTotalConnections);
            solrServer.setFollowRedirects(followRedirects);
            solrServer.setAllowCompression(allowCompression);
            solrServer.setMaxRetries(maxRetries);

            return solrServer;
        } catch (MalformedURLException ex) {
            LOG.log(Level.SEVERE, "Invalid search engine configuration. {0}", ex.getMessage());
            LOG.log(Level.FINEST, "", ex);
            throw new IllegalStateException("Invalid search engine configuration", ex);
        }
    }

    private void generateTags(SearchResult hit, QueryResponse qr, HashMap<String, Object> values) {

        if (values.containsKey(IndexField.DATE.getName())) {
            if (values.get(IndexField.DATE.getName()) instanceof Date) {
                hit.addDate((Date) values.get(IndexField.DATE.getName()));
            } else if (values.get(IndexField.DATE.getName()) instanceof List) {
                hit.setDates((List<Date>) values.get(IndexField.DATE.getName()));
            } else {
                LOG.warning("Unexpected value returned from search engine");
            }
        }

        List<String> tags = new ArrayList<String>();
        if (values.containsKey(IndexField.CONCEPT.getName())) {
            if (values.get(IndexField.CONCEPT.getName()) instanceof String) {
                Object tag = values.get(IndexField.CONCEPT.getName());
                tags.add((String) tag);
            } else if (values.get(IndexField.CONCEPT.getName()) instanceof List) {
                tags = (List<String>) values.get(IndexField.CONCEPT.getName());
            } else {
                LOG.warning("Unexpected value returned from search engine");
            }
        }

        hit.setTags(tags.toArray(new String[tags.size()]));
    }

    private void index(NewsItem ni, SolrServer solrServer) throws SearchEngineIndexingException {

        SolrInputDocument solrDoc = new SolrInputDocument();
        solrDoc.addField(IndexField.ID.getName(), ni.getId(), 1.0f);
        solrDoc.addField(IndexField.TITLE.getName(), ni.getTitle(), 1.0f);
        solrDoc.addField(IndexField.TYPE.getName(), "Story");
        solrDoc.addField(IndexField.BYLINE.getName(), ni.getByLine());
        solrDoc.addField(IndexField.BRIEF.getName(), ni.getBrief());
        solrDoc.addField(IndexField.STORY.getName(), dk.i2m.converge.core.utils.StringUtils.stripHtml(ni.getStory()));
        try {
            solrDoc.addField(IndexField.LANG.getName(), ni.getLanguage().getCode());
        } catch (NullPointerException ex) {
        }
        solrDoc.addField(IndexField.LANGUAGE.getName(), ni.getLanguage().getName());
        solrDoc.addField(IndexField.WORD_COUNT.getName(), ni.getWordCount());

        for (NewsItemPlacement placement : ni.getPlacements()) {
            if (placement.getEdition() != null) {
                if (placement.getEdition().getPublicationDate() != null) {
                    solrDoc.addField(IndexField.DATE.getName(), placement.getEdition().getPublicationDate().getTime());
                }
                solrDoc.addField(IndexField.EDITION_NUMBER.getName(), placement.getEdition().getNumber());
                solrDoc.addField(IndexField.EDITION_VOLUME.getName(), placement.getEdition().getVolume());
            }
            if (placement.getSection() != null) {
                solrDoc.addField(IndexField.SECTION.getName(), placement.getSection().getFullName());
            }
            if (placement.getOutlet() != null) {
                solrDoc.addField(IndexField.OUTLET.getName(), placement.getOutlet().getTitle());
            }
            solrDoc.addField(IndexField.PLACEMENT.getName(), placement.toString());
        }

        for (NewsItemActor actor : ni.getActors()) {
            solrDoc.addField(IndexField.ACTOR.getName(), actor.getUser().getFullName());
            // Dynamic fields for the actors role
            solrDoc.addField(actor.getRole().getName(), actor.getUser().getFullName());
        }

        for (Concept concept : ni.getConcepts()) {
            if (concept instanceof Subject) {
                solrDoc.addField(IndexField.SUBJECT.getName(), concept.getFullTitle());
            }
            if (concept instanceof Person) {
                solrDoc.addField(IndexField.PERSON.getName(), concept.getFullTitle());
            }

            if (concept instanceof Organisation) {
                solrDoc.addField(IndexField.ORGANISATION.getName(), concept.getFullTitle());
            }

            if (concept instanceof GeoArea) {
                solrDoc.addField(IndexField.LOCATION.getName(), concept.getFullTitle());
            }

            if (concept instanceof PointOfInterest) {
                solrDoc.addField(IndexField.POINT_OF_INTEREST.getName(), concept.getFullTitle());
            }

            solrDoc.addField(IndexField.CONCEPT.getName(), concept.getFullTitle());
        }
        try {
            solrServer.add(solrDoc);
        } catch (SolrServerException ex) {
            throw new SearchEngineIndexingException(ex);
        } catch (IOException ex) {
            throw new SearchEngineIndexingException(ex);
        }
    }

    public void index(MediaItem mi, SolrServer solrServer) throws SearchEngineIndexingException {

        if (mi.isOriginalAvailable()) {
            MediaItemRendition mir = mi.getOriginal();

            SolrInputDocument solrDoc = new SolrInputDocument();
            solrDoc.addField(IndexField.ID.getName(), mi.getId(), 1.0f);
            solrDoc.addField(IndexField.TYPE.getName(), "Media");

            String mediaFormat;
            String contentType = mi.getOriginal().getContentType();
            String story = "";

            if (mir.isAudio()) {
                mediaFormat = "Audio";
            } else if (mir.isVideo()) {
                mediaFormat = "Video";
            } else if (mir.isImage()) {
                mediaFormat = "Image";
            } else if (mir.isDocument()) {
                mediaFormat = "Document";
                story = metaDataService.extractContent(mir);
            } else {
                mediaFormat = "Unknown";
            }

            solrDoc.addField(IndexField.MEDIA_FORMAT.getName(), mediaFormat);
            solrDoc.addField(IndexField.TITLE.getName(), mi.getTitle(), 1.0f);
            solrDoc.addField(IndexField.BYLINE.getName(), mi.getByLine());
            solrDoc.addField(IndexField.STORY.getName(), dk.i2m.converge.core.utils.StringUtils.stripHtml(mi.getDescription()) + " " + story);
            solrDoc.addField(IndexField.CAPTION.getName(), dk.i2m.converge.core.utils.StringUtils.stripHtml(mi.getDescription()));
            solrDoc.addField(IndexField.CONTENT_TYPE.getName(), mi.getOriginal().getContentType());
            solrDoc.addField(IndexField.REPOSITORY.getName(), mi.getCatalogue().getName());

            if (mi.getMediaDate() != null) {
                solrDoc.addField(IndexField.DATE.getName(), mi.getMediaDate().getTime());
            }

            if (mi.isPreviewAvailable()) {
                solrDoc.addField(IndexField.THUMB_URL.getName(), mi.getPreview().getAbsoluteFilename());
                solrDoc.addField(IndexField.DIRECT_URL.getName(), mi.getPreview().getFileLocation());
            }

            solrDoc.addField(IndexField.ACTOR.getName(), mi.getOwner().getFullName());

            for (Concept concept : mi.getConcepts()) {
                if (concept instanceof Subject) {
                    solrDoc.addField(IndexField.SUBJECT.getName(), concept.getFullTitle());
                }
                if (concept instanceof Person) {
                    solrDoc.addField(IndexField.PERSON.getName(), concept.getFullTitle());
                }

                if (concept instanceof Organisation) {
                    solrDoc.addField(IndexField.ORGANISATION.getName(), concept.getFullTitle());
                }

                if (concept instanceof GeoArea) {
                    solrDoc.addField(IndexField.LOCATION.getName(), concept.getFullTitle());
                }

                if (concept instanceof PointOfInterest) {
                    solrDoc.addField(IndexField.POINT_OF_INTEREST.getName(), concept.getFullTitle());
                }

                solrDoc.addField(IndexField.CONCEPT.getName(), concept.getFullTitle());
            }

            try {
                solrServer.add(solrDoc);
            } catch (SolrServerException ex) {
                throw new SearchEngineIndexingException(ex);
            } catch (IOException ex) {
                throw new SearchEngineIndexingException(ex);
            }
        } else {
            LOG.log(Level.INFO, "Ignoring MediaItem #{0}. Missing original {1} rendition", new Object[]{mi.getId(), mi.getCatalogue().getOriginalRendition().getName()});
        }
    }
}
