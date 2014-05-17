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
package dk.i2m.converge.plugins.joomla;

import dk.i2m.converge.core.content.NewsItem;
import dk.i2m.converge.core.content.NewsItemPlacement;
import dk.i2m.converge.core.logging.LogSeverity;
import dk.i2m.converge.core.plugin.PluginContext;
import dk.i2m.converge.core.plugin.WorkflowAction;
import dk.i2m.converge.core.security.UserAccount;
import dk.i2m.converge.core.workflow.WorkflowStepAction;
import dk.i2m.converge.core.workflow.WorkflowStepActionProperty;
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
 * Workflow action for sending a story to a Joomla website.
 *
 * @author Allan Lykke Christensen
 */
@dk.i2m.converge.core.annotations.WorkflowAction
public class JoomlaAction extends JoomlaPlugin implements WorkflowAction {

    private static final Logger LOG = Logger.getLogger(JoomlaAction.class.
            getName());

    private ResourceBundle bundle = ResourceBundle.getBundle(
            "dk.i2m.converge.plugins.joomla.JoomlaAction");

    /**
     * Creates a new instance of {@link JoomlaAction}.
     */
    public JoomlaAction() {
    }

    @Override
    public void execute(PluginContext ctx, NewsItem item,
            WorkflowStepAction stepAction, UserAccount user) {
        this.properties = stepAction.getPropertiesAsMap();
        this.categoryMapping = new HashMap<String, String>();

        for (WorkflowStepActionProperty prop : stepAction.getProperties()) {
            if (prop.getKey().equalsIgnoreCase(PROPERTY_CATEGORY_MAPPING)) {
                String[] catMap = prop.getValue().split(";");
                if (catMap.length == 2) {
                    categoryMapping.put(catMap[0], catMap[1]);
                } else if (catMap.length == 3) {
                    categoryMapping.put(catMap[0], catMap[1]);
                    try {
                        categoryPublish.put(catMap[0],
                                Integer.valueOf(catMap[2]));
                    } catch (NumberFormatException ex) {
                        ctx.log(LogSeverity.WARNING,
                                "Invalid category publish delay: {0}",
                                new Object[]{catMap[2]}, stepAction, stepAction.
                                getId());
                    }
                } else if (catMap.length == 4) {
                    categoryMapping.put(catMap[0], catMap[1]);
                    try {
                        categoryPublish.put(catMap[0],
                                Integer.valueOf(catMap[2]));
                    } catch (NumberFormatException ex) {
                        LOG.log(Level.INFO,
                                "Invalid category publish delay: {0}", catMap[2]);
                    }
                    try {
                        categoryExpire.put(catMap[0], Integer.valueOf(catMap[3]));
                    } catch (NumberFormatException ex) {
                        LOG.log(Level.INFO,
                                "Invalid category expire delay [{0}] for category [{1}]",
                                new Object[]{catMap[3], catMap[0]});
                    }
                } else {
                    LOG.log(Level.INFO, "Invalid category mapping: {0}", prop.
                            getValue());
                }
            } else if (prop.getKey().equalsIgnoreCase(
                    PROPERTY_CATEGORY_IMAGE_RESIZE)) {

                String[] imgCat = prop.getValue().split(";");
                if (imgCat.length == 4) {
                    categoryMapping.put(imgCat[0], prop.getValue());
                } else {
                    LOG.log(Level.INFO,
                            "Invalid image category settings: {0}", prop.
                            getValue());
                }
            }

        }

        if (!properties.containsKey(JoomlaAction.PROPERTY_URL)) {
            LOG.log(Level.WARNING, "{0} property missing from action properties",
                    JoomlaAction.PROPERTY_URL);
            return;
        }

        String method = "";
        if (!properties.containsKey(JoomlaAction.PROPERTY_METHOD)) {
            LOG.log(Level.WARNING, "{0} property missing from action properties",
                    JoomlaAction.PROPERTY_METHOD);
            return;
        } else {
            method = properties.get(JoomlaAction.PROPERTY_METHOD);
        }

        if (!properties.containsKey(JoomlaAction.PROPERTY_USERNAME)) {
            LOG.log(Level.WARNING, "{0} property missing from action properties",
                    JoomlaAction.PROPERTY_USERNAME);
            return;
        }

        if (!properties.containsKey(JoomlaAction.PROPERTY_PASSWORD)) {
            LOG.log(Level.WARNING, "{0} property missing from action properties",
                    JoomlaAction.PROPERTY_PASSWORD);
            return;
        }

        int timeout = DEFAULT_TIMEOUT;
        if (properties.containsKey(PROPERTY_XMLRPC_TIMEOUT)) {
            try {
                timeout = Integer.valueOf(properties.get(
                        PROPERTY_XMLRPC_TIMEOUT));
            } catch (NumberFormatException ex) {
                LOG.log(Level.WARNING,
                        "Invalid value contained in property ({0}): {1}",
                        new Object[]{PROPERTY_XMLRPC_TIMEOUT, properties.get(
                            PROPERTY_XMLRPC_TIMEOUT)});
            }
        }

        int replyTimeout = DEFAULT_REPLY_TIMEOUT;
        if (properties.containsKey(PROPERTY_XMLRPC_REPLY_TIMEOUT)) {
            try {
                replyTimeout = Integer.valueOf(properties.get(
                        PROPERTY_XMLRPC_REPLY_TIMEOUT));
            } catch (NumberFormatException ex) {
                LOG.log(Level.WARNING,
                        "Invalid value contained in property ({0}): {1}",
                        new Object[]{PROPERTY_XMLRPC_REPLY_TIMEOUT,
                            properties.get(PROPERTY_XMLRPC_REPLY_TIMEOUT)});
            }
        }

        JoomlaConnection connection = new JoomlaConnection();
        connection.setUrl(properties.get(JoomlaAction.PROPERTY_URL));
        connection.setUsername(properties.get(JoomlaAction.PROPERTY_USERNAME));
        connection.setPassword(properties.get(JoomlaAction.PROPERTY_PASSWORD));
        connection.setTimeout(timeout);
        connection.setReplyTimeout(replyTimeout);
        connection.setTimeZone(user.getTimeZone());

        try {
            connection.validateConnection();
        } catch (JoomlaException ex) {
            LOG.log(Level.WARNING, "Connection invalid. {0}", ex.getCause().
                      getMessage());
            return;
        }

        if (method.equalsIgnoreCase(JoomlaAction.XMLRPC_METHOD_NEW_ARTICLE)) {
            for (NewsItemPlacement placement : item.getPlacements()) {
                try {
                    newArticle(connection, placement);
                } catch (JoomlaActionException ex) {
                    LOG.log(Level.SEVERE, ex.getMessage());
                    LOG.log(Level.FINE, "", ex);
                }
            }
        } else if (method.equalsIgnoreCase(
                JoomlaAction.XMLRPC_METHOD_DELETE_ARTICLE)) {
            try {
                deleteArticle(connection, item);
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
            SimpleDateFormat format =
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
