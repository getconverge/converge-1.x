/*
 * Copyright (C) 2010 Interactive Media Management
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
package dk.i2m.converge.jsf.components.message;

import javax.faces.component.UIMessage;

/**
 * JSF Component for displaying messages as jQuery message.
 *
 * @author Allan Lykke Christensen
 */
public class Message extends UIMessage {

    public static final String MSG_COMPONENT_TYPE = "dk.i2m.converge.jsf.components.message";

    public static final String MSG_RENDERER_TYPE = "dk.i2m.converge.jsf.components.message.MessageRenderer";

    public static final String MSG_COMPONENT_FAMILY = "javax.faces.Message";

    @Override
    public String getFamily() {
        return Message.MSG_COMPONENT_FAMILY;
    }

    @Override
    public String getRendererType() {
        return Message.MSG_RENDERER_TYPE;
    }

    @Override
    public String getFor() {
        return super.getFor();
    }

    @Override
    public void setFor(String forParam) {
        super.setFor(forParam);
    }
}
