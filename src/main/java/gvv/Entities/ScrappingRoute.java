package gvv.Entities;

import java.util.Date;

public class ScrappingRoute {


    /**
     * VARIABLES
     *  {{DEPARTURE_CITY_CODE}} - Código da Cidade de partida (Ex.: LIS [LISBOA])
     *  {{ARRIVAL_CITY_CODE}} - Código da Cidade de chegada (Ex.: OPO [PORTO])
     *  {{ADULTS_QUANTITY}} - Quantidade de adultos (Ex.: 1)
     *  {{FLIGHT_cLASS}} - Classe do voo (Ex.: ECONONOMY)
     *  {{DEPARTURE_DATE}} - Data de Partida do voo (Ex.: 2024-11-12)
     *  {{RETURN_DATE}} - Data de Retorno do voo (Ex.: 2024-11-13)
     *
     */

    private String departureCityCode;
    private String arrivalCityCode;
    private int adultsQuantity = 1;
    private FlightClass flightClass;
    private Date departureDate = null;
    private Date returnDate = null;

    public ScrappingRoute(){

    }

    public ScrappingRoute(String departureCityCode, String arrivalCityCode, int adultsQuantity, FlightClass flightClass, Date departureDate, Date returnDate) {
        this.departureCityCode = departureCityCode;
        this.arrivalCityCode = arrivalCityCode;
        this.adultsQuantity = adultsQuantity;
        this.flightClass = flightClass;
        this.departureDate = departureDate;
        this.returnDate = returnDate;
    }

    public String getDepartureCityCode() {
        return departureCityCode;
    }

    public ScrappingRoute setDepartureCityCode(String departureCityCode) {
        this.departureCityCode = departureCityCode;
        return this;
    }

    public String getArrivalCityCode() {
        return arrivalCityCode;
    }

    public ScrappingRoute setArrivalCityCode(String arrivalCityCode) {
        this.arrivalCityCode = arrivalCityCode;
        return this;
    }

    public int getAdultsQuantity() {
        return adultsQuantity;
    }

    public ScrappingRoute setAdultsQuantity(int adultsQuantity) {
        this.adultsQuantity = adultsQuantity;
        return this;
    }

    public FlightClass getFlightClass() {
        return flightClass;
    }

    public ScrappingRoute setFlightClass(FlightClass flightClass) {
        this.flightClass = flightClass;
        return this;
    }

    public Date getDepartureDate() {
        return departureDate;
    }

    public ScrappingRoute setDepartureDate(Date departureDate) {
        this.departureDate = departureDate;
        return this;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public ScrappingRoute setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
        return this;
    }
}
