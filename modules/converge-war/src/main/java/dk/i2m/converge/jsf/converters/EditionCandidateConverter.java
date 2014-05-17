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
package dk.i2m.converge.jsf.converters;

import dk.i2m.converge.core.workflow.EditionCandidate;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

/**
 * JSF {@link Converter} for {@link EditionCandidate}s.
 *
 * @author Allan Lykke Christensen
 */
public class EditionCandidateConverter implements Converter {

    private final static Logger log = Logger.getLogger(EditionCandidateConverter.class.getName());

    private final static String SEPARATOR = ";";

    private static String serialize(EditionCandidate ec) throws IOException {
        if (ec == null) {
            log.log(Level.FINEST, "EditionCandidate is null. Serialized as empty string");
            return "";
        }

        StringBuilder sb = new StringBuilder();
        if (ec.getEditionId() != null) {
            sb.append(ec.getEditionId());
        } else {
            sb.append("0");
        }
        sb.append(SEPARATOR);
        if (ec.getOutletId() != null) {
            sb.append(ec.getOutletId());
        } else {
            sb.append("0");
        }
        sb.append(SEPARATOR);
        if (ec.getPublicationDate() != null) {
            sb.append(ec.getPublicationDate().getTime());
        } else {
            sb.append("0");
        }
        sb.append(SEPARATOR);
        if (ec.getExpirationDate() != null) {
            sb.append(ec.getExpirationDate().getTime());
        } else {
            sb.append("0");
        }
        sb.append(SEPARATOR);
        if (ec.getCloseDate() != null) {
            sb.append(ec.getCloseDate().getTime());
        } else {
            sb.append("0");
        }
        sb.append(SEPARATOR);

        String serialized = sb.toString();

        log.log(Level.FINEST, "Serialized version of Edition Candidate: {0}", serialized);

        return serialized;
    }

    private static EditionCandidate deserialize(String serialized) {
        if (serialized.equalsIgnoreCase("")) {
            log.log(Level.FINEST, "Serialized EditionCandidate is empty string");
            return null;
        }
        log.log(Level.FINEST, "Deserializing EditionCandidate {0}", serialized);

        String[] values = serialized.split(SEPARATOR);
        EditionCandidate ec = new EditionCandidate();

        if (!values[0].equalsIgnoreCase("0")) {
            ec.setEditionId(Long.valueOf(values[0]));
        }
        if (!values[1].equalsIgnoreCase("0")) {
            ec.setOutletId(Long.valueOf(values[1]));
        }
        if (!values[2].equalsIgnoreCase("0")) {
            ec.setPublicationDate(new Date(Long.valueOf(values[2])));
        }
        if (!values[3].equalsIgnoreCase("0")) {
            ec.setExpirationDate(new Date(Long.valueOf(values[3])));
        }
        if (!values[4].equalsIgnoreCase("0")) {
            ec.setCloseDate(new Date(Long.valueOf(values[4])));
        }
        return ec;
    }

    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String string) {
        try {
            EditionCandidate ec = (EditionCandidate) deserialize(string);
            return ec;
        } catch (Exception ex) {
            Logger.getLogger(EditionCandidateConverter.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object o) {
        try {
            return serialize((EditionCandidate) o);
        } catch (IOException ex) {
            Logger.getLogger(EditionCandidateConverter.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
}
