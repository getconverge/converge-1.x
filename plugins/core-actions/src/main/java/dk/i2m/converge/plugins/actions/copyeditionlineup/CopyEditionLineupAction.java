/*
 * Copyright (C) 2012 Interactive Media Management
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
package dk.i2m.converge.plugins.actions.copyeditionlineup;

import dk.i2m.converge.core.DataNotFoundException;
import dk.i2m.converge.core.content.NewsItemPlacement;
import dk.i2m.converge.core.logging.LogSeverity;
import dk.i2m.converge.core.plugin.EditionAction;
import dk.i2m.converge.core.plugin.PluginContext;
import dk.i2m.converge.core.workflow.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Plug-in for copying the line-up of an edition to another outlet.
 * <p/>
 * @author Allan Lykke Christensen
 */
@dk.i2m.converge.core.annotations.OutletAction
public class CopyEditionLineupAction implements EditionAction {

    private ResourceBundle bundle = ResourceBundle.getBundle(
            "dk.i2m.converge.plugins.actions.copyeditionlineup.Messages");

    private Map<String, String> availableProperties = null;

    private Map<String, String> instanceProperties =
            new HashMap<String, String>();

    private PluginContext pluginCtx;

    private OutletEditionAction actionInstance;

    private static final DateFormat DATE_PARSER = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss");

    /** Contains the section mappings between the original outlet and the new outlet. */
    private Map<Long, Section> sectionMapping;

    /** Contains the unique identified of the outlet for which to copy the news items. */
    private Long outletId;

    /** Should non complete news items be copied across. */
    private boolean copyAll = false;

    /** Properties available for configuring the plug-in. */
    enum Property {

        OUTLET,
        SECTION_MAPPING,
        COPY_ALL
    }

    /** {@inheritDoc } */
    @Override
    public void execute(PluginContext ctx, Edition edition,
            OutletEditionAction action) {
        this.pluginCtx = ctx;
        this.actionInstance = action;
        this.instanceProperties = action.getPropertiesAsMap();
        this.sectionMapping = new HashMap<Long, Section>();

        // Read and validate properties
        if (!readProperties()) {
            return;
        }

        // Fetch edition for which to copy the news items
        Edition newEdition;
        try {
            newEdition = ctx.findNextEdition(this.outletId);
            log(LogSeverity.INFO, "LOG_NEXT_EDITION_FOUND_X", newEdition.getId());
        } catch (DataNotFoundException ex) {
            log(LogSeverity.SEVERE, "LOG_PROPERTY_OUTLET_INVALID");
            return;
        }

        // Load section mapping
        for (OutletEditionActionProperty property : action.getProperties()) {
            try {
                Property key = Property.valueOf(property.getKey());
                if (Property.SECTION_MAPPING == key) {
                    String sectionMappingValue = property.getValue();
                    String mappings[] = sectionMappingValue.split(";");
                    if (mappings.length != 2) {
                        continue;
                    }
                    try {
                        sectionMapping.put(Long.valueOf(mappings[0].trim()),
                                findSection(newEdition.getOutlet(),
                                Long.valueOf(mappings[1].trim())));
                    } catch (NumberFormatException ex) {
                        log(LogSeverity.WARNING,
                                "LOG_PROPERTY_SECTION_INVALID_X",
                                ex.getMessage());
                    }
                }
            } catch (IllegalArgumentException ex) {
                log(LogSeverity.WARNING, "LOG_PROPERTY_INVALID_X",
                        ex.getMessage());
            }
        }

        // Copy placements
        for (NewsItemPlacement existingPlacement : edition.getPlacements()) {

            // Skip non-complete items
            if (!copyAll && !existingPlacement.getNewsItem().isEndState()) {
                log(LogSeverity.FINE, "LOG_SKIPPING_NEWS_ITEM_X",
                        existingPlacement.getNewsItem().getId());
                continue;
            }

            // Determine which section to use in the new outlet
            Section section = null;
            if (existingPlacement.getSection() != null && sectionMapping.
                    containsKey(existingPlacement.getSection().getId())) {
                section = sectionMapping.get(existingPlacement.getSection().
                        getId());
            }

            // Construct the new placement
            NewsItemPlacement newPlacement = new NewsItemPlacement();
            newPlacement.setEdition(newEdition);
            newPlacement.setNewsItem(existingPlacement.getNewsItem());
            newPlacement.setOutlet(newEdition.getOutlet());
            newPlacement.setPosition(existingPlacement.getPosition());
            newPlacement.setSection(section);
            newPlacement.setStart(existingPlacement.getStart());

            // Save placement in database
            pluginCtx.createPlacement(newPlacement);
        }
    }

    /** {@inheritDoc } */
    @Override
    public void executePlacement(PluginContext ctx, NewsItemPlacement placement,
            Edition edition, OutletEditionAction action) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /** {@inheritDoc } */
    @Override
    public boolean isSupportEditionExecute() {
        return true;
    }

    /** {@inheritDoc } */
    @Override
    public boolean isSupportPlacementExecute() {
        return false;
    }

    /** {@inheritDoc } */
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

    /** {@inheritDoc } */
    @Override
    public String getName() {
        return bundle.getString("PLUGIN_NAME");
    }

    /** {@inheritDoc } */
    @Override
    public String getAbout() {
        return bundle.getString("PLUGIN_ABOUT");
    }

    /** {@inheritDoc } */
    @Override
    public String getDescription() {
        return bundle.getString("PLUGIN_DESCRIPTION");
    }

    /** {@inheritDoc } */
    @Override
    public String getVendor() {
        return bundle.getString("PLUGIN_VENDOR");
    }

    /** {@inheritDoc } */
    @Override
    public Date getDate() {
        try {
            return DATE_PARSER.parse(bundle.getString("PLUGIN_BUILD_TIME"));
        } catch (Exception ex) {
            return Calendar.getInstance().getTime();
        }
    }

    /** {@inheritDoc } */
    @Override
    public ResourceBundle getBundle() {
        return bundle;
    }

    /**
     * Finds a {@link Section} by its unique identifier.
     * <p/>
     * @param outlet    {@link Outlet} to search
     * @param sectionId Unique identifier of the {@link section}
     * @return {@link Section} matching the unique identifier, or {@code null}
     * if the {@link Section} could not be found
     */
    private Section findSection(Outlet outlet, Long sectionId) {
        for (Section s : outlet.getActiveSections()) {
            if (s.getId().equals(sectionId)) {
                return s;
            }
        }
        return null;
    }

    /**
     * Reads and validate the properties.
     * <p/>
     * @return {@code true} if the properties were read and validated, otherwise
     *         {@code false}
     */
    private boolean readProperties() {
        if (!isPropertySet(Property.OUTLET)) {
            log(LogSeverity.SEVERE, "LOG_PROPERTY_OUTLET_MISSING");
            return false;
        } else {
            this.outletId = getPropertyAsLong(Property.OUTLET);
        }

        if (isPropertySet(Property.COPY_ALL)) {
            this.copyAll = getPropertyAsBoolean(Property.COPY_ALL);
        }
        return true;
    }

    // -- Utility Methods -----
    private void log(LogSeverity severity, String msg) {
        log(severity, msg, new Object[]{});
    }

    private void log(LogSeverity severity, String msg, Object param) {
        log(severity, msg, new Object[]{param});
    }

    private void log(LogSeverity severity, String msg, Object[] params) {
        this.pluginCtx.log(severity, bundle.getString(msg), params,
                this.actionInstance,
                this.actionInstance.getId());
    }

    private boolean isPropertySet(Property p) {
        return instanceProperties.containsKey(p.name());
    }

    private String getProperty(Property p) {
        return instanceProperties.get(p.name());
    }

    private Boolean getPropertyAsBoolean(Property p) {
        return Boolean.parseBoolean(getProperty(p));
    }

    private Long getPropertyAsLong(Property p) {
        return Long.valueOf(getProperty(p));
    }
}
