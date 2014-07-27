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
 * Plug-in for uploading news items to an Atom Publishing compatible service.
 * The plug-in is implemented using the Apache Abdera library. The plug-in
 * consists of an <strong>Outlet Action</stron> implementing the
 * {@link dk.i2m.converge.core.plugin.EditionAction} interface and a
 * <em>Converter</em> that turns a
 * {@link dk.i2m.converge.core.content.NewsItemPlacement} into an Atom
 * {@link org.apache.abdera.model.Entry}.
 *
 * @see <a href="http://abdera.apache.org">Apache Abdera homepage</a>
 * @see <a
 * href="https://getconverge.atlassian.net/wiki/display/UD/AtomPub">AtomPub
 * Plugin User Documentation</a>
 */
package com.getconverge.plugins.atompub;
