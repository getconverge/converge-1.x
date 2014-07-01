/*
 * Copyright 2010 - 2013 Interactive Media Management
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
package dk.i2m.converge.ejb.services;

import dk.i2m.converge.core.DataNotFoundException;
import dk.i2m.converge.core.ConfigurationKey;
import dk.i2m.converge.core.content.ContentTag;
import dk.i2m.converge.core.newswire.*;
import dk.i2m.converge.core.plugin.NewswireDecoder;
import dk.i2m.converge.core.plugin.PluginManager;
import dk.i2m.converge.core.search.SearchEngineIndexingException;
import dk.i2m.converge.core.security.UserAccount;
import dk.i2m.converge.core.security.UserRole;
import dk.i2m.converge.domain.search.SearchFacet;
import dk.i2m.converge.domain.search.SearchResult;
import dk.i2m.converge.domain.search.SearchResults;
import dk.i2m.converge.ejb.facades.SystemFacadeLocal;
import dk.i2m.converge.ejb.facades.UserFacadeLocal;
import dk.i2m.converge.ejb.messaging.NewswireDecoderMessageBean;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.*;
import javax.jms.*;
import org.apache.commons.lang.StringUtils;
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

/**
 * Stateless enterprise bean providing access to the external newswire services.
 *
 * @author Allan Lykke Christensen
 */
@Stateless
public class NewswireServiceBean implements NewswireServiceLocal {

    private static final Logger LOG = Logger.getLogger(NewswireServiceBean.class.getName());
    private ResourceBundle msgs = ResourceBundle.getBundle("dk.i2m.converge.i18n.ServiceMessages");
    @EJB
    private ConfigurationServiceLocal cfgService;
    @EJB
    private DaoServiceLocal daoService;
    @EJB
    private UserFacadeLocal userFacade;
    @EJB
    private PluginContextBeanLocal pluginContext;
    @EJB
    private NotificationServiceLocal notificationService;
    @EJB
    private SystemFacadeLocal systemFacade;
    @Resource
    private SessionContext ctx;
    @Resource(mappedName = "jms/newswireServiceQueue")
    private Destination destination;
    @Resource(mappedName = "jms/connectionFactory")
    private ConnectionFactory jmsConnectionFactory;

    @Override
    public NewswireBasket createBasket(NewswireBasket basket) {
        Calendar now = Calendar.getInstance();
        basket.setCreated(now.getTime());
        basket.setUpdated(now.getTime());
        if (basket.isMailDelivery()) {
            Calendar next = (Calendar) now.clone();
            next.set(Calendar.HOUR_OF_DAY, basket.getHourFirstDelivery());
            next.set(Calendar.MINUTE, 0);
            next.set(Calendar.SECOND, 0);
            next.set(Calendar.MILLISECOND, 0);

            while (next.before(now)) {
                next.add(Calendar.HOUR_OF_DAY, basket.getMailFrequency());
            }
            basket.setNextDelivery(next.getTime());
        }

        return daoService.create(basket);
    }

    @Override
    public NewswireBasket updateBasket(NewswireBasket basket) {
        Calendar now = Calendar.getInstance();
        basket.setUpdated(now.getTime());

        if (basket.isMailDelivery()) {
            Calendar next = (Calendar) now.clone();
            next.set(Calendar.HOUR_OF_DAY, basket.getHourFirstDelivery());

            while (next.before(now)) {
                next.add(Calendar.HOUR_OF_DAY, basket.getMailFrequency());
            }
            basket.setNextDelivery(next.getTime());
        }

        return daoService.update(basket);
    }

    @Override
    public void deleteBasket(NewswireBasket basket) {
        if (basket.getId() != null) {
            daoService.delete(NewswireBasket.class, basket.getId());
        }
    }

    /**
     * Gets all the {@link NewswireBasket}s of a particular user.
     *
     * @param userId Unique identifier of the user
     * @return {@link List} of {@link NewswireAlert}s of the particular user
     */
    @Override
    public List<NewswireBasket> findBasketsByUser(Long userId) {
        Map<String, Object> params = QueryBuilder.with("uid", userId).parameters();
        return daoService.findWithNamedQuery(NewswireBasket.FIND_BY_USER, params);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<NewswireItem> getNews() {
        try {
            UserAccount user = userFacade.findById(ctx.getCallerPrincipal().
                    getName());
            Map<String, Object> params = QueryBuilder.with("user", user).
                    parameters();
            return daoService.findWithNamedQuery(NewswireItem.FIND_BY_USER,
                    params);
        } catch (DataNotFoundException ex) {
            return Collections.EMPTY_LIST;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<NewswireItem> getNews(Long newswireServiceId) {
        try {
            NewswireService service = daoService.findById(NewswireService.class,
                    newswireServiceId);
            Map<String, Object> params = QueryBuilder.with("newswireService",
                    service).parameters();
            return daoService.findWithNamedQuery(NewswireItem.FIND_BY_SERVICE,
                    params);
        } catch (DataNotFoundException ex) {
            return Collections.EMPTY_LIST;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<NewswireService> getNewswireServices() {
        return daoService.findAll(NewswireService.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<NewswireService> findActiveNewswireServices() {
        Map<String, Object> params = QueryBuilder.with("active", true).
                parameters();
        return daoService.findWithNamedQuery(NewswireService.FIND_BY_STATUS,
                params);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<NewswireService> getNewswireServicesWithSubscribersAndItems() {
        List<NewswireService> services = getNewswireServices();

        for (NewswireService service : services) {
            try {
                Long subscribers = daoService.findObjectWithNamedQuery(
                        Long.class, NewswireService.COUNT_SUBSCRIBERS,
                        QueryBuilder.with("id", service.getId()).parameters());
                Long items = daoService.findObjectWithNamedQuery(Long.class,
                        NewswireService.COUNT_ITEMS, QueryBuilder.with("id",
                        service.getId()).parameters());
                service.setNumberOfSubscribers(subscribers);
                service.setNumberOfItems(items);

            } catch (Exception ex) {
                LOG.log(Level.WARNING, "Unknown response from query.", ex);
            }
        }

        return services;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NewswireService findById(Long id) throws DataNotFoundException {
        return daoService.findById(NewswireService.class, id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NewswireService create(NewswireService newsFeed) {
        return daoService.create(newsFeed);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(NewswireService newsFeed) {
        daoService.update(newsFeed);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(Long id) throws DataNotFoundException {
        // Remove log entries by the newswire service
        systemFacade.removeLogEntries(NewswireService.class.getName(), "" + id);
        // Remove items of the newswire service
        emptyNewswireService(id);
        // Delete the newswire service
        daoService.delete(NewswireService.class, id);
    }

    @Override
    public void purgeNewswires() {
        SolrServer solrServer = getSolrServer();

        List<NewswireService> services = daoService.findAll(
                NewswireService.class);

        for (NewswireService service : services) {
            deleteExpiredItems(service.getId(), solrServer);
        }
    }

    /**
     * Indexes a {@link NewswireItem} in the database.
     *
     * @param item {@link NewswireItem} to index.
     * @throws SearchEngineIndexingException * If the item could not be indexed
     */
    @Override
    public void index(NewswireItem item) throws SearchEngineIndexingException {
        SolrServer solrServer = getSolrServer();
        index(item, solrServer);
    }

    private void deleteExpiredItems(Long newswireServiceId, SolrServer solrServer) {
        Long taskId = 0L;
        try {
            NewswireService service = daoService.findById(NewswireService.class, newswireServiceId);
            if (service.getDaysToKeep().intValue() < 1) {
                // Ignore
                return;
            }

            taskId = systemFacade.createBackgroundTask("Expiring items from newswire service " + service.getSource());
            try {
                solrServer.deleteByQuery("provider-id:" + newswireServiceId + " AND date:[* TO NOW-" + service.getDaysToKeep() + "DAY/DAY]");
                Calendar expirationDate = Calendar.getInstance();
                expirationDate.add(Calendar.DAY_OF_MONTH, -service.getDaysToKeep());
                QueryBuilder qb = QueryBuilder.
                        with("id", newswireServiceId).
                        and("expirationDate", expirationDate);
                daoService.executeQuery(NewswireService.DELETE_EXPIRED_ITEMS, qb);

            } catch (SolrServerException ex) {
                LOG.log(Level.SEVERE, "Could not remove expired newswire items from index. {0} ", new Object[]{ex.getMessage()});
                LOG.log(Level.FINEST, "", ex);
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, "Could not remove expired newswire items from index. {0} ", new Object[]{ex.getMessage()});
                LOG.log(Level.FINEST, "", ex);
            }

        } catch (DataNotFoundException ex) {
            LOG.log(Level.WARNING, ex.getMessage());
            LOG.log(Level.FINEST, "", ex);
        } finally {
            systemFacade.removeBackgroundTask(taskId);
        }
    }

    private void fetchNewswire(Long newswireServiceId, SolrServer solrServer) {
        Long taskId = 0L;
        try {
            NewswireService service = daoService.findById(NewswireService.class, newswireServiceId);

            startProcessingNewswireService(newswireServiceId);

            taskId = systemFacade.createBackgroundTask("Downloading newswire service "
                    + service.getSource());
            NewswireDecoder decoder = service.getDecoder();
            decoder.decode(pluginContext, service);

            service.setLastFetch(Calendar.getInstance());
            daoService.update(service);
            stopProcessingNewswireService(newswireServiceId);

        } catch (DataNotFoundException ex) {
            LOG.log(Level.WARNING, ex.getMessage());
        } catch (NewswireDecoderException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            systemFacade.removeBackgroundTask(taskId);
        }
    }

    private void index(NewswireItem ni, SolrServer solrServer) throws
            SearchEngineIndexingException {
        SolrInputDocument solrDoc = new SolrInputDocument();
        try {
            solrDoc.addField("id", ni.getId(), 1.0f);
            solrDoc.addField("headline", ni.getTitle(), 1.0f);
            solrDoc.addField("provider", ni.getNewswireService().getSource());
            solrDoc.addField("provider-id", ni.getNewswireService().getId());
            solrDoc.addField("story", dk.i2m.converge.core.utils.StringUtils.
                    stripHtml(ni.getContent()));
            solrDoc.addField("caption", ni.getSummary());
            solrDoc.addField("author", ni.getAuthor());
            solrDoc.addField("date", ni.getDate().getTime());
            if (ni.isThumbnailAvailable()) {
                solrDoc.addField("thumb-url", ni.getThumbnailUrl());
            }

            for (ContentTag tag : ni.getTags()) {
                solrDoc.addField("tag", tag.getTag().toLowerCase());
            }
        } catch (NullPointerException ex) {
            throw new SearchEngineIndexingException(
                    "A value was missing from the content to be indexed. ", ex);
        }

        try {
            solrServer.add(solrDoc);
        } catch (SolrServerException ex) {
            throw new SearchEngineIndexingException(ex);
        } catch (IOException ex) {
            throw new SearchEngineIndexingException(ex);
        }
    }

    /**
     * Schedules the download of all active newswire services.
     */
    @Override
    public void downloadNewswireServices() {
        List<NewswireService> services = findActiveNewswireServices();
        for (NewswireService service : services) {
            try {
                downloadNewswireService(service.getId());
            } catch (DataNotFoundException ex) {
                // Unknown newswire
            }
        }
    }

    /**
     * Schedules the download of a single {@link NewswireService}.
     *
     * @param id Unique identifier of the {@link NewswireService}
     * @throws DataNotFoundException If a {@link NewswireService} with the given
     * id does not exist
     */
    @Override
    public void downloadNewswireService(Long id) throws DataNotFoundException {

        NewswireService service = findById(id);

        if (service.isProcessing()) {
            LOG.log(Level.INFO, "{0} is already being downloaded", service.
                    getSource());
            return;
        } else {
            startProcessingNewswireService(id);
        }

        Connection conn = null;
        try {
            conn = jmsConnectionFactory.createConnection();
            Session session = conn.createSession(true, Session.AUTO_ACKNOWLEDGE);

            MessageProducer producer = session.createProducer(destination);

            MapMessage msg = session.createMapMessage();
            msg.setLongProperty(NewswireDecoderMessageBean.NEWSWIRE_SERVICE_ID,
                    id);
            producer.send(msg);

            session.close();
            conn.close();
        } catch (JMSException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception e) {
                }
            }
        }
        stopProcessingNewswireService(id);
    }

    @Override
    public SearchResults search(String query, int start, int rows,
            String sortField, boolean sortOrder, String... filterQueries) {
        long startTime = System.currentTimeMillis();
        SearchResults searchResults = new SearchResults();
        final DateFormat ORIGINAL_FORMAT = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss'Z'");
        final DateFormat NEW_FORMAT = new SimpleDateFormat("MMMM yyyy");

        try {
            SolrQuery solrQuery = new SolrQuery();
            solrQuery.setStart(start);
            solrQuery.setRows(rows);
            solrQuery.setQuery(query);
            solrQuery.setFacet(true);
            if (sortOrder) {
                solrQuery.setSortField(sortField, SolrQuery.ORDER.asc);
            } else {
                solrQuery.setSortField(sortField, SolrQuery.ORDER.desc);
            }

            solrQuery.addFacetField("provider");
            solrQuery.addFacetField("tag");
            solrQuery.addFilterQuery(filterQueries);
            solrQuery.setFacetMinCount(1);
            solrQuery.setIncludeScore(true);
            solrQuery.setHighlight(true).setHighlightSnippets(1); //set other params as needed
            solrQuery.setParam("hl.fl", "headline,story,caption");
            solrQuery.setParam("hl.fragsize", "500");
            solrQuery.setParam("hl.simple.pre",
                    "<span class=\"searchHighlight\">");
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

                for (Iterator<Map.Entry<String, Object>> i = d.iterator(); i.
                        hasNext();) {
                    Map.Entry<String, Object> e2 = i.next();
                    values.put(e2.getKey(), e2.getValue());
                }


                SearchResult hit = new SearchResult();

                String id = (String) values.get("id");

                StringBuilder story = new StringBuilder();
                StringBuilder title = new StringBuilder();
                StringBuilder note = new StringBuilder();

                Map<String, List<String>> highlighting = qr.getHighlighting().
                        get(id);

                boolean highlightingExist = highlighting != null;

                if (highlightingExist && highlighting.get("caption") != null) {
                    for (String hl : highlighting.get("caption")) {
                        story.append(hl);
                    }
                } else {
                    story.append(StringUtils.abbreviate((String) values.get(
                            "caption"), 500));
                }

                if (highlightingExist && highlighting.get("headline") != null) {
                    for (String hl
                            : qr.getHighlighting().get(id).get("headline")) {
                        title.append(hl);
                    }
                } else {
                    title.append((String) values.get("headline"));
                }

                if (values.containsKey("tag")) {
                    if (values.get("tag") instanceof String) {
                        hit.setTags(new String[]{(String) values.get("tag")});
                    } else if (values.get("tag") instanceof List) {
                        List<String> tags = (List<String>) values.get("tag");
                        hit.setTags(tags.toArray(new String[tags.size()]));
                    } else {
                        LOG.warning(
                                "Unexpected (tag) value returned from search engine");
                    }
                }

                note.append(values.get("provider"));
                if (values.containsKey("author") && values.get("author") != null
                        && !((String) values.get("author")).trim().isEmpty()) {
                    note.append(" - ").append(values.get("author"));
                }

                if (title != null) {
                    hit.setTitle(title.toString());
                }
                if (story != null) {
                    hit.setDescription(story.toString());
                }
                if (note != null) {
                    hit.setNote(note.toString());
                }
                hit.setId(Long.valueOf(id));
                hit.setLink("{0}/Newswire.xhtml?q=id%3A" + id);
                hit.setType("Newswire");

                if (values.containsKey("thumb-url")) {
                    hit.setPreview(true);
                    hit.setPreviewLink((String) values.get("thumb-url"));
                }


                if (values.containsKey("date")) {
                    if (values.get("date") instanceof Date) {
                        hit.addDate((Date) values.get("date"));
                    } else if (values.get("date") instanceof List) {
                        hit.setDates((List<Date>) values.get("date"));
                    } else {
                        LOG.warning(
                                "Unexpected (date) value returned from search engine");
                    }
                }

                hit.setScore((Float) d.getFieldValue("score"));
                searchResults.getHits().add(hit);
            }

            List<FacetField> facets = qr.getFacetFields();

            for (FacetField facet : facets) {
                List<FacetField.Count> facetEntries = facet.getValues();
                if (facetEntries != null) {
                    for (FacetField.Count fcount : facetEntries) {
                        if (!searchResults.getFacets().containsKey(
                                facet.getName())) {
                            searchResults.getFacets().put(facet.getName(),
                                    new ArrayList<SearchFacet>());
                        }

                        SearchFacet sf = new SearchFacet(fcount.getName(),
                                fcount.getAsFilterQuery(), fcount.getCount());

                        // Check if the filter query is already active
                        for (String fq : filterQueries) {
                            if (fq.equals(fcount.getAsFilterQuery())) {
                                sf.setSelected(true);
                            }
                        }

                        // Ensure that the facet is not already there
                        if (!searchResults.getFacets().get(facet.getName()).
                                contains(sf)) {
                            searchResults.getFacets().get(facet.getName()).add(
                                    sf);
                        }
                    }
                }
            }


            for (FacetField facet : qr.getFacetDates()) {
                List<FacetField.Count> facetEntries = facet.getValues();
                if (facetEntries != null) {
                    for (FacetField.Count fcount : facetEntries) {
                        if (fcount.getCount() != 0) {
                            if (!searchResults.getFacets().containsKey(facet.
                                    getName())) {
                                searchResults.getFacets().put(facet.getName(),
                                        new ArrayList<SearchFacet>());
                            }

                            String facetLabel = "";
                            try {
                                Date facetDate = ORIGINAL_FORMAT.parse(fcount.
                                        getName());
                                facetLabel = NEW_FORMAT.format(facetDate);
                            } catch (ParseException ex) {
                                LOG.log(Level.SEVERE, null, ex);
                                facetLabel = fcount.getName();
                            }

                            String realFilterQuery = "date:[" + fcount.getName()
                                    + " TO " + fcount.getName() + "+1MONTH]";

                            SearchFacet sf = new SearchFacet(facetLabel,
                                    realFilterQuery, fcount.getCount());

                            // Check if the filter query is already active
                            for (String fq : filterQueries) {
                                if (fq.equals(realFilterQuery)) {
                                    sf.setSelected(true);
                                }
                            }

                            // Ensure that the facet is not already there
                            if (!searchResults.getFacets().get(facet.getName()).
                                    contains(sf)) {
                                searchResults.getFacets().get(facet.getName()).
                                        add(sf);
                            }
                        }
                    }
                }
            }


        } catch (SolrServerException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        long endTime = System.currentTimeMillis();

        searchResults.setSearchTime(endTime - startTime);
        searchResults.setStart(start);
        searchResults.setResultsPerPage(rows);

        return searchResults;
    }

    /**
     * Empties the items of a given {@link NewswireService}.
     *
     * @param id Unique identifier of the {@link NewswireService}
     * @return Number of items deleted
     */
    @Override
    public int emptyNewswireService(Long id) {
        int affectedRecords = 0;
        try {
            NewswireService ns = daoService.findById(NewswireService.class, id);
            SolrServer solr = getSolrServer();
            for (NewswireItem item : ns.getItems()) {
                try {
                    solr.deleteById(String.valueOf(item.getId()));
                } catch (SolrServerException ex) {
                    LOG.log(Level.SEVERE,
                            "Could not remove newswire item from SolrServer", ex);
                } catch (IOException ex) {
                    LOG.log(Level.SEVERE,
                            "Could not remove newswire item from SolrServer", ex);
                }
            }

            return daoService.executeQuery(NewswireItem.DELETE_BY_SERVICE,
                    QueryBuilder.with("newswireService", ns));
        } catch (DataNotFoundException ex) {
            return affectedRecords;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<NewswireItem> findByExternalId(String externalId) {
        Map<String, Object> params = QueryBuilder.with("externalId", externalId).
                parameters();
        return daoService.findWithNamedQuery(NewswireItem.FIND_BY_EXTERNAL_ID,
                params);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NewswireItem create(NewswireItem item) {
        return daoService.create(item);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, NewswireDecoder> getNewswireDecoders() {
        return PluginManager.getInstance().getNewswireDecoders();
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
            String url = cfgService.getString(
                    ConfigurationKey.SEARCH_ENGINE_NEWSWIRE_URL);
            Integer socketTimeout = cfgService.getInteger(
                    ConfigurationKey.SEARCH_ENGINE_SOCKET_TIMEOUT);
            Integer connectionTimeout = cfgService.getInteger(
                    ConfigurationKey.SEARCH_ENGINE_CONNECTION_TIMEOUT);
            Integer maxTotalConnectionsPerHost =
                    cfgService.getInteger(
                    ConfigurationKey.SEARCH_ENGINE_MAX_TOTAL_CONNECTIONS_PER_HOST);
            Integer maxTotalConnections =
                    cfgService.getInteger(
                    ConfigurationKey.SEARCH_ENGINE_MAX_TOTAL_CONNECTIONS);
            Integer maxRetries = cfgService.getInteger(
                    ConfigurationKey.SEARCH_ENGINE_MAX_RETRIES);
            Boolean followRedirects = cfgService.getBoolean(
                    ConfigurationKey.SEARCH_ENGINE_FOLLOW_REDIRECTS);
            Boolean allowCompression = cfgService.getBoolean(
                    ConfigurationKey.SEARCH_ENGINE_ALLOW_COMPRESSION);

            CommonsHttpSolrServer solrServer = new CommonsHttpSolrServer(url);
            solrServer.setRequestWriter(new BinaryRequestWriter());
            solrServer.setSoTimeout(socketTimeout);
            solrServer.setConnectionTimeout(connectionTimeout);
            solrServer.setDefaultMaxConnectionsPerHost(
                    maxTotalConnectionsPerHost);
            solrServer.setMaxTotalConnections(maxTotalConnections);
            solrServer.setFollowRedirects(followRedirects);
            solrServer.setAllowCompression(allowCompression);
            solrServer.setMaxRetries(maxRetries);

            return solrServer;
        } catch (MalformedURLException ex) {
            LOG.log(Level.SEVERE, "Invalid search engine configuration. {0}",
                    ex.getMessage());
            LOG.log(Level.FINE, "", ex);
            throw new java.lang.IllegalStateException(
                    "Invalid search engine configuration", ex);
        }
    }

    @Override
    public NewswireItem findNewswireItemById(Long id) throws
            DataNotFoundException {
        return daoService.findById(NewswireItem.class, id);
    }

    /**
     * Find a {@link List} of available {@link NewswireService}s for a given
     * {@link UserAccount}.
     *
     * @param id Unique identifier of the {@link UserAccount}
     * @return {@link List} of available {@link NewswireService}s for the
     * {@link UserAccount}
     */
    @Override
    public List<NewswireService> findAvailableNewswireServices(Long id) {
        List<NewswireService> servicesAvailable =
                new ArrayList<NewswireService>();
        try {
            UserAccount ua = daoService.findById(UserAccount.class, id);
            List<NewswireService> services = findActiveNewswireServices();
            for (NewswireService s : services) {
                if (s.isPublic()) {
                    servicesAvailable.add(s);
                } else {
                    for (UserRole role : ua.getUserRoles()) {
                        if (s.getRestrictedTo().contains(role)) {
                            servicesAvailable.add(s);
                            break;
                        }
                    }
                }
            }

            return servicesAvailable;
        } catch (DataNotFoundException ex) {
            return Collections.EMPTY_LIST;
        }
    }

    @Override
    public void dispatchBaskets() {
        Long taskId = 0L;
        taskId = systemFacade.createBackgroundTask(msgs.getString(
                "NewswireServiceBean_BASKET_DISPATCH_TASK"));
        SimpleDateFormat dateFormat = new SimpleDateFormat(msgs.getString(
                "Generic_FORMAT_DATE_AND_TIME"));
        final String NL = System.getProperty("line.separator");
        final String SENDER = cfgService.getString(
                ConfigurationKey.NEWSWIRE_BASKET_MAIL);
        final String HOME_URL = cfgService.getString(
                ConfigurationKey.CONVERGE_HOME_URL);
        final String LBL_READ_MORE = msgs.getString(
                "NewswireServiceBean_BASKET_MAIL_READ_MORE");
        final String LBL_BASKET_SUMMARY = msgs.getString(
                "NewswireServiceBean_BASKET_MAIL_SUMMARY");
        final String LBL_MATCHES = msgs.getString(
                "NewswireServiceBean_BASKET_MAIL_MATCHES");
        final String LBL_SEARCH_TERMS = msgs.getString(
                "NewswireServiceBean_BASKET_MAIL_SEARCH_TERMS");
        final String LBL_TAGS = msgs.getString(
                "NewswireServiceBean_BASKET_MAIL_TAGS");
        final String LBL_SERVICES = msgs.getString(
                "NewswireServiceBean_BASKET_MAIL_SERVICES");
        final String LBL_SERVICES_ALL = msgs.getString(
                "NewswireServiceBean_BASKET_MAIL_SERVICES_ALL");
        final String LBL_BASKET_SUBJECT = msgs.getString(
                "NewswireServiceBean_BASKET_MAIL_SUBJECT");

        Calendar now = Calendar.getInstance();
        List<NewswireBasket> baskets =
                daoService.findWithNamedQuery(
                NewswireBasket.FIND_BY_EMAIL_DISPATCH);

        for (NewswireBasket basket : baskets) {
            dispatchBasket(basket);
        }
        systemFacade.removeBackgroundTask(taskId);
    }

    /**
     * Dispatches a {@link NewswireBasket} via e-mail.
     *
     * @param id Unique identifier of the {@link NewswireBasket}
     * @return {@code true} if the basket has items that was e-mailed, otherwise
     * {@code false}
     */
    @Override
    public boolean dispatchBasket(Long id) {
        try {
            NewswireBasket b = daoService.findById(NewswireBasket.class, id);
            return dispatchBasket(b);
        } catch (DataNotFoundException ex) {
            LOG.log(Level.WARNING, ex.getMessage());
            return false;
        }
    }

    private boolean dispatchBasket(NewswireBasket basket) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(msgs.getString(
                "Generic_FORMAT_DATE_AND_TIME"));
        final String NL = System.getProperty("line.separator");
        final String SENDER = cfgService.getString(
                ConfigurationKey.NEWSWIRE_BASKET_MAIL);
        final String HOME_URL = cfgService.getString(
                ConfigurationKey.CONVERGE_HOME_URL);
        final String LBL_READ_MORE = msgs.getString(
                "NewswireServiceBean_BASKET_MAIL_READ_MORE");
        final String LBL_BASKET_SUMMARY = msgs.getString(
                "NewswireServiceBean_BASKET_MAIL_SUMMARY");
        final String LBL_MATCHES = msgs.getString(
                "NewswireServiceBean_BASKET_MAIL_MATCHES");
        final String LBL_SEARCH_TERMS = msgs.getString(
                "NewswireServiceBean_BASKET_MAIL_SEARCH_TERMS");
        final String LBL_TAGS = msgs.getString(
                "NewswireServiceBean_BASKET_MAIL_TAGS");
        final String LBL_SERVICES = msgs.getString(
                "NewswireServiceBean_BASKET_MAIL_SERVICES");
        final String LBL_SERVICES_ALL = msgs.getString(
                "NewswireServiceBean_BASKET_MAIL_SERVICES_ALL");
        final String LBL_BASKET_SUBJECT = msgs.getString(
                "NewswireServiceBean_BASKET_MAIL_SUBJECT");

        Calendar now = Calendar.getInstance();

        Calendar lastDelivery = (Calendar) now.clone();
        if (basket.getLastDelivery() == null) {
            lastDelivery.add(Calendar.HOUR_OF_DAY, -24);
            basket.setLastDelivery(now.getTime());
        } else {
            lastDelivery.setTime(basket.getLastDelivery());
        }

        Calendar next = (Calendar) now.clone();
        next.set(Calendar.MINUTE, 0);
        next.set(Calendar.SECOND, 0);
        next.set(Calendar.MILLISECOND, 0);
        next.add(Calendar.HOUR_OF_DAY, basket.getMailFrequency());
        basket.setNextDelivery(next.getTime());

        StringBuilder query = new StringBuilder();
        query.append("date:[NOW-").
                append(basket.getMailFrequency()).
                append("HOURS ").
                append(" TO NOW]");

        if (!basket.getQuery().trim().isEmpty()) {
            query.append(" && ");
            query.append(basket.getQuery());
        }

        SearchResults searchResults;
        try {
            searchResults = search(query.toString(), 0, 1000, "score", false,
                    new String[]{});
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Problem dispatching basket #{0} with query {1}. {2}",
                    new Object[]{basket.getId(), query.toString(), ex.getMessage()});
            return false;
        }

        if (searchResults.getHits().size() > 0) {
            dateFormat.setTimeZone(basket.getOwner().getTimeZone());

            StringBuilder htmlContent = new StringBuilder();
            StringBuilder plainContent = new StringBuilder();

            htmlContent.append("<html><head><title>").
                    append(basket.getTitle()).
                    append("</title>").
                    append("</head><body><h1>").
                    append(basket.getTitle()).
                    append("</h1>");

            plainContent.append(basket.getTitle().toUpperCase()).
                    append(NL).append(NL);

            for (SearchResult sr : searchResults.getHits()) {
                String link = HOME_URL + "/NewswireItem.xhtml?id=" + sr.getId();

                htmlContent.append(
                        "<h2 style=\"margin-bottom: 0px; padding-top: 10px;\">").
                        append(sr.getTitle()).
                        append("</h2>");
                htmlContent.append("<p style=\"margin-top: 0px;\">");
                htmlContent.append(
                        " <span style=\"font-size: 0.9em; color: #0E774A; font-weight: bold; text-transform: uppercase;\">").
                        append(sr.getNote()).
                        append("</span>");
                htmlContent.append(
                        " <span style=\"font-size: 0.9em; color: #4272DB; text-transform:  uppercase;\">&#160;").
                        append(dateFormat.format(sr.getLatestDate())).append(
                        "</span>");
                htmlContent.append("</p>");
                htmlContent.append("<p style=\"margin-top: 0px;\">");
                if (sr.isPreview()) {
                    htmlContent.append("<a href=\"").append(link).append(
                            "\"><img src=\"").append(sr.getPreviewLink()).
                            append(
                            "\" style=\"border: 1px solid black; margin-right: 5px; margin-top: 5px;\" align=\"left\" alt=\"\" title=\"\" /></a>");
                }

                htmlContent.append(sr.getDescription()).append("</p>");
                htmlContent.append("<p><a href=\"").append(link).append(
                        "\">").append(LBL_READ_MORE).append(
                        "</a></p><div style=\"clear: both;\" />");

                plainContent.append(sr.getTitle().toUpperCase()).append(NL);
                plainContent.append(sr.getNote().toUpperCase()).append(" ").
                        append(sr.getLatestDate()).append(NL);
                plainContent.append(dk.i2m.converge.core.utils.StringUtils.
                        stripHtml(sr.getDescription()).trim()).append(NL).
                        append(NL);
                plainContent.append(LBL_READ_MORE).append(" ").append(link).
                        append(NL).append("===").append(NL);
            }

            htmlContent.append("<hr/><h3>").append(LBL_BASKET_SUMMARY).
                    append("</h3>");
            plainContent.append(LBL_BASKET_SUMMARY.toUpperCase()).append(NL);

            htmlContent.append("<p>").append(LBL_MATCHES).append(searchResults.
                    getNumberOfResults()).append("</p>");
            plainContent.append(LBL_MATCHES).append(searchResults.
                    getNumberOfResults()).append(NL);

            htmlContent.append("<p>").append(LBL_SEARCH_TERMS).append(basket.
                    getSearchTerm()).append("</p>");
            plainContent.append(LBL_SEARCH_TERMS).append(searchResults.
                    getNumberOfResults()).append(NL);

            if (!basket.getTags().isEmpty()) {
                htmlContent.append("<p>").append(LBL_TAGS).append("<ul>");
                plainContent.append(LBL_TAGS).append(NL);
                for (ContentTag tag : basket.getTags()) {
                    htmlContent.append("<li>").append(tag.getTag()).append(
                            "</li>");
                    plainContent.append("* ").append(tag.getTag()).append(NL);
                }
                htmlContent.append("</ul></p>");
                plainContent.append(NL);
            }

            htmlContent.append("<p>").append(LBL_SERVICES).append("<ul>");
            plainContent.append(LBL_SERVICES).append(NL);

            if (basket.getAppliesTo().isEmpty()) {
                htmlContent.append("<li>").append(LBL_SERVICES_ALL).append(
                        "</li>");
                plainContent.append("* ").append(LBL_SERVICES_ALL);
            } else {
                for (NewswireService service : basket.getAppliesTo()) {
                    htmlContent.append("<li>").append(service.getSource()).
                            append("</li>");
                    plainContent.append("* ").append(service.getSource()).
                            append(NL);
                }
            }

            htmlContent.append("</ul></p>");
            plainContent.append(NL);

            htmlContent.append("</body></html>");

            // Generate subject
            String subject = compileMsg(LBL_BASKET_SUBJECT,
                    new Object[]{searchResults.getHits().size(),
                basket.getTitle()},
                    basket.getOwner().getPreferredLocale());

            // Dispatch mail
            notificationService.dispatchMail(basket.getOwner().getEmail(),
                    SENDER, subject, htmlContent.toString(), plainContent.
                    toString());
            return true;
        } else {
            return false;
        }
    }

    private String compileMsg(String pattern, Object[] arguments,
            Locale userLocale) {
        MessageFormat fmt = new MessageFormat(pattern);
        if (userLocale != null) {
            fmt.setLocale(userLocale);
        }
        return fmt.format(arguments);
    }

    @Override
    public NewswireItemAttachment findNewswireItemAttachmentById(Long id) throws
            DataNotFoundException {
        return daoService.findById(NewswireItemAttachment.class, id);
    }

    /**
     * Removes a given item from the newswire service and the search engine.
     *
     * @param id * Unique identifier of the newswire item
     */
    @Override
    public void removeItem(Long id) {
        daoService.delete(NewswireItem.class, id);
        SolrServer solr = getSolrServer();
        try {
            solr.deleteById(String.valueOf(id));
        } catch (SolrServerException ex) {
            LOG.log(Level.SEVERE,
                    "Could not remove newswire item from SolrServer", ex);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE,
                    "Could not remove newswire item from SolrServer", ex);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @Override
    public void startProcessingNewswireService(Long id) {
        try {
            NewswireService service = daoService.findById(NewswireService.class,
                    id);
            service.setProcessing(true);
            daoService.update(service);
        } catch (DataNotFoundException ex) {
            LOG.log(Level.WARNING, ex.getMessage());
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @Override
    public void stopProcessingNewswireService(Long id) {
        try {
            NewswireService service = daoService.findById(NewswireService.class,
                    id);
            service.setProcessing(false);
            daoService.update(service);
        } catch (DataNotFoundException ex) {
            LOG.log(Level.WARNING, ex.getMessage());
        }
    }
}
