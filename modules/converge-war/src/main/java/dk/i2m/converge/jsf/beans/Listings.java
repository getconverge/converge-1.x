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
package dk.i2m.converge.jsf.beans;

import dk.i2m.converge.core.content.forex.Currency;
import dk.i2m.converge.core.content.forex.Rate;
import dk.i2m.converge.core.content.markets.FinancialMarket;
import dk.i2m.converge.core.content.markets.MarketValue;
import dk.i2m.converge.core.content.weather.Forecast;
import dk.i2m.converge.core.content.weather.Location;
import dk.i2m.converge.core.content.weather.Situation;
import dk.i2m.converge.ejb.facades.ListingFacadeLocal;
import javax.ejb.EJB;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

/**
 * JSF backing bean for {@code /Listings.jspx}.
 * 
 * @author Allan Lykke Christensen
 */
public class Listings {

    @EJB private ListingFacadeLocal listingFacade;

    private DataModel forexRates = null;

    private Rate selectedForexRate;

    private DataModel currencies = null;

    private Currency selectedCurrency;

    private DataModel marketValues = null;

    private MarketValue selectedMarketValue;

    private DataModel forecasts = null;

    private Forecast selectedForecast;

    private DataModel financialMarkets = null;

    private FinancialMarket selectedFinancialMarket;

    private DataModel locations = null;

    private Location selectedLocation;

    private DataModel situations = null;

    private Situation selectedSituation;

    public Listings() {
    }

    // -- FOREX RATES ----------------------------------------------------
    public DataModel getLatestForexRates() {
        return new ListDataModel(listingFacade.findLatestForexRates());
    }

    public DataModel getForexRates() {
        if (forexRates == null) {
            forexRates = new ListDataModel(listingFacade.findRates());
        }
        return forexRates;
    }

    public Rate getSelectedForexRate() {
        return selectedForexRate;
    }

    public void setSelectedForexRate(Rate selectedForexRate) {
        this.selectedForexRate = selectedForexRate;
    }

    public void onNewForexRate(ActionEvent event) {
        selectedForexRate = new Rate();
    }

    public void onAddForexRate(ActionEvent event) {
        selectedForexRate = listingFacade.createRate(selectedForexRate);
        forexRates = null;
    }

    public void onUpdateForexRate(ActionEvent event) {
        selectedForexRate = listingFacade.updateRate(selectedForexRate);
        forexRates = null;
    }

    public void onDeleteForexRate(ActionEvent event) {
        if (selectedForexRate != null) {
            listingFacade.deleteRate(selectedForexRate);
        }
        forexRates = null;
    }

    // -- CURRENCIES ----------------------------------------------------
    public DataModel getCurrencies() {
        if (currencies == null) {
            currencies = new ListDataModel(listingFacade.findCurrencies());
        }
        return currencies;
    }

    public Currency getSelectedCurrency() {
        return selectedCurrency;
    }

    public void setSelectedCurrency(Currency selectedCurrency) {
        this.selectedCurrency = selectedCurrency;
    }

    public void onNewCurrency(ActionEvent event) {
        selectedCurrency = new Currency();
    }

    public void onAddCurrency(ActionEvent event) {
        selectedCurrency = listingFacade.createCurrency(selectedCurrency);
        currencies = null;
    }

    public void onUpdateCurrency(ActionEvent event) {
        selectedCurrency = listingFacade.updateCurrency(selectedCurrency);
        currencies = null;
    }

    public void onDeleteCurrency(ActionEvent event) {
        if (selectedCurrency != null) {
            listingFacade.deleteCurrency(selectedCurrency);
        }
        currencies = null;
    }

    // -- FINANCIAL MARKETS --------------------------------------------
    public DataModel getLatestFinancialMarkets() {
        return new ListDataModel(listingFacade.findLatestMarketValues());
    }

    public DataModel getFinancialMarkets() {
        if (financialMarkets == null) {
            financialMarkets = new ListDataModel(listingFacade.findFinancialMarkets());
        }
        return financialMarkets;
    }

    public FinancialMarket getSelectedFinancialMarket() {
        return selectedFinancialMarket;
    }

    public void setSelectedFinancialMarket(FinancialMarket selectedFinancialMarket) {
        this.selectedFinancialMarket = selectedFinancialMarket;
    }

    public void onNewFinancialMarket(ActionEvent event) {
        selectedFinancialMarket = new FinancialMarket();
    }

    public void onAddFinancialMarket(ActionEvent event) {
        selectedFinancialMarket = listingFacade.createFinancialMarket(selectedFinancialMarket);
        financialMarkets = null;
    }

    public void onUpdateFinancialMarket(ActionEvent event) {
        selectedFinancialMarket = listingFacade.updateFinancialMarket(selectedFinancialMarket);
        financialMarkets = null;
    }

    public void onDeleteFinancialMarket(ActionEvent event) {
        if (selectedFinancialMarket != null) {
            listingFacade.deleteFinancialMarket(selectedFinancialMarket);
        }
        financialMarkets = null;
    }

    // -- MARKET VALUES --------------------------------------------
    public DataModel getMarketValues() {
        if (marketValues == null) {
            marketValues = new ListDataModel(listingFacade.findMarketValues());
        }
        return marketValues;
    }

    public MarketValue getSelectedMarketValue() {
        return selectedMarketValue;
    }

    public void setSelectedMarketValue(MarketValue selectedMarketValue) {
        this.selectedMarketValue = selectedMarketValue;
    }

    public void onNewMarketValue(ActionEvent event) {
        selectedMarketValue = new MarketValue();
    }

    public void onAddMarketValue(ActionEvent event) {
        selectedMarketValue = listingFacade.createMarketValue(selectedMarketValue);
        marketValues = null;
    }

    public void onUpdateMarketValue(ActionEvent event) {
        selectedMarketValue = listingFacade.updateMarketValue(selectedMarketValue);
        marketValues = null;
    }

    public void onDeleteMarketValue(ActionEvent event) {
        if (selectedMarketValue != null) {
            listingFacade.deleteMarketValue(selectedMarketValue);
        }
        marketValues = null;
    }

    // -- WEATHER FORECASTS --------------------------------------------
    public DataModel getLatestWeatherForecasts() {
        return new ListDataModel(listingFacade.findLatestForecasts());
    }

    public DataModel getForecasts() {
        if (forecasts == null) {
            forecasts = new ListDataModel(listingFacade.findForecasts());
        }
        return forecasts;
    }

    public Forecast getSelectedForecast() {
        return selectedForecast;
    }

    public void setSelectedForecast(Forecast selectedForecast) {
        this.selectedForecast = selectedForecast;
    }

    public void onNewForecast(ActionEvent event) {
        selectedForecast = new Forecast();
    }

    public void onAddForecast(ActionEvent event) {
        selectedForecast = listingFacade.createForecast(selectedForecast);
        forecasts = null;
    }

    public void onUpdateForecast(ActionEvent event) {
        selectedForecast = listingFacade.updateForecast(selectedForecast);
        forecasts = null;
    }

    public void onDeleteForecast(ActionEvent event) {
        if (selectedForecast != null) {
            listingFacade.deleteForecast(selectedForecast);
        }
        forecasts = null;
    }

    // -- WEATHER SITUATIONS -------------------------------------------
    public DataModel getSituations() {
        if (situations == null) {
            situations = new ListDataModel(listingFacade.findWeatherSituations());
        }
        return situations;
    }

    public Situation getSelectedSituation() {
        return selectedSituation;
    }

    public void setSelectedSituation(Situation selectedSituation) {
        this.selectedSituation = selectedSituation;
    }

    public void onNewSituation(ActionEvent event) {
        selectedSituation = new Situation();
    }

    public void onAddSituation(ActionEvent event) {
        selectedSituation = listingFacade.createWeatherSituation(selectedSituation);
        situations = null;
    }

    public void onUpdateSituation(ActionEvent event) {
        selectedSituation = listingFacade.updateWeatherSituation(selectedSituation);
        situations = null;
    }

    public void onDeleteSituation(ActionEvent event) {
        if (selectedSituation != null) {
            listingFacade.deleteWeatherSituation(selectedSituation);
        }
        situations = null;
    }

    // -- WEATHER LOCATIONS -------------------------------------------
    public DataModel getLocations() {
        if (locations == null) {
            locations = new ListDataModel(listingFacade.findWeatherLocations());
        }
        return locations;
    }

    public Location getSelectedLocation() {
        return selectedLocation;
    }

    public void setSelectedLocation(Location selectedLocation) {
        this.selectedLocation = selectedLocation;
    }

    public void onNewLocation(ActionEvent event) {
        selectedLocation = new Location();
    }

    public void onAddLocation(ActionEvent event) {
        selectedLocation = listingFacade.createWeatherLocation(selectedLocation);
        locations = null;
    }

    public void onUpdateLocation(ActionEvent event) {
        selectedLocation = listingFacade.updateWeatherLocation(selectedLocation);
        locations = null;
    }

    public void onDeleteLocation(ActionEvent event) {
        if (selectedLocation != null) {
            listingFacade.deleteWeatherLocation(selectedLocation);
        }
        locations = null;
    }
}
