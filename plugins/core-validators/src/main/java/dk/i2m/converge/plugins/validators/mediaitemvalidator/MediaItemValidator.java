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
package dk.i2m.converge.plugins.validators.mediaitemvalidator;

import dk.i2m.converge.core.content.NewsItem;
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
public class MediaItemValidator implements WorkflowValidator {

    public static final String PROPERTY_OPERATION = "Operation";

    public static final String PROPERTY_COUNT = "Count";

    public static final String PROPERTY_MESSAGE = "Message";

    private static final Logger LOG =
            Logger.getLogger(MediaItemValidator.class.getName());

    private Map<String, String> availableProperties = null;

    private ResourceBundle bundle = ResourceBundle.getBundle(
            "dk.i2m.converge.plugins.validators.mediaitemvalidator.Messages");

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

        try {
            int count = Integer.valueOf(fieldCount);
            int mediaItems = item.getMediaAttachments().size();

            if (operation.equalsIgnoreCase(">")) {
                if (!(mediaItems > count)) {
                    throw new WorkflowValidatorException(message);
                }
            } else if (operation.equalsIgnoreCase("<")) {
                if (!(mediaItems < count)) {
                    throw new WorkflowValidatorException(message);
                }
            } else if (operation.equalsIgnoreCase("=")) {
                if (!(mediaItems == count)) {
                    throw new WorkflowValidatorException(message);
                }
            } else {
                throw new WorkflowValidatorException("Validation plug-in not configured. Incorrect value ("
                        + operation + ") set for property "
                        + PROPERTY_OPERATION);
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
