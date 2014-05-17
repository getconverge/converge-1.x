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

import dk.i2m.converge.jsf.beans.Bundle;
import dk.i2m.jsf.JsfUtils;
import java.io.IOException;
import java.util.Iterator;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;
import org.apache.commons.lang.StringEscapeUtils;

/**
 * Message renderer based on the Gritter plug-in for jQuery.
 *
 * @see <a href="http://boedesign.com/blog/2009/07/11/growl-for-jquery-gritter/">Growl for jQuery Gritter</a>
 * @author Allan Lykke Christensen
 */
public class MessageRenderer extends Renderer {

    @Override
    public void encodeBegin(FacesContext context, UIComponent component) throws
            IOException {
        Message alertMessage = (Message) component;

        Iterator iter;
        if (alertMessage.getFor() != null) {
            // Locate the component for which to display messages  
            UIComponent forComponent = alertMessage.findComponent(alertMessage.
                    getFor());

            // If the component could not be found end processing  
            if (forComponent == null) {
                return;
            }

            iter = context.getMessages(forComponent.getClientId(context));
        } else {
            iter = context.getMessages();
        }

        // Iterate through messages for the component  

        if (iter.hasNext()) {
            ResponseWriter writer = context.getResponseWriter();

            // Start the script tag

            writer.startElement("script", alertMessage);
            writer.writeAttribute("type", "text/javascript", null);

            // Construct one big string of all messages  
            StringBuilder message = new StringBuilder();
            while (iter.hasNext()) {
                FacesMessage msg = (FacesMessage) iter.next();

                message.append("jQuery.gritter.add({title: '");

                String severity = "SEVERITY_INFO";
                String severityClass = "msg_info";

                if (msg.getSeverity().getOrdinal() == FacesMessage.SEVERITY_ERROR.
                        getOrdinal()) {
                    severity = "SEVERITY_ERROR";
                    severityClass = "msg_error";
                } else if (msg.getSeverity().getOrdinal() == FacesMessage.SEVERITY_FATAL.
                        getOrdinal()) {
                    severity = "SEVERITY_FATAL";
                    severityClass = "msg_fatal";
                } else if (msg.getSeverity().getOrdinal() == FacesMessage.SEVERITY_INFO.
                        getOrdinal()) {
                    severity = "SEVERITY_INFO";
                    severityClass = "msg_info";
                } else if (msg.getSeverity().getOrdinal() == FacesMessage.SEVERITY_WARN.
                        getOrdinal()) {
                    severity = "SEVERITY_WARN";
                    severityClass = "msg_warn";
                }

                String title = StringEscapeUtils.escapeJavaScript(JsfUtils.
                        getResourceBundle(Bundle.i18n.name()).getString(
                        "Generic_dk.i2m.converge.jsf.components.message."
                        + severity));

                message.append(title);
                message.append("',text: '");
                message.append(StringEscapeUtils.escapeJavaScript(
                        msg.getDetail()));
                message.append("',class_name: '");
                message.append(severityClass);
                message.append("'});");
            }
            String out = message.toString();

            // Output the javascript code for displaying the alert dialogue  
            writer.writeText(out.toCharArray(), 0, out.length());

            // End the script tag  
            writer.endElement("script");
        }
    }

    @Override
    public void decode(FacesContext ctx, UIComponent component) {
        if (ctx == null || component == null) {
            throw new NullPointerException();
        }
    }
}
