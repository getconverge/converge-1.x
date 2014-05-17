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
import dk.i2m.converge.core.DataNotFoundException;
import java.util.List;
import javax.ejb.Local;

/**
 * Local interface for the Listing Facade.
 *
 * @author Allan Lykke Christensen
 */
@Local
public interface ListingFacadeLocal {

    List<Currency> findCurrencies();

    Currency findCurrencyById(Long id) throws DataNotFoundException;

    Currency createCurrency(Currency selectedCurrency);

    Currency updateCurrency(Currency currency);

    void deleteCurrency(Currency currency);

    java.util.List<dk.i2m.converge.core.content.forex.Rate> findLatestForexRates();

    List<Rate> findRates();

    Rate findRateById(Long id) throws DataNotFoundException;

    Rate createRate(Rate rate);

    Rate updateRate(Rate rate);

    void deleteRate(Rate rate);

    List<FinancialMarket> findFinancialMarkets();

    FinancialMarket findFinancialMarketById(Long id) throws DataNotFoundException;

    FinancialMarket createFinancialMarket(FinancialMarket financialMarket);

    FinancialMarket updateFinancialMarket(FinancialMarket financialMarket);

    void deleteFinancialMarket(FinancialMarket financialMarket);

    java.util.List<dk.i2m.converge.core.content.markets.MarketValue> findLatestMarketValues();

    List<MarketValue> findMarketValues();

    MarketValue findMarketValueById(Long id) throws DataNotFoundException;

    MarketValue createMarketValue(MarketValue value);

    MarketValue updateMarketValue(MarketValue value);

    void deleteMarketValue(MarketValue value);

    java.util.List<dk.i2m.converge.core.content.weather.Forecast> findLatestForecasts();

    List<Forecast> findForecasts();

    Forecast findForecastById(Long id) throws DataNotFoundException;

    Forecast createForecast(Forecast selectedForecast);

    void deleteForecast(Forecast selectedForecast);

    Forecast updateForecast(Forecast selectedForecast);

    List<Situation> findWeatherSituations();

    Situation findWeatherSituationById(Long id) throws DataNotFoundException;

    Situation createWeatherSituation(Situation situation);

    Situation updateWeatherSituation(Situation situation);

    void deleteWeatherSituation(Situation situation);

    List<Location> findWeatherLocations();

    Location findWeatherLocationById(Long id) throws DataNotFoundException;

    Location createWeatherLocation(Location location);

    Location updateWeatherLocation(Location location);

    void deleteWeatherLocation(Location location);
}
