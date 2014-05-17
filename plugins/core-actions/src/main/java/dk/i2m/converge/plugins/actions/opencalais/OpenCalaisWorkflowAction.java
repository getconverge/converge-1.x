/*
 * Copyright (C) 2011 Interactive Media Management
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
package dk.i2m.converge.plugins.actions.opencalais;

import dk.i2m.converge.core.EnrichException;
import dk.i2m.converge.core.content.NewsItem;
import dk.i2m.converge.core.metadata.Concept;
import dk.i2m.converge.core.plugin.PluginContext;
import dk.i2m.converge.core.plugin.WorkflowAction;
import dk.i2m.converge.core.security.UserAccount;
import dk.i2m.converge.core.workflow.WorkflowStepAction;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * {@link WorkflowAction} for discovering {@link Concept}s for a story through
 * the OpenCalais service.
 *
 * @author Allan Lykke Christensen
 */
@dk.i2m.converge.core.annotations.WorkflowAction
public class OpenCalaisWorkflowAction implements WorkflowAction {

    private Map<String, String> availableProperties = null;

    private static final Logger LOG =
            Logger.getLogger(OpenCalaisWorkflowAction.class.getName());

    private ResourceBundle bundle =
            ResourceBundle.getBundle(
            "dk.i2m.converge.plugins.actions.opencalais.WorkflowActionMessages");

    @Override
    public void execute(PluginContext ctx, NewsItem item,
            WorkflowStepAction stepAction, UserAccount user) {
        List<Concept> concepts = new ArrayList<Concept>();
        try {
            concepts = ctx.enrich(item.getStory());
        } catch (EnrichException ex) {
            LOG.log(Level.SEVERE, ex.getMessage());
        }

        item.getConcepts().addAll(concepts);
        Set<Concept> set = new HashSet<Concept>(item.getConcepts());
        ArrayList<Concept> unique = new ArrayList<Concept>(set);
        item.setConcepts(unique);
    }

    @Override
    public Map<String, String> getAvailableProperties() {
        if (availableProperties == null) {
            availableProperties = new LinkedHashMap<String, String>();
        }
        return availableProperties;
    }

    @Override
    public String getName() {
        return bundle.getString("PLUGIN_NAME");
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
    public String getAbout() {
        return bundle.getString("PLUGIN_ABOUT");
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
