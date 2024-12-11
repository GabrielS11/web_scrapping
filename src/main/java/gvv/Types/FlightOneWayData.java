package gvv.Types;

import gvv.Entities.*;
import gvv.WebScrappingOptimized.Utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class FlightOneWayData {

    private String name;
    private LocalDateTime departureDate;
    private String departureAirport;
    private LocalDateTime destinationDate;
    private String destinationAirport;
    private String departureCity;
    private String destinationCity;
    private String isDirect;

    private LocalDateTime retrievedDate = LocalDateTime.now();
    private String companyName;
    private Double discountPrice = null;
    private Double originalPrice;



    private FlightClass flightClass;
    private int adults = 1;

    private String airPlaneNumber;

    private List<FlightOneWayData> stops = new ArrayList<>();

    public FlightOneWayData() {
    }

    public FlightOneWayData addStop(FlightOneWayData stop) {
        stops.add(stop);
        return this;
    }

    public List<FlightOneWayData> getStops() {
        return stops;
    }

    public FlightOneWayData setStops(List<FlightOneWayData> stops) {
        this.stops = stops;
        return this;
    }

    public int getAdults() {
        return adults;
    }

    public FlightOneWayData setAdults(int adults) {
        this.adults = adults;
        return this;
    }

    public FlightClass getFlightClass() {
        return flightClass;
    }

    public FlightOneWayData setFlightClass(FlightClass flightClass) {
        this.flightClass = flightClass;
        return this;
    }

    public String getAirPlaneNumber() {
        return airPlaneNumber;
    }

    public FlightOneWayData setAirPlaneNumber(String airPlaneNumber) {
        this.airPlaneNumber = airPlaneNumber;
        return this;
    }

    public String getName() {
        return name;
    }

    public FlightOneWayData setName(String name) {
        this.name = name;
        return this;
    }

    public LocalDateTime getDepartureDate() {
        return departureDate;
    }

    public FlightOneWayData setDepartureDate(LocalDateTime departureDate) {
        this.departureDate = departureDate;
        return this;
    }

    public LocalDateTime getDestinationDate() {
        return destinationDate;
    }

    public FlightOneWayData setDestinationDate(LocalDateTime destinationDate) {
        this.destinationDate = destinationDate;
        return this;
    }

    public String getDepartureCity() {
        return departureCity;
    }

    public FlightOneWayData setDepartureCity(String departureCity) {
        this.departureCity = departureCity;
        return this;
    }

    public String getDestinationCity() {
        return destinationCity;
    }

    public FlightOneWayData setDestinationCity(String destinationCity) {
        this.destinationCity = destinationCity;
        return this;
    }

    public String getIsDirect() {
        return isDirect;
    }

    public FlightOneWayData setIsDirect(String isDirect) {
        this.isDirect = isDirect;
        return this;
    }

    public LocalDateTime getRetrievedDate() {
        return retrievedDate;
    }

    public FlightOneWayData setRetrievedDate(LocalDateTime retrievedDate) {
        this.retrievedDate = retrievedDate;
        return this;
    }

    public String getCompanyName() {
        return companyName;
    }

    public FlightOneWayData setCompanyName(String companyName) {
        this.companyName = companyName;
        return this;
    }

    public Double getDiscountPrice() {
        return discountPrice;
    }

    public FlightOneWayData setDiscountPrice(Double discountPrice) {
        this.discountPrice = discountPrice;
        return this;
    }

    public Double getOriginalPrice() {
        return originalPrice;
    }

    public FlightOneWayData setOriginalPrice(Double originalPrice) {
        this.originalPrice = originalPrice;
        return this;
    }

    public String getDepartureAirport() {
        return departureAirport;
    }

    public FlightOneWayData setDepartureAirport(String departureAirport) {
        this.departureAirport = departureAirport;
        return this;
    }

    public String getDestinationAirport() {
        return destinationAirport;
    }

    public FlightOneWayData setDestinationAirport(String destinationAirport) {
        this.destinationAirport = destinationAirport;
        return this;
    }



    @Override
    public String toString() {
        return "{" +
                "\"name\": \"" + name + "\"," +
                "\"departureDate\": \"" + (departureDate != null ? departureDate.toString() : null) + "\"," +
                "\"destinationDate\": \"" + (destinationDate != null ? destinationDate.toString() : null) + "\"," +
                "\"departureCity\": \"" + departureCity + "\"," +
                "\"destinationCity\": \"" + destinationCity + "\"," +
                "\"departureAirport\": \"" + departureAirport + "\"," +
                "\"destinationAirport\": \"" + destinationAirport + "\"," +
                "\"isDirect\": \"" + isDirect + "\"," +
                "\"retrievedDate\": \"" + (retrievedDate != null ? retrievedDate.toString() : null) + "\"," +
                "\"companyName\": \"" + companyName + "\"," +
                "\"discountPrice\": " + discountPrice + "," +
                "\"originalPrice\": " + originalPrice + "," +
                "\"flightClass\": \"" + (flightClass != null ? flightClass.toString() : null) + "\"," +
                "\"adults\": " + adults + "," +
                "\"airPlaneNumber\": \"" + airPlaneNumber + "\"," +
                "\"stops\": " + stopsToString() +
                "}";
    }

    private String stopsToString() {
        if (stops == null || stops.isEmpty()) {
            return "[]";
        }
        StringBuilder stopsJson = new StringBuilder("[");
        for (int i = 0; i < stops.size(); i++) {
            stopsJson.append(stops.get(i).toString());
            if (i < stops.size() - 1) {
                stopsJson.append(",");
            }
        }
        stopsJson.append("]");
        return stopsJson.toString();
    }

    public Flight asFlight() throws Exception {

        Flight flight = new Flight();

        flight.setIsDirect(isDirect);
        flight.setDepartureDate(this.departureDate.atZone(ZoneId.systemDefault()).toInstant());
        flight.setArrivalDate(this.departureDate.atZone(ZoneId.systemDefault()).toInstant());
        flight.setName(this.name);
        flight.setDirectionType("DEPARTURE");

        Company company = new Company();
        company.setName(this.companyName);
        Airplane airplane = new Airplane();
        airplane.setCompanyFk(company);
        airplane.setCode(this.airPlaneNumber);
        flight.setAirplaneFk(airplane);

        /*
         *
         *  DEPARTURE
         *
         * */

        if(Utils.getAirport(this.departureCity) == null){
            throw new Exception("Couldn't find airport for code (" + this.departureCity + ")");
        } else if (Utils.getAirport(this.destinationCity) == null) throw new Exception("Couldn't find airport for code (" + this.destinationCity + ")");

        Airport departureAirport = new Airport();
        departureAirport.setDescription(this.departureAirport);

        City departureCity = new City();

        departureCity.setDescription(Utils.getAirport(this.departureCity).getCity());

        Country departureCountry = new Country();
        departureCountry.setCode(Utils.getAirport(this.departureCity).getCountryCode());
        departureCountry.setDescription(Utils.getAirport(this.departureCity).getCountry());

        departureCity.setCountryFk(departureCountry);

        departureAirport.setDescription(Utils.getAirport(this.departureCity).getAirportName());
        departureAirport.setCityFk(departureCity);

        flight.setDepartureAirport(departureAirport);

        /*
        *
        *  ARRIVAL
        *
        * */
        Airport arrivalAirport = new Airport();
        arrivalAirport.setDescription(this.destinationAirport);

        City arrivalCity = new City();
        arrivalCity.setDescription(Utils.getAirport(this.destinationCity).getCity());

        Country arrivalCountry = new Country();
        arrivalCountry.setCode(Utils.getAirport(this.destinationCity).getCountryCode());
        arrivalCountry.setDescription(Utils.getAirport(this.destinationCity).getCountry());

        arrivalCity.setCountryFk(arrivalCountry);

        arrivalAirport.setDescription(Utils.getAirport(this.destinationCity).getAirportName());
        arrivalAirport.setCityFk(arrivalCity);

        flight.setArrivalAirport(arrivalAirport);

        return flight;
    }

    public FlightStop asFlightStop() throws Exception {
        FlightStop stop = new FlightStop();
        Flight flight = asFlight();

        stop.setAirplaneFk(flight.getAirplaneFk());
        stop.setArrivalAirport(flight.getArrivalAirport());
        stop.setDepartureAirport(flight.getDepartureAirport());
        stop.setName(flight.getName());
        stop.setArrivalDate(flight.getArrivalDate());
        stop.setDepartureDate(flight.getDepartureDate());
        return stop;
    }

    public Trip asTrip() throws Exception {
        Trip trip = new Trip();
        Flight flight = asFlight();
        trip.setOutwardAirport(flight.getArrivalAirport());
        trip.setFlightType("ONEWAY");
        trip.setFlightChoice(this.getFlightClass().name());
        trip.setOutwardDate(flight.getDepartureDate());
        trip.setRetrievedDate(this.retrievedDate.atZone(ZoneId.systemDefault()).toInstant());
        trip.setOutwardFlight(flight);
        return trip;
    }

}
