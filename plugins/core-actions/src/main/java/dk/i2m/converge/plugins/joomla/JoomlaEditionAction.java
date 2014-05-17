/*
 * Copyright (C) 2010 - 2012 Interactive Media Management
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
package dk.i2m.converge.plugins.joomla;

import dk.i2m.converge.core.content.NewsItem;
import dk.i2m.converge.core.content.NewsItemPlacement;
import dk.i2m.converge.core.logging.LogSeverity;
import dk.i2m.converge.core.plugin.EditionAction;
import dk.i2m.converge.core.plugin.PluginContext;
import dk.i2m.converge.core.workflow.Edition;
import dk.i2m.converge.core.workflow.OutletEditionAction;
import dk.i2m.converge.core.workflow.OutletEditionActionProperty;
import dk.i2m.converge.plugins.joomla.client.JoomlaConnection;
import dk.i2m.converge.plugins.joomla.client.JoomlaException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * {Plug-in {link EditionAction} for uploading
 *
 * @author Allan Lykke Christensen
 */
@dk.i2m.converge.core.annotations.OutletAction
public class JoomlaEditionAction extends JoomlaPlugin implements EditionAction {

    private static final Logger LOG =
            Logger.getLogger(JoomlaEditionAction.class.getName());

    private ResourceBundle bundle = ResourceBundle.getBundle(
            "dk.i2m.converge.plugins.joomla.JoomlaEditionAction");

    private int timeout = DEFAULT_TIMEOUT;

    private int replyTimeout = DEFAULT_REPLY_TIMEOUT;

    private String method = "";

    private PluginContext pluginContext;

    @Override
    public void execute(PluginContext ctx, Edition edition,
            OutletEditionAction action) {
        this.pluginContext = ctx;
        loadSettings(action);
        JoomlaConnection connection = createConnection();

        try {
            connection.validateConnection();
        } catch (JoomlaException ex) {
            pluginContext.log(LogSeverity.SEVERE, bundle.getString(
                    "LOG_INVALID_CONNECTION"), new Object[]{ex.getCause().
                        getMessage()}, action, action.getId());
            return;
        }

        if (this.method.equalsIgnoreCase(
                JoomlaEditionAction.XMLRPC_METHOD_NEW_ARTICLE)) {
            fetchJoomlaCategories(connection);

            for (NewsItemPlacement placement : edition.getPlacements()) {
                NewsItem item = placement.getNewsItem();
                if (item.isEndState()) {
                    if (isCategoryMapped(placement)) {
                        try {
                            Integer aid = newArticle(connection, placement);
                            pluginContext.log(LogSeverity.INFO,
                                    bundle.getString("LOG_UPLOADED_WITH_ID_X"),
                                    new Object[]{placement.getNewsItem().getId(),
                                        aid}, placement.getNewsItem(),
                                    placement.getNewsItem().getId());
                        } catch (JoomlaActionException ex) {
                            pluginContext.log(LogSeverity.SEVERE, bundle.
                                    getString("LOG_COULD_NOT_CREATE_ARTICLE"),
                                    new Object[]{ex.getMessage()}, action,
                                    action.getId());
                            LOG.log(Level.FINE, "", ex);
                        }
                    } else {
                        pluginContext.log(LogSeverity.INFO, bundle.getString(
                                "LOG_SECTION_NOT_MAPPED"),
                                new Object[]{placement.getNewsItem().getId(),
                                    placement.getSection().getFullName()},
                                action, action.getId());
                    }
                }
            }
        } else if (method.equalsIgnoreCase(XMLRPC_METHOD_DELETE_ARTICLE)) {
            for (NewsItemPlacement placement : edition.getPlacements()) {
                try {
                    deleteArticle(connection, placement.getNewsItem());
                } catch (JoomlaActionException ex) {
                    LOG.log(Level.SEVERE, ex.getMessage());
                    LOG.log(Level.FINE, "", ex);
                }
            }
        }
    }

    @Override
    public void executePlacement(PluginContext ctx, NewsItemPlacement placement,
            Edition edition, OutletEditionAction action) {
        this.pluginContext = ctx;
        loadSettings(action);
        JoomlaConnection connection = createConnection();

        try {
            connection.validateConnection();
        } catch (JoomlaException ex) {
            pluginContext.log(LogSeverity.SEVERE, bundle.getString(
                    "LOG_INVALID_CONNECTION"), new Object[]{ex.getMessage()},
                    action, action.getId());
            return;
        }

        if (this.method.equalsIgnoreCase(
                JoomlaEditionAction.XMLRPC_METHOD_NEW_ARTICLE)) {
            fetchJoomlaCategories(connection);


            if (placement.getNewsItem().isEndState()) {
                if (isCategoryMapped(placement)) {
                    try {
                        Integer aid = newArticle(connection, placement);
                        pluginContext.log(LogSeverity.INFO, bundle.getString(
                                "LOG_UPLOADED_WITH_ID_X"),
                                new Object[]{placement.getNewsItem().getId(),
                                    aid}, placement.getNewsItem(),
                                placement.getNewsItem().getId());
                    } catch (JoomlaActionException ex) {
                        pluginContext.log(LogSeverity.SEVERE, bundle.getString(
                                "LOG_COULD_NOT_UPLOAD_X_BECAUSE_Y"),
                                new Object[]{placement.getNewsItem().getId(),
                                    ex.getMessage()}, placement.getNewsItem(),
                                placement.getNewsItem().getId());
                    }
                } else {
                    pluginContext.log(LogSeverity.INFO, bundle.getString(
                            "LOG_SECTION_NOT_MAPPED"), new Object[]{placement.
                                getNewsItem().getId(), placement.getSection().
                                getFullName()}, action, action.getId());
                }
            }

        } else if (method.equalsIgnoreCase(XMLRPC_METHOD_DELETE_ARTICLE)) {
            try {
                deleteArticle(connection, placement.getNewsItem());
            } catch (JoomlaActionException ex) {
                LOG.log(Level.SEVERE, ex.getMessage());
                LOG.log(Level.FINE, "", ex);
            }
        }
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
            final String pattern = "yyyy-MM-dd HH:mm:ss";
            SimpleDateFormat format = new SimpleDateFormat(pattern);
            return format.parse(bundle.getString("PLUGIN_BUILD_TIME"));
        } catch (Exception ex) {
            return Calendar.getInstance().getTime();
        }
    }

    @Override
    public ResourceBundle getBundle() {
        return bundle;
    }

    @Override
    public boolean isSupportEditionExecute() {
        return true;
    }

    @Override
    public boolean isSupportPlacementExecute() {
        return true;
    }

    private boolean loadSettings(OutletEditionAction action) {

        this.properties = action.getPropertiesAsMap();
        this.categoryMapping = new HashMap<String, String>();
        this.categoryImageMapping = new HashMap<String, String>();

        for (OutletEditionActionProperty prop : action.getProperties()) {

            // Fetch Category Mappings
            if (prop.getKey().equalsIgnoreCase(PROPERTY_CATEGORY_MAPPING)) {
                String[] catMap = prop.getValue().split(";");
                if (catMap.length == 2) {
                    categoryMapping.put(catMap[0], catMap[1]);
                } else if (catMap.length == 3) {
                    categoryMapping.put(catMap[0], catMap[1]);
                    try {
                        categoryPublish.put(catMap[0],
                                Integer.valueOf(catMap[2].trim()));
                    } catch (NumberFormatException ex) {
                        pluginContext.log(LogSeverity.WARNING,
                                "Invalid category publish delay: {0}",
                                new Object[]{catMap[2]}, action, action.getId());
                    }
                } else if (catMap.length == 4) {
                    categoryMapping.put(catMap[0], catMap[1]);
                    try {
                        categoryPublish.put(catMap[0],
                                Integer.valueOf(catMap[2].trim()));
                    } catch (NumberFormatException ex) {
                        pluginContext.log(LogSeverity.WARNING,
                                "Invalid category publish delay: {0}",
                                new Object[]{catMap[2]}, action, action.getId());
                    }
                    try {
                        categoryExpire.put(catMap[0],
                                Integer.valueOf(catMap[3].trim()));
                    } catch (NumberFormatException ex) {
                        pluginContext.log(LogSeverity.WARNING,
                                "Invalid category expire delay: {0}",
                                new Object[]{catMap[3]}, action, action.getId());
                    }
                } else {
                    pluginContext.log(LogSeverity.WARNING,
                            "Invalid category mapping: {0}", new Object[]{prop.
                                getValue()}, action, action.getId());
                }
            } else if (prop.getKey().equalsIgnoreCase(
                    PROPERTY_CATEGORY_IMAGE_RESIZE)) {

                String[] imgCat = prop.getValue().split(";");
                if (imgCat.length == 4) {
                    this.categoryImageMapping.put(imgCat[0], prop.getValue());
                } else {
                    pluginContext.log(LogSeverity.WARNING,
                            "Invalid image category settings: {0}",
                            new Object[]{prop.getValue()}, action,
                            action.getId());
                }
            }
        }

        if (!properties.containsKey(JoomlaEditionAction.PROPERTY_URL)) {
            LOG.log(Level.WARNING,
                    "{0} property missing from action properties",
                    JoomlaEditionAction.PROPERTY_URL);
            return false;
        }

        if (!properties.containsKey(JoomlaEditionAction.PROPERTY_METHOD)) {
            LOG.log(Level.WARNING, "{0} property missing from action properties",
                    JoomlaEditionAction.PROPERTY_METHOD);
            return false;
        } else {
            this.method = properties.get(JoomlaEditionAction.PROPERTY_METHOD);
        }

        if (!properties.containsKey(JoomlaEditionAction.PROPERTY_USERNAME)) {
            LOG.log(Level.WARNING, "{0} property missing from action properties",
                    JoomlaEditionAction.PROPERTY_USERNAME);
            return false;
        }

        if (!properties.containsKey(JoomlaEditionAction.PROPERTY_PASSWORD)) {
            LOG.log(Level.WARNING, "{0} property missing from action properties",
                    JoomlaEditionAction.PROPERTY_PASSWORD);
            return false;
        }


        if (properties.containsKey(PROPERTY_XMLRPC_TIMEOUT)) {
            try {
                this.timeout = Integer.valueOf(properties.get(
                        PROPERTY_XMLRPC_TIMEOUT));
            } catch (NumberFormatException ex) {
                LOG.log(Level.WARNING,
                        "Invalid value contained in property ({0}): {1}",
                        new Object[]{PROPERTY_XMLRPC_TIMEOUT, properties.get(
                            PROPERTY_XMLRPC_TIMEOUT)});
            }
        }

        if (properties.containsKey(PROPERTY_XMLRPC_REPLY_TIMEOUT)) {
            try {
                this.replyTimeout = Integer.valueOf(properties.get(
                        PROPERTY_XMLRPC_REPLY_TIMEOUT));
            } catch (NumberFormatException ex) {
                LOG.log(Level.WARNING,
                        "Invalid value contained in property ({0}): {1}",
                        new Object[]{PROPERTY_XMLRPC_REPLY_TIMEOUT, properties.
                            get(
                            PROPERTY_XMLRPC_REPLY_TIMEOUT)});
            }
        }

        return true;
    }

    /**
     * Creates a connection to a Joomla instance with the Joomla Converge
     * XML-RPC API installed.
     *
     * @return Connection to Joomla instance
     */
    private JoomlaConnection createConnection() {
        JoomlaConnection con = new JoomlaConnection();
        con.setUrl(properties.get(JoomlaEditionAction.PROPERTY_URL));
        con.setUsername(properties.get(JoomlaEditionAction.PROPERTY_USERNAME));
        con.setPassword(properties.get(JoomlaEditionAction.PROPERTY_PASSWORD));
        con.setTimeout(this.timeout);
        con.setReplyTimeout(this.replyTimeout);
//        con.setTimeZone(user.getTimeZone());
        return con;
    }
}
