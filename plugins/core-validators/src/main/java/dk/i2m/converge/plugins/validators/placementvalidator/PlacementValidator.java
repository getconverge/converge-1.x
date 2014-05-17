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
package dk.i2m.converge.plugins.validators.placementvalidator;

import dk.i2m.converge.core.content.NewsItem;
import dk.i2m.converge.core.content.NewsItemPlacement;
import dk.i2m.converge.core.plugin.WorkflowValidator;
import dk.i2m.converge.core.plugin.WorkflowValidatorException;
import dk.i2m.converge.core.workflow.WorkflowStep;
import dk.i2m.converge.core.workflow.WorkflowStepValidator;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

/**
 * Validator for checking the presence of {@link MediaItem}s in a {@link NewsItem}.
 *
 * @author Allan Lykke Christensen
 */
@dk.i2m.converge.core.annotations.WorkflowValidator
public class PlacementValidator implements WorkflowValidator {

    public static final String PROPERTY_OPERATION = "Operation";

    public static final String PROPERTY_COUNT = "Count";

    public static final String PROPERTY_MESSAGE = "Message";

    public static final String PROPERTY_MUST_HAVE_SECTION = "MustHaveSection";

    public static final String PROPERTY_MUST_HAVE_START = "MustHaveStart";

    public static final String PROPERTY_MUST_HAVE_POSITION =
            "MustHavePosition";

    public static final String PROPERTY_MUST_HAVE_EDITION = "MustHaveEdition";

    private static final Logger LOG =
            Logger.getLogger(PlacementValidator.class.getName());

    private Map<String, String> availableProperties = null;

    private ResourceBundle bundle = ResourceBundle.getBundle(
            "dk.i2m.converge.plugins.validators.placementvalidator.Messages");

    @Override
    public void execute(NewsItem item, WorkflowStep step,
            WorkflowStepValidator validator) throws WorkflowValidatorException {
        Map<String, String> properties = validator.getPropertiesAsMap();

        if (!properties.containsKey(PROPERTY_OPERATION)) {
            throw new WorkflowValidatorException("Validation plug-in not configured. Missing property "
                    + PROPERTY_OPERATION);
        }

        if (!properties.containsKey(PROPERTY_COUNT)) {
            throw new WorkflowValidatorException("Validation plug-in not configured. Missing property "
                    + PROPERTY_COUNT);
        }

        if (!properties.containsKey(PROPERTY_MESSAGE)) {
            throw new WorkflowValidatorException("Validation plug-in not configured. Missing property "
                    + PROPERTY_MESSAGE);
        }

        String operation = properties.get(PROPERTY_OPERATION);
        String fieldCount = properties.get(PROPERTY_COUNT);
        String message = properties.get(PROPERTY_MESSAGE);

        boolean mustHaveSection = false;
        boolean mustHaveStart = false;
        boolean mustHavePosition = false;
        boolean mustHaveEdition = false;

        if (properties.containsKey(PROPERTY_MUST_HAVE_SECTION)) {
            mustHaveSection = Boolean.parseBoolean(properties.get(
                    PROPERTY_MUST_HAVE_SECTION));
        }

        if (properties.containsKey(PROPERTY_MUST_HAVE_START)) {
            mustHaveStart = Boolean.parseBoolean(properties.get(
                    PROPERTY_MUST_HAVE_START));
        }

        if (properties.containsKey(PROPERTY_MUST_HAVE_POSITION)) {
            mustHavePosition = Boolean.parseBoolean(properties.get(
                    PROPERTY_MUST_HAVE_POSITION));
        }

        if (properties.containsKey(PROPERTY_MUST_HAVE_EDITION)) {
            mustHaveEdition = Boolean.parseBoolean(properties.get(
                    PROPERTY_MUST_HAVE_EDITION));
        }

        try {
            int count = Integer.valueOf(fieldCount);
            int placements = item.getPlacements().size();

            if (operation.equalsIgnoreCase(">")) {
                if (!(placements > count)) {
                    throw new WorkflowValidatorException(message);
                }
            } else if (operation.equalsIgnoreCase("<")) {
                if (!(placements < count)) {
                    throw new WorkflowValidatorException(message);
                }
            } else if (operation.equalsIgnoreCase("=")) {
                if (!(placements == count)) {
                    throw new WorkflowValidatorException(message);
                }
            } else {
                throw new WorkflowValidatorException("Validation plug-in not configured. Incorrect value ("
                        + operation + ") set for property "
                        + PROPERTY_OPERATION);
            }

            if (mustHaveSection || mustHaveStart || mustHavePosition) {
                for (NewsItemPlacement placement : item.getPlacements()) {
                    if (mustHaveEdition && placement.getEdition() == null) {
                        throw new WorkflowValidatorException(message);
                    }

                    if (mustHaveSection && placement.getSection() == null) {
                        throw new WorkflowValidatorException(message);
                    }

                    if (mustHaveStart && placement.getStart() == null) {
                        throw new WorkflowValidatorException(message);
                    }

                    if (mustHavePosition && placement.getPosition() == null) {
                        throw new WorkflowValidatorException(message);
                    }
                }
            }

        } catch (NumberFormatException ex) {
            throw new WorkflowValidatorException("Validation plug-in not configured. Incorrect value ("
                    + fieldCount + ") set for property " + PROPERTY_COUNT);
        }

    }

    @Override
    public Map<String, String> getAvailableProperties() {
        if (availableProperties == null) {
            availableProperties = new LinkedHashMap<String, String>();
            availableProperties.put(bundle.getString(PROPERTY_OPERATION),
                    PROPERTY_OPERATION);
            availableProperties.put(bundle.getString(PROPERTY_COUNT),
                    PROPERTY_COUNT);
            availableProperties.put(bundle.getString(PROPERTY_MESSAGE),
                    PROPERTY_MESSAGE);
            availableProperties.put(bundle.getString(
                    PROPERTY_MUST_HAVE_EDITION), PROPERTY_MUST_HAVE_EDITION);
            availableProperties.put(bundle.getString(
                    PROPERTY_MUST_HAVE_SECTION), PROPERTY_MUST_HAVE_SECTION);
            availableProperties.put(bundle.getString(PROPERTY_MUST_HAVE_START),
                    PROPERTY_MUST_HAVE_START);
            availableProperties.put(bundle.getString(
                    PROPERTY_MUST_HAVE_POSITION), PROPERTY_MUST_HAVE_POSITION);
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

    @Override
    public String getAbout() {
        return bundle.getString("PLUGIN_ABOUT");
    }
}
