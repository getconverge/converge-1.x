/*
 * Copyright (C) 2010 - 2012 Interactive Media Management
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
package dk.i2m.converge.jsf.beans.administrator;

import dk.i2m.converge.core.ConfigurationKey;
import dk.i2m.converge.core.search.SearchEngineIndexingException;
import dk.i2m.converge.domain.SystemTimer;
import dk.i2m.converge.ejb.facades.SearchEngineLocal;
import dk.i2m.converge.ejb.services.ConfigurationServiceLocal;
import dk.i2m.converge.ejb.services.PeriodicTimer;
import dk.i2m.converge.ejb.services.TimerException;
import dk.i2m.converge.ejb.services.TimerServiceLocal;
import dk.i2m.converge.jsf.beans.Bundle;
import dk.i2m.jsf.JsfUtils;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

/**
 * Backing bean for {@code /administrator/SearchEngine.jspx}.
 *
 * @author Allan Lykke Christensen
 */
public class SearchEngine {

    private static final Logger LOG = Logger.getLogger(SearchEngine.class.
            getName());

    @EJB private SearchEngineLocal searchEngine;

    @EJB private ConfigurationServiceLocal cfgService;

    @EJB private TimerServiceLocal timerService;

    private DataModel indexQueue = null;

    public SearchEngine() {
    }

    public Date getNextProcess() {
        try {
            SystemTimer timer = timerService.getTimer(
                    PeriodicTimer.SEARCH_ENGINE_INDEXING);
            return timer.getNextTimeout();
        } catch (TimerException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String getSearchEngineURL() {
        return cfgService.getString(ConfigurationKey.SEARCH_ENGINE_URL);
    }

    public void onOptimize(ActionEvent event) {
        try {
            searchEngine.optimizeIndex();
            JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_INFO,
                    Bundle.i18n.name(), "administrator_SearchEngine_OPTIMISED");
        } catch (SearchEngineIndexingException ex) {
            JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_WARN,
                    Bundle.i18n.name(),
                    "administrator_SearchEngine_OPTIMISE_ERROR",
                    new Object[]{ex.getMessage()});
        }
    }

    public void onRefresh(ActionEvent event) {
        indexQueue = new ListDataModel(searchEngine.getIndexQueue());
    }

    public DataModel getIndexQueue() {
        if (indexQueue == null) {
            onRefresh(null);
        }
        return indexQueue;
    }
}
