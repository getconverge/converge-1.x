/*
 * Copyright (C) 2011 Interactive Media Management
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package dk.i2m.converge.plugins.validators.lengthvalidator;

import dk.i2m.converge.core.content.NewsItem;
import dk.i2m.converge.core.content.NewsItemField;
import dk.i2m.converge.core.plugin.WorkflowValidator;
import dk.i2m.converge.core.plugin.WorkflowValidatorException;
import dk.i2m.converge.core.workflow.WorkflowStep;
import dk.i2m.converge.core.workflow.WorkflowStepValidator;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

/**
 * Validator for checking the length of a {@link NewsItem} field.
 *
 * @author Allan Lykke Christensen
 */
@dk.i2m.converge.core.annotations.WorkflowValidator
public class LengthValidator implements WorkflowValidator {

    public static final String PROPERTY_FIELD = "Field";

    public static final String PROPERTY_OPERATION = "Operation";

    public static final String PROPERTY_LENGTH = "Length";

    public static final String PROPERTY_MESSAGE = "Message";

    private static final Logger LOG = Logger.getLogger(LengthValidator.class.getName());

    private Map<String, String> availableProperties = null;

    private ResourceBundle bundle = ResourceBundle.getBundle("dk.i2m.converge.plugins.validators.lengthvalidator.Messages");

    @Override
    public void execute(NewsItem item, WorkflowStep step, WorkflowStepValidator validator) throws WorkflowValidatorException {
        Map<String, String> properties = validator.getPropertiesAsMap();

        if (!properties.containsKey(PROPERTY_FIELD)) {
            throw new WorkflowValidatorException("Validation plug-in not configured. Missing property " + PROPERTY_FIELD);
        }

        if (!properties.containsKey(PROPERTY_OPERATION)) {
            throw new WorkflowValidatorException("Validation plug-in not configured. Missing property " + PROPERTY_OPERATION);
        }

        if (!properties.containsKey(PROPERTY_LENGTH)) {
            throw new WorkflowValidatorException("Validation plug-in not configured. Missing property " + PROPERTY_LENGTH);
        }

        if (!properties.containsKey(PROPERTY_MESSAGE)) {
            throw new WorkflowValidatorException("Validation plug-in not configured. Missing property " + PROPERTY_MESSAGE);
        }

        String fieldName = properties.get(PROPERTY_FIELD);
        String operation = properties.get(PROPERTY_OPERATION);
        String fieldLength = properties.get(PROPERTY_LENGTH);
        String message = properties.get(PROPERTY_MESSAGE);

        NewsItemField field;

        String fieldValue = "";
        try {
            field = NewsItemField.valueOf(fieldName.toUpperCase());
            int length = Integer.valueOf(fieldLength);

            switch (field) {
                case BRIEF:
                    fieldValue = item.getBrief();
                    break;
                case BY_LINE:
                    fieldValue = item.getByLine();
                    break;
                case STORY:
                    fieldValue = item.getStory();
                    break;
                case TITLE:
                    fieldValue = item.getTitle();
                    break;
                default:
                    throw new WorkflowValidatorException("Validation plug-in not configured. Unsupported field " + field.name());
            }

            if (operation.equalsIgnoreCase(">")) {
                if (!(fieldValue.length() > length)) {
                    throw new WorkflowValidatorException(message);
                }
            } else if (operation.equalsIgnoreCase("<")) {
                if (!(fieldValue.length() < length)) {
                    throw new WorkflowValidatorException(message);
                }
            } else if (operation.equalsIgnoreCase("=")) {
                if (!(fieldValue.length() == length)) {
                    throw new WorkflowValidatorException(message);
                }
            } else {
                throw new WorkflowValidatorException("Validation plug-in not configured. Incorrect value (" + operation + ") set for property " + PROPERTY_OPERATION);
            }

        } catch (NumberFormatException ex) {
            throw new WorkflowValidatorException("Validation plug-in not configured. Incorrect value (" + fieldLength + ") set for property " + PROPERTY_LENGTH);
        } catch (IllegalArgumentException ex) {
            throw new WorkflowValidatorException("Validation plug-in not configured. Incorrect value (" + fieldName + ") set for property " + PROPERTY_FIELD);
        }

    }

    @Override
    public Map<String, String> getAvailableProperties() {
        if (availableProperties == null) {
            availableProperties = new LinkedHashMap<String, String>();
            availableProperties.put(bundle.getString(PROPERTY_FIELD), PROPERTY_FIELD);
            availableProperties.put(bundle.getString(PROPERTY_OPERATION), PROPERTY_OPERATION);
            availableProperties.put(bundle.getString(PROPERTY_LENGTH), PROPERTY_LENGTH);
            availableProperties.put(bundle.getString(PROPERTY_MESSAGE), PROPERTY_MESSAGE);
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

    @Override
    public String getAbout() {
        return bundle.getString("PLUGIN_ABOUT");
    }
}
