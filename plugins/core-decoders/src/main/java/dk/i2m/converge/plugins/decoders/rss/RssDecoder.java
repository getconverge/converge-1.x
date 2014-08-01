/*
 * Copyright (C) 2010 - 2013 Interactive Media Management
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
package dk.i2m.converge.plugins.decoders.rss;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import dk.i2m.converge.core.DataExistsException;
import dk.i2m.converge.core.EnrichException;
import dk.i2m.converge.core.content.ContentTag;
import dk.i2m.converge.core.logging.LogSeverity;
import dk.i2m.converge.core.metadata.Concept;
import dk.i2m.converge.core.newswire.NewswireDecoderException;
import dk.i2m.converge.core.newswire.NewswireItem;
import dk.i2m.converge.core.newswire.NewswireService;
import dk.i2m.converge.core.plugin.NewswireDecoder;
import dk.i2m.converge.core.plugin.PluginContext;
import dk.i2m.converge.core.search.SearchEngineIndexingException;
import dk.i2m.converge.core.utils.StringUtils;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Newswire decoder for RSS feeds. The decoder has a single property, the
 * {@link RssDecoder#URL} of the RSS feed.
 *
 * @author Allan Lykke Christensen
 */
@dk.i2m.converge.core.annotations.NewswireDecoder
public class RssDecoder implements NewswireDecoder {

    public enum Property {

        ENABLE_OPEN_CALAIS, URL
    }
    /**
     * Number of seconds to wait for a connection to the RSS feed server.
     */
    public static final int CONNECTION_TIMEOUT = 60;
    /**
     * Number of seconds to wait for the feed to be downloaded.
     */
    public static final int READ_TIMEOUT = 60 * 3;
    private Map<String, String> availableProperties = null;
    private ResourceBundle bundle = ResourceBundle.getBundle("dk.i2m.converge.plugins.decoders.rss.Messages");
    private PluginContext pluginContext;
    private NewswireService newswireService;
    private boolean useOpenCalais = false;

    /**
     * Creates a new instance of {@link RssDecoder}.
     */
    public RssDecoder() {
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void decode(PluginContext ctx, NewswireService newswire) throws NewswireDecoderException {
        this.pluginContext = ctx;
        this.newswireService = newswire;
        String url = newswire.getPropertiesMap().get(Property.URL.name());

        if (newswire.getPropertiesMap().containsKey(Property.ENABLE_OPEN_CALAIS.name())) {
            useOpenCalais = Boolean.parseBoolean(newswire.getPropertiesMap().get(Property.ENABLE_OPEN_CALAIS.name()));
        } else {
            useOpenCalais = false;
        }

        ctx.log(LogSeverity.INFO, "Downloading webfeed {0} {1}", new Object[]{newswire.getSource(), url}, this.newswireService, this.newswireService.getId());

        int duplicates = 0;
        int newItems = 0;

        try {
            URL feedSource = new URL(url);
            URLConnection feedConnection = feedSource.openConnection();
            feedConnection.setConnectTimeout(CONNECTION_TIMEOUT * 1000);
            feedConnection.setReadTimeout(READ_TIMEOUT * 1000);

            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed = input.build(new XmlReader(feedConnection));

            for (SyndEntry entry : (List<SyndEntry>) feed.getEntries()) {
                try {
                    create(entry, newswire);
                    newItems++;
                } catch (DataExistsException dee) {
                    duplicates++;
                }
            }
        } catch (MalformedURLException ex) {
            throw new NewswireDecoderException(ex);
        } catch (IllegalArgumentException ex) {
            throw new NewswireDecoderException(ex);
        } catch (FeedException ex) {
            throw new NewswireDecoderException(ex);
        } catch (IOException ex) {
            throw new NewswireDecoderException(ex);
        }

        ctx.log(LogSeverity.INFO,
                "{2} had {0} {0, choice, 0#duplicates|1#duplicate|2#duplicates} and {1} new {1, choice, 0#items|1#item|2#items} ",
                new Object[]{
                    duplicates, newItems, newswire.getSource()},
                this.newswireService,
                this.newswireService.getId());
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Map<String, String> getAvailableProperties() {
        if (availableProperties == null) {
            availableProperties = new HashMap<String, String>();
            for (Property p : Property.values()) {
                availableProperties.put(bundle.getString(p.name()), p.name());
            }
        }
        return this.availableProperties;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getName() {
        return bundle.getString("PLUGIN_NAME");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getAbout() {
        return bundle.getString("PLUGIN_ABOUT");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getDescription() {
        return bundle.getString("PLUGIN_DESCRIPTION");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getVendor() {
        return bundle.getString("PLUGIN_VENDOR");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Date getDate() {
        final String FORMAT = "yyyy-MM-dd HH:mm:ss";
        try {

            SimpleDateFormat format = new SimpleDateFormat(FORMAT);
            return format.parse(bundle.getString("PLUGIN_BUILD_TIME"));
        } catch (ParseException ex) {
            return Calendar.getInstance().getTime();
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ResourceBundle getBundle() {
        return bundle;
    }

    private void create(SyndEntry entry, NewswireService source) throws DataExistsException {
        List<NewswireItem> results = pluginContext.findNewswireItemsByExternalId(entry.getUri());

        if (results.isEmpty()) {

            NewswireItem item = new NewswireItem();
            item.setExternalId(entry.getUri());
            item.setTitle(StringUtils.stripHtml(entry.getTitle()));
            if (entry.getDescription() != null) {
                item.setSummary(StringUtils.stripHtml(entry.getDescription().getValue()));
            }
            item.setUrl(entry.getLink());
            item.setNewswireService(source);
            item.setAuthor(entry.getAuthor());
            Calendar now = Calendar.getInstance();
            item.setDate(now);
            item.setUpdated(now);

            if (entry.getPublishedDate() != null) {
                item.getDate().setTime(entry.getPublishedDate());
                item.getUpdated().setTime(entry.getPublishedDate());
            }

            if (entry.getUpdatedDate() != null) {
                item.getUpdated().setTime(entry.getUpdatedDate());
            }

            if (useOpenCalais) {
                enrich(pluginContext, item);
            }
            NewswireItem nwi = pluginContext.createNewswireItem(item);
            try {
                pluginContext.index(nwi);
            } catch (SearchEngineIndexingException ex) {
                pluginContext.log(LogSeverity.SEVERE, ex.getMessage(),
                        this.newswireService, this.newswireService.getId());
            }
        } else {
            throw new DataExistsException("NewswireItem with external id [" + entry.getUri() + "] already downloaded");
        }
    }

    private void enrich(PluginContext ctx, NewswireItem item) {
        StringBuilder story = new StringBuilder();
        story.append(item.getTitle());
        story.append(item.getSummary());
        story.append(item.getContent());
        try {
            List<Concept> concepts = ctx.enrich(story.toString());

            for (Concept concept : concepts) {
                ContentTag tag = ctx.findOrCreateContentTag(concept.getName());
                if (!item.getTags().contains(tag)) {
                    item.getTags().add(tag);
                }
            }
        } catch (EnrichException ex) {
            ctx.log(LogSeverity.WARNING, ex.getMessage(), this.newswireService,
                    this.newswireService.getId());
        }
    }
}
