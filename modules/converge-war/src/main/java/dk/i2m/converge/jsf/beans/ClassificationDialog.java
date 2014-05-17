/*
 * Copyright (C) 2012 Interactive Media Management
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
package dk.i2m.converge.jsf.beans;

import dk.i2m.converge.core.metadata.Concept;
import dk.i2m.converge.core.metadata.Subject;
import dk.i2m.converge.ejb.facades.MetaDataFacadeLocal;
import dk.i2m.converge.ejb.services.MetaDataServiceLocal;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.event.ActionEvent;
import org.richfaces.component.html.HtmlTree;
import org.richfaces.event.NodeSelectedEvent;

/**
 * JSF backing bean for the classification dialog.
 *
 * @author Allan Lykke Christensen
 */
public class ClassificationDialog {

    @EJB private MetaDataFacadeLocal metaDataFacade;

    @EJB private MetaDataServiceLocal metaDataService;

    /** Name of the {@link Concept}. */
    private String name = "";

    /** Definition of the {@link Concept}. */
    private String definition = "";

    /** Type of the {@link Concept}. */
    private String type = "";

    /** List of selected {@link Concept}s. */
    private List<Concept> selectedConcepts = new ArrayList<Concept>();

    /**
     * Creates a new instance of {@link ClassificationDialog}.
     */
    public ClassificationDialog() {
    }

    /**
     * Event handler for resetting the entry of a new {@link Concept}.
     * <p/>
     * @param event Event that invoked the handler
     */
    public void onReset(ActionEvent event) {
        this.name = "";
        this.definition = "";
        this.type = "";
    }

    /**
     * Event handler for removing all selected {@link Concept}s.
     * <p/>
     * @param event Event that invoked the handler
     */
    public void onRemoveAll(ActionEvent event) {
        selectedConcepts = new ArrayList<Concept>();
    }
    
    public void onSelectSubject(NodeSelectedEvent event) {
        HtmlTree tree = (HtmlTree) event.getComponent();
        Subject subj = (Subject) tree.getRowData();
        selectedConcepts.add(subj);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
