/*
 * OutletType.java
 * 
 * Copyright (C) 2008 Allan Lykke Christensen
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
package dk.i2m.converge.core.workflow;

/**
 * Type of {@link Outlet}. The {@link OutletType} represents the media
 * which the outlet is distributed.
 *
 * @author Allan Lykke Christensen
 */
public enum OutletType {

    PRINT, AUDIO, VIDEO, WEB, MOBILE, NEWSLETTER
}
