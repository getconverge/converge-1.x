/*
 * Copyright (C) 2011 - 2012 Interactive Media Management
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
package dk.i2m.converge.core.content.catalogue;

import dk.i2m.converge.core.plugin.CatalogueEventException;
import dk.i2m.converge.core.plugin.CatalogueHook;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.*;
import org.eclipse.persistence.annotations.PrivateOwned;

/**
 * Configured instance of a {@link CatalogueHook}.
 *
 * @author Allan Lykke Christensen
 */
@Entity
@Table(name = "catalogue_hook")
public class CatalogueHookInstance implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "catalogue_id")
    private Catalogue catalogue;

    @Column(name = "execute_order")
    private Integer executeOrder = 1;

    @Column(name = "label")
    private String label = "";

    @Column(name = "hook_class")
    private String hookClass = null;

    @Column(name = "manual")
    private boolean manual = false;

    @Column(name = "asynchronous")
    private boolean asynchronous = false;

    @OneToMany(mappedBy = "catalogueHook", cascade = CascadeType.ALL, fetch =
    FetchType.EAGER)
    @PrivateOwned
    private List<CatalogueHookInstanceProperty> properties =
            new ArrayList<CatalogueHookInstanceProperty>();

    public CatalogueHookInstance() {
    }

    public Catalogue getCatalogue() {
        return catalogue;
    }

    public void setCatalogue(Catalogue catalogue) {
        this.catalogue = catalogue;
    }

    public Integer getExecuteOrder() {
        return executeOrder;
    }

    public void setExecuteOrder(Integer executeOrder) {
        this.executeOrder = executeOrder;
    }

    public String getHookClass() {
        return hookClass;
    }

    public void setHookClass(String hookClass) {
        this.hookClass = hookClass;
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

    public boolean isManual() {
        return manual;
    }

    public void setManual(boolean manual) {
        this.manual = manual;
    }

    /**
     * Determines if the hook should be executed asynchronously.
     * <p/>
     * @return {@code true} if the hook should be executed asynchronously, otherwise {@code false}
     */
    public boolean isAsynchronous() {
        return asynchronous;
    }

    /**
     * Sets the type of operation for the hook.
     * <p/>
     * @param asynchronous {@code true} if the hook should be executed asynchronously, otherwise {@code false}
     */
    public void setAsynchronous(boolean asynchronous) {
        this.asynchronous = asynchronous;
    }

    public List<CatalogueHookInstanceProperty> getProperties() {
        return properties;
    }

    public void setProperties(List<CatalogueHookInstanceProperty> properties) {
        this.properties = properties;
    }

    public Map<String, String> getPropertiesAsMap() {
        Map<String, String> map = new HashMap<String, String>();
        for (CatalogueHookInstanceProperty property : getProperties()) {
            map.put(property.getKey(), property.getValue());
        }
        return map;
    }

    public CatalogueHook getHook() throws CatalogueEventException {
        try {
            Class c = Class.forName(getHookClass());
            CatalogueHook hook = (CatalogueHook) c.newInstance();
            return hook;
        } catch (ClassNotFoundException ex) {
            throw new CatalogueEventException("Could not find action: "
                    + getHookClass(), ex);
        } catch (InstantiationException ex) {
            throw new CatalogueEventException(
                    "Could not instantiate hook [" + getHookClass()
                    + "]. Check to ensure that the hook has a public contructor with no arguments",
                    ex);
        } catch (IllegalAccessException ex) {
            throw new CatalogueEventException("Could not access hook: "
                    + getHookClass(), ex);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CatalogueHookInstance other = (CatalogueHookInstance) obj;
        if (this.id != other.id
                && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }
}
