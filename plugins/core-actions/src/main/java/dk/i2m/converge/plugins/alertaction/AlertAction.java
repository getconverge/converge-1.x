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
package dk.i2m.converge.plugins.alertaction;

import dk.i2m.converge.core.Notification;
import dk.i2m.converge.core.content.NewsItem;
import dk.i2m.converge.core.content.NewsItemActor;
import dk.i2m.converge.core.plugin.PluginContext;
import dk.i2m.converge.core.plugin.WorkflowAction;
import dk.i2m.converge.core.security.UserAccount;
import dk.i2m.converge.core.workflow.WorkflowStepAction;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.language.DefaultTemplateLexer;
import org.apache.commons.lang.StringEscapeUtils;

/**
 * Workflow action for creating an alert.
 *
 * For the {@code message} property, the following variables are available:
 *
 * <ul> <li>initiator</li> <li>newsitem</li> </ul>
 *
 * All variables must be enclosed in dollar signs, e.g. $action-initiator$.
 *
 * @author Allan Lykke Christensen
 */
@dk.i2m.converge.core.annotations.WorkflowAction
public class AlertAction implements WorkflowAction {

    private static final String PROPERTY_RECIPIENT_USER = "recipient.user";

    private static final String PROPERTY_RECIPIENT_ROLE = "recipient.role";

    private static final String PROPERTY_MESSAGE = "message";

    private static final String PROPERTY_LINK = "link";

    private Map<String, String> availableProperties = null;

    private static final Logger LOG = Logger.getLogger(AlertAction.class.
            getName());

    private ResourceBundle bundle = ResourceBundle.getBundle(
            "dk.i2m.converge.plugins.alertaction.Messages");

    /**
     * Creates a new instance of {@link AlertAction}.
     */
    public AlertAction() {
    }

    @Override
    public void execute(PluginContext ctx, NewsItem item,
            WorkflowStepAction stepAction, UserAccount user) {
        Map<String, String> properties = stepAction.getPropertiesAsMap();

        if (!properties.containsKey(AlertAction.PROPERTY_MESSAGE)) {
            LOG.log(Level.WARNING,
                    "{0} property missing from action properties",
                    AlertAction.PROPERTY_MESSAGE);
            return;
        }

        if (!properties.containsKey(AlertAction.PROPERTY_RECIPIENT_USER)
                && !properties.containsKey(AlertAction.PROPERTY_RECIPIENT_ROLE)) {
            LOG.log(Level.WARNING,
                    "{0} or {1} property missing from action properties",
                    new Object[]{AlertAction.PROPERTY_RECIPIENT_USER,
                        AlertAction.PROPERTY_RECIPIENT_ROLE});
            return;
        }

        boolean sendToUser = false;
        String sendToUserRole = "";
        boolean sendToRole = false;
        String sendToRoleRole = "";

        if (properties.containsKey(AlertAction.PROPERTY_RECIPIENT_USER)) {
            sendToUser = true;
            sendToUserRole = properties.get(
                    AlertAction.PROPERTY_RECIPIENT_USER);
        }

        if (properties.containsKey(AlertAction.PROPERTY_RECIPIENT_ROLE)) {
            sendToRole = true;
            sendToRoleRole = properties.get(
                    AlertAction.PROPERTY_RECIPIENT_ROLE);
        }

        String link = "";
        if (properties.containsKey(AlertAction.PROPERTY_LINK)) {
            link = properties.get(AlertAction.PROPERTY_LINK);
        }

        StringTemplate template =
                new StringTemplate(
                properties.get(AlertAction.PROPERTY_MESSAGE),
                DefaultTemplateLexer.class);
        template.setAttribute("newsitem", item);
        template.setAttribute("html-newsitem-title", StringEscapeUtils.
                escapeHtml(item.getTitle()));
        template.setAttribute("initiator", user);
        String notificationMessage = template.toString();

        template = new StringTemplate(link, DefaultTemplateLexer.class);
        template.setAttribute("newsitem", item);
        template.setAttribute("html-newsitem-title", StringEscapeUtils.
                escapeHtml(item.getTitle()));
        template.setAttribute("initiator", user);
        link = template.toString();

        List<UserAccount> usersToNotify = new ArrayList<UserAccount>();

        if (sendToUser) {
            for (NewsItemActor actor : item.getActors()) {
                if (actor.getRole().getName().equalsIgnoreCase(sendToUserRole)) {
                    usersToNotify.add(actor.getUser());
                }
            }
        }

        if (sendToRole) {
            usersToNotify.addAll(ctx.findUserAccountsByRole(sendToRoleRole));
        }

        for (UserAccount ua : usersToNotify) {
            Notification notification = new Notification();
            notification.setMessage(notificationMessage);
            notification.setAdded(Calendar.getInstance());
            notification.setRecipient(ua);
            notification.setSender(user);
            notification.setLink(link);
            ctx.createNotification(notification);
        }
    }

    @Override
    public Map<String, String> getAvailableProperties() {
        if (availableProperties == null) {
            availableProperties = new LinkedHashMap<String, String>();
            availableProperties.put(
                    bundle.getString(PROPERTY_RECIPIENT_USER),
                    PROPERTY_RECIPIENT_USER);
            availableProperties.put(
                    bundle.getString(PROPERTY_RECIPIENT_ROLE),
                    PROPERTY_RECIPIENT_ROLE);
            availableProperties.put(bundle.getString(PROPERTY_MESSAGE),
                    PROPERTY_MESSAGE);
            availableProperties.put(bundle.getString(PROPERTY_LINK),
                    PROPERTY_LINK);
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
            SimpleDateFormat format = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss");
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
