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

/**
 * Plug-in for uploading news items to a Drupal 7.x with the Services REST
 * Server module. The plug-in consists of an <strong>Outlet Action</strong>
 * implementing the {@link dk.i2m.converge.core.plugin.EditionAction} interface
 * and <em>Converters</em> that turn
 * {@link dk.i2m.converge.core.content.NewsItemPlacement} into data that can be
 * sent to Drupal.
 *
 * @see <a href="https://getconverge.atlassian.net/wiki/display/UD/Drupal+Services+Client">
 * Drupal2 Plugin User Documentation</a>
 */
package dk.i2m.converge.plugins.drupal;
