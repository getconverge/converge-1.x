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
package dk.i2m.converge.plugins.joomla.client;

/**
 * Exception thrown when an incompatible Joomla plug-in is encountered.
 *
 * @author Allan Lykke Christensen
 */
public class IncompatibleJoomlaPluginException extends JoomlaException {

    public IncompatibleJoomlaPluginException(Throwable thrwbl) {
        super(thrwbl);
    }

    public IncompatibleJoomlaPluginException(String string, Throwable thrwbl) {
        super(string, thrwbl);
    }

    public IncompatibleJoomlaPluginException(String string) {
        super(string);
    }

    public IncompatibleJoomlaPluginException() {
        super();
    }
}
