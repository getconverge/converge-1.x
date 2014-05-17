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
package dk.i2m.converge.core.plugin;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author Allan Lykke Christensen
 */
public class PropertyOption {

    public static final int NO_LIMIT = 0;

    private String label;

    private String help;

    private PropertyInputType type;

    private Map<String, String> values;

    private int count = NO_LIMIT;

    public PropertyOption() {
        this.values = new LinkedHashMap<String, String>();
        this.label = "";
        this.type = PropertyInputType.TEXT;
        this.count = NO_LIMIT;
        this.help = "";
    }

    public PropertyOption(String label, PropertyInputType type, Map<String, String> values, int count) {
        this.label = label;
        this.type = type;
        this.values = values;
        this.count = count;
        this.help = "";
    }

    public PropertyOption(String label, PropertyInputType type, String value, int count) {
        this.label = label;
        this.type = type;
        this.values = new LinkedHashMap<String, String>();
        this.values.put(value, value);
        this.count = count;
        this.help = "";
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public PropertyInputType getType() {
        return type;
    }

    public void setType(PropertyInputType type) {
        this.type = type;
    }

    public Map<String, String> getValues() {
        return values;
    }

    public void setValues(Map<String, String> values) {
        this.values = values;
    }

    public String getHelp() {
        return help;
    }

    public void setHelp(String help) {
        this.help = help;
    }
}
