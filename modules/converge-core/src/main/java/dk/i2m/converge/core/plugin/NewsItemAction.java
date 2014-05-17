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

import dk.i2m.converge.core.content.NewsItem;
import dk.i2m.converge.core.security.UserAccount;
import java.util.Map;

/**
 * Interface for implementing an action that can be
 * executed on a {@link NewsItem}.
 *
 * @author Allan Lykke Christensen
 */
public interface NewsItemAction extends Plugin {

    /**
     * Executes the {@link NewsItemAction}.
     *
     * @param ctx
     *           Context for which action is being executed
     * @param item
     *          {@link NewsItem} being processed
     * @param user
     *          User that selected invoked the action
     */
    public abstract void execute(PluginContext ctx, NewsItem item, UserAccount user);

    /**
     * Provides a map of possible properties for the action.
     *
     * @return Map of possible action properties
     */
    public abstract Map<String, String> getAvailableProperties();
}
