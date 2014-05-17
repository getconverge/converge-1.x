/*
 * Copyright (C) 2011 - 2013 Interactive Media Management
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
package dk.i2m.converge.plugins.actions.urlcallback;

import dk.i2m.converge.core.content.NewsItemPlacement;
import dk.i2m.converge.core.plugin.EditionAction;
import dk.i2m.converge.core.plugin.PluginContext;
import dk.i2m.converge.core.workflow.Edition;
import dk.i2m.converge.core.workflow.OutletEditionAction;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.language.DefaultTemplateLexer;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;

/**
 * {@link EditionAction} for executing a URL. This action can be used as a
 * callback for external system that needs to be notified of an {@link Edition}
 * closing.
 *
 * @author Allan Lykke Christensen
 */
@dk.i2m.converge.core.annotations.OutletAction
public class UrlCallbackAction implements EditionAction {

    private static final int DEFAULT_TIMEOUT = 30000;
    private Map<String, String> availableProperties = null;
    private static final Logger LOG = Logger.getLogger(UrlCallbackAction.class.getName());
    private ResourceBundle bundle = ResourceBundle.getBundle("dk.i2m.converge.plugins.actions.urlcallback.Messages");

    public enum Property {

        CALLBACK_URL,
        TIMEOUT
    }

    @Override
    public void execute(PluginContext ctx, Edition edition, OutletEditionAction action) {
        Map<String, String> properties = action.getPropertiesAsMap();

        if (!properties.containsKey(Property.CALLBACK_URL.name())) {
            LOG.log(Level.WARNING, "{0} property missing from properties", Property.CALLBACK_URL.name());
            return;
        }

        int timeout = DEFAULT_TIMEOUT;
        if (properties.containsKey(Property.TIMEOUT.name())) {
            String rawTimeout = "";
            try {
                rawTimeout = properties.get(Property.TIMEOUT.name());
                timeout = Integer.valueOf(rawTimeout);
            } catch (Exception e) {
                LOG.log(Level.WARNING, "Invalid value set for {0}: {1}. Using {2}", new Object[]{Property.TIMEOUT.name(), rawTimeout, timeout});
            }
        }

        // Replace template tags in URL
        String rawUrl = properties.get(Property.CALLBACK_URL.name());
        StringTemplate template = new StringTemplate(rawUrl, DefaultTemplateLexer.class);
        template.setAttribute("edition", edition);
        String url = template.toString();

        HttpClient client = new HttpClient();
        client.getHttpConnectionManager().getParams().setConnectionTimeout(timeout);
        HttpMethod method = new GetMethod(url);
        method.setFollowRedirects(true);

        try {
            client.executeMethod(method);
        } catch (HttpException ex) {
            LOG.log(Level.WARNING, "Could not execute callback URL. {0}", ex.getMessage());
            LOG.log(Level.FINEST, "", ex);
        } catch (IOException ex) {
            LOG.log(Level.WARNING, "Could not execute callback URL. {0}", ex.getMessage());
            LOG.log(Level.FINEST, "", ex);
        } finally {
            method.releaseConnection();
        }
    }

    @Override
    public void executePlacement(PluginContext ctx, NewsItemPlacement placement, Edition edition, OutletEditionAction action) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public boolean isSupportEditionExecute() {
        return true;
    }

    @Override
    public boolean isSupportPlacementExecute() {
        return false;
    }

    @Override
    public Map<String, String> getAvailableProperties() {
        if (availableProperties == null) {
            availableProperties = new LinkedHashMap<String, String>();
            for (Property p : Property.values()) {
                availableProperties.put(bundle.getString(p.name()), p.name());
            }
        }
        return availableProperties;
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
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return format.parse(bundle.getString("PLUGIN_BUILD_TIME"));
        } catch (Exception ex) {
            return Calendar.getInstance().getTime();
        }
    }

    @Override
    public ResourceBundle getBundle() {
        return bundle;
    }
}
