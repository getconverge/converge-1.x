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
package dk.i2m.converge.plugins.actions;

import dk.i2m.converge.core.logging.LogSeverity;
import dk.i2m.converge.core.plugin.EditionAction;
import dk.i2m.converge.core.plugin.PluginContext;
import dk.i2m.converge.core.workflow.OutletEditionAction;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Core Action is an abstract {@link EditionAction} implementing the basic
 * interface and providing convenience methods for logging.
 *
 * @author Allan Lykke Christensen
 */
public abstract class CoreAction implements EditionAction {

    enum BundleKey {

        PLUGIN_NAME, PLUGIN_DESCRIPTION, PLUGIN_ABOUT, PLUGIN_VENDOR,
        PLUGIN_BUILD_TIME
    }

    private final String PLUG_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private Map<String, String> availableProperties = null;

    private ResourceBundle bundle;

    protected String bundleClass;
    
    protected PluginContext pluginCtx;
    
    protected OutletEditionAction actionInstance;

    @Override
    public Map<String, String> getAvailableProperties() {
        if (availableProperties == null) {
            availableProperties = new LinkedHashMap<String, String>();
        }
        return availableProperties;
    }

    @Override
    public String getName() {
        return bundle.getString(BundleKey.PLUGIN_NAME.name());
    }

    @Override
    public String getAbout() {
        return bundle.getString(BundleKey.PLUGIN_ABOUT.name());
    }

    @Override
    public String getDescription() {
        return bundle.getString(BundleKey.PLUGIN_DESCRIPTION.name());
    }

    @Override
    public String getVendor() {
        return bundle.getString(BundleKey.PLUGIN_VENDOR.name());
    }

    @Override
    public Date getDate() {
        try {
            SimpleDateFormat format = new SimpleDateFormat(PLUG_DATE_FORMAT);
            String date = bundle.getString(BundleKey.PLUGIN_BUILD_TIME.name());
            return format.parse(date);
        } catch (Exception ex) {
            return Calendar.getInstance().getTime();
        }
    }

    @Override
    public ResourceBundle getBundle() {
        if (this.bundle == null) {
            this.bundle = ResourceBundle.getBundle(bundleClass);
        }
        return this.bundle;
    }
    
    protected void log(LogSeverity severity, String msg) {
        log(severity, msg, new Object[]{});
    }

    protected void log(LogSeverity severity, String msg, Object param) {
        log(severity, msg, new Object[]{param});
    }

    protected void log(LogSeverity severity, String msg, Object[] params) {
        pluginCtx.log(severity, msg, params, actionInstance, actionInstance.getId());
    }

}
