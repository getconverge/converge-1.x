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
package dk.i2m.converge.plugins.indexedition;

import dk.i2m.converge.core.content.NewsItem;
import dk.i2m.converge.core.content.NewsItemPlacement;
import dk.i2m.converge.core.logging.LogSeverity;
import dk.i2m.converge.core.plugin.EditionAction;
import dk.i2m.converge.core.plugin.PluginContext;
import dk.i2m.converge.core.search.SearchEngineIndexingException;
import dk.i2m.converge.core.workflow.Edition;
import dk.i2m.converge.core.workflow.OutletEditionAction;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * {@link EditionAction} for indexing the news items of an {@link Edition}.
 *
 * @author Allan Lykke Christensen
 */
@dk.i2m.converge.core.annotations.OutletAction
public class IndexEditionAction implements EditionAction {

    private Map<String, String> availableProperties = null;

    private ResourceBundle bundle = ResourceBundle.getBundle(
            "dk.i2m.converge.plugins.indexedition.Messages");

    /**
     * {@inheritDoc }
     */
    @Override
    public void execute(PluginContext ctx, Edition edition,
            OutletEditionAction action) {
        for (NewsItemPlacement placement : edition.getPlacements()) {
            executePlacement(ctx, placement, edition, action);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void executePlacement(PluginContext ctx, NewsItemPlacement placement,
            Edition edition, OutletEditionAction action) {

        NewsItem newsItem = placement.getNewsItem();

        ctx.log(LogSeverity.INFO, bundle.getString(
                "LOG_INDEXING_NEWS_ITEM_X"), new Object[]{newsItem.getId()},
                newsItem, newsItem.getId());
        if (newsItem.isEndState()) {
            try {
                ctx.index(newsItem);
            } catch (SearchEngineIndexingException ex) {
                ctx.log(LogSeverity.SEVERE, bundle.getString(
                        "LOG_INDEXING_FAILED"), new Object[]{newsItem.getId(),
                            ex.getMessage()}, newsItem, newsItem.getId());
            }
        } else {
            ctx.log(LogSeverity.INFO, bundle.getString(
                    "LOG_INDEXING_NEWS_ITEM_X_NOT_END_STATE"),
                    new Object[]{newsItem.getId()}, newsItem, newsItem.getId());
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Map<String, String> getAvailableProperties() {
        if (availableProperties == null) {
            availableProperties = new LinkedHashMap<String, String>();
        }
        return availableProperties;
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
        try {
            final String pattern = "yyyy-MM-dd HH:mm:ss";
            SimpleDateFormat format = new SimpleDateFormat(pattern);
            return format.parse(bundle.getString("PLUGIN_BUILD_TIME"));
        } catch (Exception ex) {
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

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean isSupportEditionExecute() {
        return true;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean isSupportPlacementExecute() {
        return true;
    }
}
