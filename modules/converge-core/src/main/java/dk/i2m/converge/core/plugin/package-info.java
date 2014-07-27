/*
 * Copyright (C) 2014 Allan Lykke Christensen
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
/**
 * Implementation of the pluggable architecture. There are five extension points
 * in Converge
 * <ul>
 * <li>{@link dk.i2m.converge.core.plugin.WorkflowAction} - a piece of code that
 * is executed upon selecting a workflow option</li>
 * <li>{@link dk.i2m.converge.core.plugin.WorkflowValidator} - a piece of code
 * that validates a new items before allowing a workflow option to be
 * executed</li>
 * <li>{@link dk.i2m.converge.core.plugin.EditionAction} - a piece of code that
 * is executed upon closing an edition</li>
 * <li>{@link dk.i2m.converge.core.plugin.CatalogueHook} - a piece of code that
 * is executed when an item is added or updated in a catalogue</li>
 * <li>{@link dk.i2m.converge.core.plugin.NewswireDecoder} - the ability to
 * write code that decode external news so that it appears as a newswire service
 * inside Converge</li>
 * </ul>
 * All extension points derive from the
 * {@link dk.i2m.converge.core.plugin.Plugin} interface.
 *
 * @see <a
 * href="https://getconverge.atlassian.net/wiki/display/CON/Plugin+Architecture">Plugin
 * Architecture (Converge Developer Documentation)</a>
 */
package dk.i2m.converge.core.plugin;
