/*
 *  Copyright (C) 2010 - 2013 Interactive Media Management
 *  Copyright (C) 2014 Allan Lykke Christensen
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package dk.i2m.converge.core.workflow;

import dk.i2m.converge.core.plugin.EditionAction;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.eclipse.persistence.annotations.PrivateOwned;

/**
 * Action to be executed when on an {@link Edition}. The action can configured
 * for automatic invocation when the {@link Outlet} if the {@link Edition} is
 * closed or manually by a user with sufficient privileges.
 *
 * @author <a href="mailto:allan@getconverge.com">Allan Lykke Christensen</a>
 */
@Entity
@Table(name = "outlet_edition_action")
public class OutletEditionAction implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @ManyToOne
    @JoinColumn(name = "outlet_id")
    private Outlet outlet;
    @Column(name = "execute_order")
    private Integer executeOrder = 1;
    @Column(name = "label")
    private String label = "";
    @Column(name = "action_class")
    private String actionClass = null;
    @Column(name = "manual_action")
    private boolean manualAction = false;
    @OneToMany(mappedBy = "outletEditionAction", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @PrivateOwned
    private List<OutletEditionActionProperty> properties = new ArrayList<OutletEditionActionProperty>();

    /**
     * Creates a new instance of {@link OutletEditionAction}.
     */
    public OutletEditionAction() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Integer getExecuteOrder() {
        return executeOrder;
    }

    public void setExecuteOrder(Integer executeOrder) {
        this.executeOrder = executeOrder;
    }

    public Outlet getOutlet() {
        return outlet;
    }

    public void setOutlet(Outlet outlet) {
        this.outlet = outlet;
    }

    /**
     * Determines if this action should only be executed manually. Manual
     * actions are not executed when editions are closed, they can only be
     * executed manually by planners.
     *
     * @return {@code true} if this is a manual action, otherwise {@code false}
     */
    public boolean isManualAction() {
        return manualAction;
    }

    /**
     * Sets the manual action indicator of the action.
     *
     * @param manualAction {@code true} if this is a manual action, otherwise
     * {@code false}
     *
     */
    public void setManualAction(boolean manualAction) {
        this.manualAction = manualAction;
    }

    public String getActionClass() {
        return actionClass;
    }

    public void setActionClass(String actionClass) {
        this.actionClass = actionClass;
    }

    /**
     * Creates an instance of the action specified in
     * {@link OutletEditionAction#getActionClass()}.
     *
     * @return Instance of the action
     * @throws EditionActionException If the action could not be instantiated
     */
    public EditionAction getAction() throws EditionActionException {
        try {
            Class c = Class.forName(getActionClass());
            EditionAction action = (EditionAction) c.newInstance();
            return action;
        } catch (ClassNotFoundException ex) {
            throw new EditionActionException("Could not find action: " + getActionClass(), ex);
        } catch (InstantiationException ex) {
            throw new EditionActionException("Could not instantiate action [" + getActionClass() + "]. Check to ensure that the action has a public contructor with no arguments", ex);
        } catch (IllegalAccessException ex) {
            throw new EditionActionException("Could not access action: " + getActionClass(), ex);
        }
    }

    /**
     * Determine if the {@link EditionAction} specified as the
     * {@link #actionClass} is valid.
     *
     * @return {@code true} if the {@link EditionAction} is valid and
     * instantiable or {@code false} if it not valid
     */
    public boolean isActionValid() {
        try {
            getAction();
            return true;
        } catch (EditionActionException ex) {
            return false;
        }
    }

    public List<OutletEditionActionProperty> getProperties() {
        return properties;
    }

    public Map<String, String> getPropertiesAsMap() {
        Map<String, String> map = new HashMap<String, String>();
        for (OutletEditionActionProperty property : properties) {
            map.put(property.getKey(), property.getValue());
        }
        return map;
    }

    public void setProperties(List<OutletEditionActionProperty> properties) {
        this.properties = properties;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof OutletEditionAction)) {
            return false;
        }
        OutletEditionAction other = (OutletEditionAction) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }

    @Override
    public String toString() {
        return getClass().getName() + "[id=" + id + "]";
    }
}
