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
package dk.i2m.converge.ejb.facades;

import dk.i2m.converge.core.content.forex.Currency;
import dk.i2m.converge.core.content.forex.Rate;
import dk.i2m.converge.core.content.markets.FinancialMarket;
import dk.i2m.converge.core.content.markets.MarketValue;
import dk.i2m.converge.core.content.weather.Forecast;
import dk.i2m.converge.core.content.weather.Location;
import dk.i2m.converge.core.content.weather.Situation;
import dk.i2m.converge.ejb.services.DaoServiceLocal;
import dk.i2m.converge.core.DataNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 * Implementation of the Listing Facade Stateless Session Bean.
 *
 * @author Allan Lykke Christensen
 */
@Stateless
public class ListingFacadeBean implements ListingFacadeLocal {

    @EJB private DaoServiceLocal daoService;

    @Override
    public Currency findCurrencyById(Long id) throws DataNotFoundException {
        return daoService.findById(Currency.class, id);
    }

    @Override
    public List<Currency> findCurrencies() {
        return daoService.findAll(Currency.class);
    }

    @Override
    public Currency createCurrency(Currency currency) {
        return daoService.create(currency);
    }

    @Override
    public Currency updateCurrency(Currency currency) {
        return daoService.update(currency);
    }

    @Override
    public void deleteCurrency(Currency currency) {
        daoService.delete(Currency.class, currency.getId());
    }

    @Override
    public Rate findRateById(Long id) throws DataNotFoundException {
        return daoService.findById(Rate.class, id);
    }

    @Override
    public Rate createRate(Rate rate) {
        return daoService.create(rate);
    }

    @Override
    public Rate updateRate(Rate rate) {
        return daoService.update(rate);
    }

    @Override
    public void deleteRate(Rate rate) {
        daoService.delete(Rate.class, rate.getId());
    }

    @Override
    public List<Rate> findRates() {
        return daoService.findAll(Rate.class);
    }

    @Override
    public List<MarketValue> findMarketValues() {
        return daoService.findAll(MarketValue.class);
    }

    @Override
    public MarketValue createMarketValue(MarketValue value) {
        return daoService.create(value);
    }

    @Override
    public MarketValue updateMarketValue(MarketValue value) {
        return daoService.update(value);
    }

    @Override
    public void deleteMarketValue(MarketValue value) {
        daoService.delete(MarketValue.class, value.getId());
    }

    @Override
    public MarketValue findMarketValueById(Long id) throws DataNotFoundException {
        return daoService.findById(MarketValue.class, id);
    }

    @Override
    public List<FinancialMarket> findFinancialMarkets() {
        return daoService.findAll(FinancialMarket.class);
    }

    @Override
    public FinancialMarket findFinancialMarketById(Long id) throws DataNotFoundException {
        return daoService.findById(FinancialMarket.class, id);
    }

    @Override
    public List<Situation> findWeatherSituations() {
        return daoService.findAll(Situation.class);
    }

    @Override
    public Situation findWeatherSituationById(Long id) throws DataNotFoundException {
        return daoService.findById(Situation.class, id);
    }

    @Override
    public List<Location> findWeatherLocations() {
        return daoService.findAll(Location.class);
    }

    @Override
    public Location findWeatherLocationById(Long id) throws DataNotFoundException {
        return daoService.findById(Location.class, id);
    }

    @Override
    public Forecast findForecastById(Long id) throws DataNotFoundException {
        return daoService.findById(Forecast.class, id);
    }

    @Override
    public List<Forecast> findForecasts() {
        return daoService.findAll(Forecast.class);
    }

    @Override
    public Forecast createForecast(Forecast selectedForecast) {
        return daoService.create(selectedForecast);
    }

    @Override
    public void deleteForecast(Forecast selectedForecast) {
        daoService.delete(Forecast.class, selectedForecast.getId());
    }

    @Override
    public Forecast updateForecast(Forecast selectedForecast) {
        return daoService.update(selectedForecast);
    }

    @Override
    public List<Forecast> findLatestForecasts() {
        List<Forecast> all = findForecasts();
        Map<Location, Forecast> latest = new HashMap<Location, Forecast>();

        for (Forecast forecast : all) {
            if (forecast.getLocation().isActive()) {
                if (latest.containsKey(forecast.getLocation())) {
                    Forecast exForecast = latest.get(forecast.getLocation());
                    if (exForecast.getUpdated().before(forecast.getUpdated())) {
                        latest.put(forecast.getLocation(), forecast);
                    }
                } else {
                    latest.put(forecast.getLocation(), forecast);
                }
            }
        }

        return new ArrayList(latest.values());
    }

    @Override
    public List<MarketValue> findLatestMarketValues() {
        List<MarketValue> all = findMarketValues();
        Map<FinancialMarket, MarketValue> latest = new HashMap<FinancialMarket, MarketValue>();

        for (MarketValue marketValue : all) {
            if (marketValue.getFinancialMarket().isActive()) {
                if (latest.containsKey(marketValue.getFinancialMarket())) {
                    MarketValue exMarketValue = latest.get(marketValue.getFinancialMarket());
                    if (exMarketValue.getUpdated().before(marketValue.getUpdated())) {
                        latest.put(marketValue.getFinancialMarket(), marketValue);
                    }
                } else {
                    latest.put(marketValue.getFinancialMarket(), marketValue);
                }
            }
        }

        return new ArrayList(latest.values());
    }

    @Override
    public List<Rate> findLatestForexRates() {
        List<Rate> all = findRates();
        Map<Currency, Rate> latest = new HashMap<Currency, Rate>();

        for (Rate rate : all) {
            if (rate.getCurrency().isActive()) {
                if (latest.containsKey(rate.getCurrency())) {
                    Rate exRate = latest.get(rate.getCurrency());
                    if (exRate.getUpdated().before(rate.getUpdated())) {
                        latest.put(rate.getCurrency(), rate);
                    }
                } else {
                    latest.put(rate.getCurrency(), rate);
                }
            }
        }

        return new ArrayList(latest.values());
    }

    @Override
    public FinancialMarket createFinancialMarket(FinancialMarket financialMarket) {
        return daoService.create(financialMarket);
    }

    @Override
    public FinancialMarket updateFinancialMarket(FinancialMarket financialMarket) {
        return daoService.update(financialMarket);
    }

    @Override
    public void deleteFinancialMarket(FinancialMarket financialMarket) {
        daoService.delete(FinancialMarket.class, financialMarket.getId());
    }

    @Override
    public Situation createWeatherSituation(Situation situation) {
        return daoService.create(situation);
    }

    @Override
    public Situation updateWeatherSituation(Situation situation) {
        return daoService.update(situation);
    }

    @Override
    public void deleteWeatherSituation(Situation situation) {
        daoService.delete(Situation.class, situation.getId());
    }

    @Override
    public Location createWeatherLocation(Location location) {
        return daoService.create(location);
    }

    @Override
    public Location updateWeatherLocation(Location location) {
        return daoService.update(location);
    }

    @Override
    public void deleteWeatherLocation(Location location) {
        daoService.delete(Location.class, location.getId());
    }
}
