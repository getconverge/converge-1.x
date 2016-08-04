/*
 * Copyright (C) 2015 Raymond Wanyoike
 *
 * This file is part of Converge.
 *
 * Converge is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Converge is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Converge. If not, see <http://www.gnu.org/licenses/>.
 */
package dk.i2m.converge.plugins.drupal.converters;

import com.google.gson.annotations.SerializedName;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EntityConverter<T> {

    private static final Logger LOG = Logger.getLogger(EntityConverter.class.getName());

    public Map<String, String> convert(T entity) {
        Map<String, String> params = new HashMap<String, String>();

        try {
            Map<String, String> properties = BeanUtils.describe(entity);
            for (Map.Entry<String, String> entry : properties.entrySet()) {
                getField(entity, entry, params);
            }
        } catch (IllegalAccessException e) {
            LOG.log(Level.WARNING, e.getMessage());
            LOG.log(Level.FINEST, "", e);
        } catch (InvocationTargetException e) {
            LOG.log(Level.WARNING, e.getMessage());
            LOG.log(Level.FINEST, "", e);
        } catch (NoSuchMethodException e) {
            LOG.log(Level.WARNING, e.getMessage());
            LOG.log(Level.FINEST, "", e);
        }

        return params;
    }

    private void getField(T entity, Map.Entry<String, String> entry, Map<String, String> params) throws SecurityException {
        try {
            Field field = entity.getClass().getDeclaredField(entry.getKey());
            SerializedName annotation = field.getAnnotation(SerializedName.class);
            params.put(annotation != null
                    ? annotation.value()
                    : entry.getKey(), entry.getValue());
        } catch (NoSuchFieldException e) {
            LOG.log(Level.FINEST, e.getMessage());
        }
    }
}
