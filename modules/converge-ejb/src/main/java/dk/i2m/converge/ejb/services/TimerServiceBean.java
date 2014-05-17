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
package dk.i2m.converge.ejb.services;

import dk.i2m.converge.domain.SystemTimer;
import dk.i2m.converge.ejb.facades.CatalogueFacadeLocal;
import dk.i2m.converge.ejb.facades.OutletFacadeLocal;
import dk.i2m.converge.ejb.facades.SearchEngineLocal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.*;

/**
 * Stateless session bean implementing the timer service of the system
 *
 * @author Allan Lykke Christensen
 */
@Stateless
public class TimerServiceBean implements TimerServiceLocal {

    private static final Logger LOG = Logger.getLogger(TimerServiceBean.class.getName());

    @Resource private TimerService timerService;

    @EJB private ConfigurationServiceLocal cfgService;

    @EJB private NewswireServiceLocal newswireService;

    @EJB private OutletFacadeLocal outletFacade;

    @EJB private CatalogueFacadeLocal catalogueFacade;

    @EJB private SearchEngineLocal searchEngineService;

    /** {@inheritDoc} */
    @Override
    public void startTimer(PeriodicTimer timer) {
        Calendar now = Calendar.getInstance();
        now.set(Calendar.HOUR_OF_DAY, 0);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);
        now.add(Calendar.DATE, -1);

        boolean foundTimer = false;
        for (Timer t : (Collection<Timer>) timerService.getTimers()) {
            if (t.getInfo().equals(timer.name())) {
                LOG.log(Level.INFO, "{0} is already active, next timeout on {1, date, d. MMMM} at {1, date, h:mm a}", new Object[]{timer.name(), t.getNextTimeout()});
                foundTimer = true;
            }
        }

        if (!foundTimer) {
            String strReloadInterval = cfgService.getString(timer.interval());
            Long reloadInterval = Long.valueOf(strReloadInterval) * 60L * 1000L;
            LOG.log(Level.INFO, "Starting timer [{0}] at {1} repeat every: {2} ms / {3} hrs / {4} mins ", new Object[]{timer.name(), now.getTime().toString(), reloadInterval, reloadInterval / 3600000L, reloadInterval / 60000L});
            timerService.createTimer(now.getTime(), reloadInterval, timer.name());
        }
    }

    /** {@inheritDoc} */
    @Override
    public void stopTimer(PeriodicTimer timer) {
        for (Timer t : (Collection<Timer>) timerService.getTimers()) {
            if (t.getInfo().equals(timer.name())) {
                LOG.log(Level.INFO, "Stopping timer: {0}", new Object[]{timer.name()});
                t.cancel();
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public void startTimers() {
        LOG.log(Level.INFO, "Starting timers");
        for (PeriodicTimer systemTimer : PeriodicTimer.values()) {
            startTimer(systemTimer);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void stopTimers() {
        LOG.log(Level.INFO, "Stopping timers");
        for (PeriodicTimer systemTimer : PeriodicTimer.values()) {
            stopTimer(systemTimer);
        }
    }

    /** {@inheritDoc} */
    @Override
    public List<dk.i2m.converge.domain.SystemTimer> getAllTimers() {
        List<dk.i2m.converge.domain.SystemTimer> timers = new ArrayList<dk.i2m.converge.domain.SystemTimer>();

        for (PeriodicTimer sTimer : PeriodicTimer.values()) {
            dk.i2m.converge.domain.SystemTimer sysTimer = new dk.i2m.converge.domain.SystemTimer();
            boolean set = false;
            for (Timer timer : (Collection<Timer>) timerService.getTimers()) {
                if (timer.getInfo().equals(sTimer.name())) {
                    sysTimer.setNextTimeout(timer.getNextTimeout());
                    sysTimer.setTimeRemaining(timer.getTimeRemaining());
                    sysTimer.setName((String) timer.getInfo());
                    set = true;
                    break;
                }
            }

            if (!set) {
                sysTimer.setNextTimeout(null);
                sysTimer.setTimeRemaining(null);
                sysTimer.setName(sTimer.name());
            }

            timers.add(sysTimer);
        }
        return timers;
    }

    /** {@inheritDoc} */
    @Override
    public List<dk.i2m.converge.domain.SystemTimer> getActiveTimers() {
        List<dk.i2m.converge.domain.SystemTimer> timers = new ArrayList<dk.i2m.converge.domain.SystemTimer>();
        for (Timer timer : (Collection<Timer>) timerService.getTimers()) {
            dk.i2m.converge.domain.SystemTimer sysTimer = new dk.i2m.converge.domain.SystemTimer();
            sysTimer.setNextTimeout(timer.getNextTimeout());
            sysTimer.setTimeRemaining(timer.getTimeRemaining());
            sysTimer.setName((String) timer.getInfo());
            timers.add(sysTimer);
        }
        return timers;
    }

    /**
     * Executes a {@link Timer}
     *
     * @param timer
     *          Timer that initiated the timeout
     */
    @Timeout
    public void executeTimer(Timer timer) {
        LOG.log(Level.FINE, "Executing timer [{0}]", new Object[]{timer.getInfo()});

        try {
            if (timer.getInfo().equals(PeriodicTimer.NEWSWIRE.name())) {
                newswireService.downloadNewswireServices();
            }else if (timer.getInfo().equals(PeriodicTimer.NEWSWIRE_PURGE.name())) {
                newswireService.purgeNewswires();
            } else if (timer.getInfo().equals(PeriodicTimer.EDITION.name())) {
                outletFacade.closeOverdueEditions();
            } else if (PeriodicTimer.CATALOGUE_WATCH.name().equals(timer.getInfo())) {
                catalogueFacade.scanDropPoints();
            } else if (PeriodicTimer.SEARCH_ENGINE_INDEXING.name().equals(timer.getInfo())) {
                searchEngineService.processIndexingQueue();
            } else if (PeriodicTimer.NEWSWIRE_BASKET.name().equals(timer.getInfo())) {
                newswireService.dispatchBaskets();
            } else {
                LOG.log(Level.WARNING, "Ignoring unknown timer [{0}]", new Object[]{timer.getInfo()});
            }
        } catch (Throwable t) {
            LOG.log(Level.SEVERE, t.getMessage(), t);
        }
    }

    @Override
    public SystemTimer getTimer(PeriodicTimer timer) throws TimerException {
        for (SystemTimer t : getAllTimers()) {
            if (t.getName().equals(timer.name())) {
                return t;
            }
        }
        throw new TimerException(timer.name() + " does not exist");
    }
}
