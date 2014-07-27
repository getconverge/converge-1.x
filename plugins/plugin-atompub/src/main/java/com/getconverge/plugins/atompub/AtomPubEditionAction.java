/*
 * Copyright (C) 2014 Allan Lykke Christensen
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
package com.getconverge.plugins.atompub;

import dk.i2m.converge.core.annotations.OutletAction;
import dk.i2m.converge.core.content.NewsItemPlacement;
import dk.i2m.converge.core.plugin.EditionAction;
import dk.i2m.converge.core.plugin.PluginContext;
import dk.i2m.converge.core.workflow.Edition;
import dk.i2m.converge.core.workflow.OutletEditionAction;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.abdera.Abdera;
import org.apache.abdera.model.Entry;
import org.apache.abdera.protocol.Response;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;

/**
 * {@link EditionAction} for uploading news items to an AtomPub server.
 *
 * @author Allan Lykke Christensen
 * @since 1.1.12
 */
@OutletAction
public class AtomPubEditionAction implements EditionAction {

    private static final String PLUGIN_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final Logger LOG = Logger.getLogger(AtomPubEditionAction.class.getName());
    private final ResourceBundle bundle = ResourceBundle.getBundle("com.getconverge.plugins.atompub.Messages");
    private Map<String, String> availableProperties = null;

    enum Property {

        SERVICE_URL
    }

    enum BundleKey {

        PLUGIN_NAME, PLUGIN_DESCRIPTION, PLUGIN_ABOUT, PLUGIN_VENDOR,
        PLUGIN_BUILD_TIME
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void execute(PluginContext ctx, Edition edition, OutletEditionAction action) {
        LOG.log(Level.INFO, "Executing {0} on Edition #{1} with {2} placements", new Object[]{getName(), edition.getId(), edition.getPlacements().size()});
        for (NewsItemPlacement placement : edition.getPlacements()) {
            executePlacement(ctx, placement, edition, action);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void executePlacement(PluginContext ctx, NewsItemPlacement placement, Edition edition, OutletEditionAction action) {
        LOG.log(Level.INFO, "Executing {0} on NewsItem #{1}", new Object[]{getName(), placement.getNewsItem().getId()});

        String serviceUrl = action.getPropertiesAsMap().get(Property.SERVICE_URL.name());

        Abdera atompub = new Abdera();
        AbderaClient client = new AbderaClient(atompub);

        NewsItemPlacementToAtomConverter converter = new NewsItemPlacementToAtomConverter(atompub);
        Entry entry;
        entry = converter.convert(placement);

        ClientResponse response = client.post(serviceUrl, entry);

        if (response.getType() != Response.ResponseType.SUCCESS) {
            LOG.log(Level.SEVERE, "Could not post news item #{1} to the AtomPub server ({2)). {0}", new Object[]{response.getStatusText(), placement.getNewsItem().getId(), serviceUrl});
        } else {
            LOG.log(Level.FINE, "News item #{2} posted to AtomPub server ({0}): {1}", new Object[]{serviceUrl, response.getStatusText(), placement.getNewsItem().getId()});
        }
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

    /**
     * {@inheritDoc }
     */
    @Override
    public Map<String, String> getAvailableProperties() {
        if (availableProperties == null) {
            availableProperties = new LinkedHashMap<String, String>();
            availableProperties.put(getBundle().getString(Property.SERVICE_URL.name()), Property.SERVICE_URL.name());
        }
        return availableProperties;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getName() {
        return bundle.getString(BundleKey.PLUGIN_NAME.name());
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getAbout() {
        return bundle.getString(BundleKey.PLUGIN_ABOUT.name());
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getDescription() {
        return bundle.getString(BundleKey.PLUGIN_DESCRIPTION.name());
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getVendor() {
        return bundle.getString(BundleKey.PLUGIN_VENDOR.name());
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Date getDate() {
        try {
            SimpleDateFormat format = new SimpleDateFormat(PLUGIN_DATE_FORMAT);
            String date = bundle.getString(BundleKey.PLUGIN_BUILD_TIME.name());
            return format.parse(date);
        } catch (ParseException ex) {
            LOG.log(Level.WARNING, "{0}. Using todays date", ex.getMessage());
            LOG.log(Level.FINEST, "", ex);
            return Calendar.getInstance().getTime();
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ResourceBundle getBundle() {
        return this.bundle;
    }
}
