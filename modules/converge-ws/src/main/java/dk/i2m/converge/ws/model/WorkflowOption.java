/*
 * Copyright (C) 2011 Interactive Media Management
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
package dk.i2m.converge.ws.model;

import java.io.Serializable;

/**
 * Web service model containing information about available
 * workflow options for a {@link NewsItem}.
 *
 * @author Allan Lykke Christensen
 */
public class WorkflowOption implements Serializable {

    private Long optionId;

    private String label;

    private String description;

    private int displayOrder;

    /**
     * Creates a new instance of {@link WorkflowOption}.
     */
    public WorkflowOption() {
        this(0L, "", "", 0);
    }

    /**
     * Creates a new instance of {@link WorkflowOption}.
     * 
     * @param optionId
     *          Unique identifier of the workflow option
     * @param label
     *          Label of the workflow option to show to the user
     * @param description
     *          Description of the workflow option
     * @param displayOrder 
     *          Display order of the {@link WorkflowOption}
     */
    public WorkflowOption(Long optionId, String label, String description, int displayOrder) {
        this.optionId = optionId;
        this.label = label;
        this.description = description;
        this.displayOrder = displayOrder;
    }

    public Long getOptionId() {
        return optionId;
    }

    public void setOptionId(Long optionId) {
        this.optionId = optionId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }
}
