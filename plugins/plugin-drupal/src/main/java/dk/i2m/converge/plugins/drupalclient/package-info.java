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
 * Plug-in for uploading news items to a Drupal 7 website. The plug-in consists
 * of an <strong>Outlet Action</strong> implementing the
 * {@link dk.i2m.converge.core.plugin.EditionAction} interface and
 * <em>Converters</em> that turns
 * {@link dk.i2m.converge.core.content.NewsItemPlacement} into data that can be
 * sent to Drupal
 *
 * @see <a
 * href="https://getconverge.atlassian.net/wiki/display/UD/Drupal+Services+Client">Drupal
 * Services Plugin User Documentation</a>
 */
package dk.i2m.converge.plugins.drupalclient;
