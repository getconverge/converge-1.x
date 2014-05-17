/*
 * Copyright (C) 2010 - 2011 Interactive Media Management
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
package dk.i2m.converge.ejb.services;

import dk.i2m.converge.core.ConfigurationKey;
import dk.i2m.converge.core.Configuration;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 * Stateless session bean providing a service for obtaining the application
 * configuration.
 *
 * @author Allan Lykke Christensen
 */
@Stateless
public class ConfigurationServiceBean implements ConfigurationServiceLocal {

    @EJB private DaoServiceLocal daoService;

    private ResourceBundle cfg = ResourceBundle.getBundle("dk.i2m.converge.Configuration");

    private static final Logger LOG = Logger.getLogger(ConfigurationServiceBean.class.getName());

    /** {@inheritDoc } */
    @Override
    public String getMessage(String key) {
        String bundle = getString(ConfigurationKey.MESSAGE_BUNDLE);
        return ResourceBundle.getBundle(bundle).getString(key);
    }

    /** {@inheritDoc } */
    @Override
    public <T> T get(Class<T> type, ConfigurationKey key, T defaultValue) {

        List results = daoService.findWithNamedQuery(Configuration.FIND_BY_KEY,
                QueryBuilder.with("cfgKey", key).parameters());
        if (results.size() == 1) {
            Object obj = results.iterator().next();
            if (type == Integer.class) {
                obj = Integer.valueOf((String) obj);
            } else if (type == Long.class) {
                obj = Long.valueOf((String) obj);
            }
            return (T) obj;
        } else {
            try {
                Object obj = cfg.getObject(key.name());
                if (type == Integer.class) {
                    obj = Integer.valueOf((String) obj);
                } else if (type == Long.class) {
                    obj = Long.valueOf((String) obj);
                }

                return (T) obj;
            } catch (Exception ex) {
                LOG.log(Level.WARNING, "Unknown configuration: {0}", new Object[]{key.name()});
                return defaultValue;
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public String getString(ConfigurationKey key) {
        List results = daoService.findWithNamedQuery(Configuration.FIND_BY_KEY,
                QueryBuilder.with("cfgKey", key).parameters());
        if (results.size() == 1) {
            return ((Configuration) results.get(0)).getValue();
        } else {
            try {
                return cfg.getString(key.name());
            } catch (Exception ex) {
                LOG.log(Level.WARNING, "Unknown configuration: {0}", new Object[]{key.name()});
                return key.name();
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public Integer getInteger(ConfigurationKey key) {
        try {
            List results = daoService.findWithNamedQuery(Configuration.FIND_BY_KEY,
                    QueryBuilder.with("cfgKey", key).parameters());
            if (results.size() == 1) {
                return Integer.valueOf(((Configuration) results.get(0)).getValue());
            } else {
                try {
                    return Integer.valueOf(cfg.getString(key.name()));
                } catch (Exception ex) {
                    LOG.log(Level.WARNING, "Unknown configuration: {0}", new Object[]{key.name()});
                    return -1;
                }
            }
        } catch (NumberFormatException ex) {
            LOG.log(Level.WARNING, "Couldn't retrieve configuration: {0}. {1}", new Object[]{key.name(), ex.getMessage()});
            return -1;
        }
    }

    /** {@inheritDoc} */
    @Override
    public Boolean getBoolean(ConfigurationKey key) {
        List results = daoService.findWithNamedQuery(Configuration.FIND_BY_KEY,
                QueryBuilder.with("cfgKey", key).parameters());
        if (results.size() == 1) {
            return Boolean.valueOf(((Configuration) results.get(0)).getValue());
        } else {
            try {
                return Boolean.valueOf(cfg.getString(key.name()));
            } catch (Exception ex) {
                LOG.log(Level.WARNING, "Unknown configuration: {0}", new Object[]{key.name()});
                return false;
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public void set(ConfigurationKey key, String value) {
        List results = daoService.findWithNamedQuery(Configuration.FIND_BY_KEY,
                QueryBuilder.with("cfgKey", key).parameters());
        if (results.size() == 1) {
            Configuration entry = (Configuration) results.get(0);
            entry.setValue(value);
            daoService.update(entry);
        } else {
            Configuration entry = new Configuration();
            entry.setKey(key);
            entry.setValue(value);
            daoService.create(entry);
        }
    }

    /** {@inheritDoc} */
    @Override
    public Locale getApplicationLocale() {
        String language = getString(ConfigurationKey.LANGUAGE);
        String country = getString(ConfigurationKey.COUNTRY);
        return new Locale(language, country);
    }

    /** {@inheritDoc} */
    @Override
    public String getVersion() {
        return getString(ConfigurationKey.VERSION);
    }

    /** {@inheritDoc} */
    @Override
    public String getBuildNumber() {
        return getString(ConfigurationKey.BUILD_TIME);
    }

    /** {@inheritDoc} */
    @Override
    public String getLongVersion() {
        String shortDisplay = getVersion();
        return shortDisplay + " " + getString(ConfigurationKey.BUILD_TIME);
    }
}
