/*
 * Copyright (C) 2010 - 2011 Interactive Media Management
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package dk.i2m.converge.plugins.atomexport;

import com.sun.syndication.feed.synd.*;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedOutput;
import dk.i2m.converge.core.content.NewsItem;
import dk.i2m.converge.core.content.NewsItemActor;
import dk.i2m.converge.core.content.NewsItemPlacement;
import dk.i2m.converge.core.plugin.EditionAction;
import dk.i2m.converge.core.plugin.PluginContext;
import dk.i2m.converge.core.workflow.Edition;
import dk.i2m.converge.core.workflow.OutletEditionAction;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.language.DefaultTemplateLexer;
import org.apache.commons.lang.StringEscapeUtils;

/**
 * Outlet Edition Action for exporting editions to ATOM feeds.
 *
 * @author Allan Lykke Christensen
 */
@dk.i2m.converge.core.annotations.OutletAction
public class AtomExportAction implements EditionAction {

    private static final DateFormat DATE_PARSER = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss");

    private static final Logger LOG = Logger.getLogger(AtomExportAction.class.
            getName());

    private static final String DEFAULT_FEED_TYPE = "atom_1.0";

    private static final String PROPERTY_FEED_TYPE = "feed.type";

    private static final String PROPERTY_FEED_TITLE = "feed.title";

    private static final String PROPERTY_FEED_DESCRIPTION = "feed.description";

    private static final String PROPERTY_FEED_LINK = "feed.link";

    private static final String PROPERTY_FEED_OUTPUT = "feed.output";

    private ResourceBundle bundle = ResourceBundle.getBundle(
            "dk.i2m.converge.plugins.atomexport.Messages");

    private Map<String, String> availableProperties = null;

    @Override
    public void execute(PluginContext ctx, Edition edition,
            OutletEditionAction action) {
        Map<String, String> properties = action.getPropertiesAsMap();
        validateProperties(properties);

        Map<String, Object> templateAttributes = new HashMap<String, Object>();
        templateAttributes.put("edition", edition);

        String propTitle = compileTemplate(properties.get(PROPERTY_FEED_TITLE),
                templateAttributes);
        String propDescription = compileTemplate(properties.get(
                PROPERTY_FEED_DESCRIPTION), templateAttributes);
        String propFeedLink = compileTemplate(properties.get(
                PROPERTY_FEED_LINK), templateAttributes);
        String propOutput = compileTemplate(properties.get(
                PROPERTY_FEED_OUTPUT), templateAttributes);
        String propFeedType = properties.get(PROPERTY_FEED_TYPE);

        SyndFeed feed = new SyndFeedImpl();
        feed.setFeedType(propFeedType);
        feed.setTitle(propTitle);
        feed.setDescription(propDescription);
        feed.setLink(propFeedLink);
        feed.setLanguage(edition.getOutlet().getLanguage().getCode());

        List<SyndEntry> entries = new ArrayList<SyndEntry>();
        SyndEntry entry;
        SyndContent description;

        for (NewsItemPlacement nip : edition.getPlacements()) {
            NewsItem newsItem = nip.getNewsItem();

            entry = new SyndEntryImpl();
            entry.setTitle(StringEscapeUtils.escapeHtml(newsItem.getTitle()));
            entry.setLink(propFeedLink);
            entry.setPublishedDate(edition.getPublicationDate().getTime());
            description = new SyndContentImpl();
            description.setType("text/plain");
            description.setValue(StringEscapeUtils.escapeHtml(
                    newsItem.getBrief()));
            entry.setDescription(description);

            SyndContent story = new SyndContentImpl();
            story.setType("text/html");
            story.setValue(newsItem.getStory());

            entry.setContents(new ArrayList<SyndContent>());
            entry.getContents().add(story);

            List<SyndCategory> categories = new ArrayList<SyndCategory>();
            if (nip.getSection() != null) {
                SyndCategory category = new SyndCategoryImpl();
                category.setName(nip.getSection().getName());
                categories.add(category);
                entry.setCategories(categories);
            }

            if (newsItem.getByLine().trim().isEmpty()) {
                for (NewsItemActor actor : newsItem.getActors()) {
                    if (actor.getRole().equals(edition.getOutlet().getWorkflow().
                            getStartState().getActorRole())) {
                        if (entry.getAuthor() != null && !entry.getAuthor().
                                isEmpty()) {
                            entry.setAuthor(entry.getAuthor() + ", ");
                        }
                        entry.setAuthor(actor.getUser().getFullName());
                    }
                }
            } else {
                entry.setAuthor(newsItem.getByLine());
            }

            entry.setPublishedDate(edition.getPublicationDate().getTime());
            entry.setUpdatedDate(newsItem.getUpdated().getTime());
            entry.setUri("" + newsItem.getId());

            entries.add(entry);
        }
        feed.setEntries(entries);

        Writer writer;
        try {
            writer = new FileWriter(propOutput);
            SyndFeedOutput output = new SyndFeedOutput();
            output.output(feed, writer);
            writer.close();
        } catch (FeedException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public boolean isSupportEditionExecute() {
        return true;
    }

    @Override
    public Map<String, String> getAvailableProperties() {
        if (availableProperties == null) {
            addProperty(PROPERTY_FEED_TITLE);
            addProperty(PROPERTY_FEED_DESCRIPTION);
            addProperty(PROPERTY_FEED_LINK);
            addProperty(PROPERTY_FEED_TYPE);
            addProperty(PROPERTY_FEED_OUTPUT);
        }
        return availableProperties;
    }

    private void addProperty(String property) {
        if (availableProperties == null) {
            availableProperties = new LinkedHashMap<String, String>();
        }
        availableProperties.put(bundle.getString(property), property);
    }

    @Override
    public String getName() {
        return bundle.getString("PLUGIN_NAME");
    }

    @Override
    public String getAbout() {
        return bundle.getString("PLUGIN_ABOUT");
    }

    @Override
    public String getDescription() {
        return bundle.getString("PLUGIN_DESCRIPTION");
    }

    @Override
    public String getVendor() {
        return bundle.getString("PLUGIN_VENDOR");
    }

    @Override
    public Date getDate() {
        try {
            return DATE_PARSER.parse(bundle.getString("PLUGIN_BUILD_TIME"));
        } catch (Exception ex) {
            return Calendar.getInstance().getTime();
        }
    }

    /**
     * Compiles a given template with the given attributes.
     *
     * @param template   Template to compile
     * @param attributes Attributes to interpolate
     * @return Compiled template
     */
    private String compileTemplate(String template,
            Map<String, Object> attributes) {
        StringTemplate stringTemplate = new StringTemplate(template,
                DefaultTemplateLexer.class);
        for (String attr : attributes.keySet()) {
            stringTemplate.setAttribute(attr, attributes.get(attr));
        }
        return stringTemplate.toString();
    }

    private void validateProperties(Map<String, String> properties) {
        if (!properties.containsKey(PROPERTY_FEED_TITLE)) {
            properties.put(PROPERTY_FEED_TITLE, "");
        }

        if (!properties.containsKey(PROPERTY_FEED_DESCRIPTION)) {
            properties.put(PROPERTY_FEED_DESCRIPTION, "");
        }

        if (!properties.containsKey(PROPERTY_FEED_LINK)) {
            properties.put(PROPERTY_FEED_LINK, "");
        }

        if (!properties.containsKey(PROPERTY_FEED_OUTPUT)) {
            properties.put(PROPERTY_FEED_OUTPUT, "");
        }

        if (!properties.containsKey(PROPERTY_FEED_TYPE)) {
            properties.put(PROPERTY_FEED_TYPE, DEFAULT_FEED_TYPE);
        }
    }

    @Override
    public ResourceBundle getBundle() {
        return bundle;
    }

    @Override
    public void executePlacement(PluginContext ctx, NewsItemPlacement placement,
            Edition edition, OutletEditionAction action) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public boolean isSupportPlacementExecute() {
        return false;
    }
}
