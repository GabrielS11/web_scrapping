package gvv.Types;

import gvv.Entities.Flight;
import gvv.Entities.Trip;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class FlightRoundTripData {

    private String departureCity;
    private String departureAirport;
    private String destinationCity;
    private String destinationAirport;
    private LocalDateTime departureDate;
    private LocalDateTime returnDate;

    private LocalDateTime retrievedDate;

    private FlightOneWayData flightOutward;
    private FlightOneWayData flightReturn;

    public String getDepartureCity() {
        return departureCity;
    }

    public FlightRoundTripData setDepartureCity(String departureCity) {
        this.departureCity = departureCity;
        return this;
    }

    public String getDestinationCity() {
        return destinationCity;
    }

    public FlightRoundTripData setDestinationCity(String destinationCity) {
        this.destinationCity = destinationCity;
        return this;
    }

    public LocalDateTime getRetrievedDate() {
        return retrievedDate;
    }

    public FlightRoundTripData setRetrievedDate(LocalDateTime retrievedDate) {
        this.retrievedDate = retrievedDate;
        return this;
    }

    public FlightOneWayData getFlightOutward() {
        return flightOutward;
    }

    public FlightRoundTripData setFlightOutward(FlightOneWayData flightOutward) {
        this.flightOutward = flightOutward;
        return this;
    }

    public FlightOneWayData getFlightReturn() {
        return flightReturn;
    }

    public FlightRoundTripData setFlightReturn(FlightOneWayData flightReturn) {
        this.flightReturn = flightReturn;
        return this;
    }

    public LocalDateTime getDepartureDate() {
        return departureDate;
    }

    public FlightRoundTripData setDepartureDate(LocalDateTime departureDate) {
        this.departureDate = departureDate;
        return this;
    }

    public LocalDateTime getReturnDate() {
        return returnDate;
    }

    public FlightRoundTripData setReturnDate(LocalDateTime returnDate) {
        this.returnDate = returnDate;
        return this;
    }

    public String getDepartureAirport() {
        return departureAirport;
    }

    public FlightRoundTripData setDepartureAirport(String departureAirport) {
        this.departureAirport = departureAirport;
        return this;
    }

    public String getDestinationAirport() {
        return destinationAirport;
    }

    public FlightRoundTripData setDestinationAirport(String destinationAirport) {
        this.destinationAirport = destinationAirport;
        return this;
    }

    @Override
    public String toString() {
        return "{" +
                "\"departureCity\": \"" + departureCity + "\"," +
                "\"destinationCity\": \"" + destinationCity + "\"," +
                "\"departureDate\": \"" + (departureDate != null ? departureDate.toString() : null) + "\"," +
                "\"returnDate\": \"" + (returnDate != null ? returnDate.toString() : null) + "\"," +
                "\"flightOutward\": " + (flightOutward != null ? flightOutward.toString() : "null") + "," +
                "\"flightReturn\": " + (flightReturn != null ? flightReturn.toString() : "null") +
                "}";
    }


    public Trip asTrip() throws Exception {
        Trip trip = new Trip();
        Flight outwardFlight = this.flightOutward.asFlight();
        Flight returnFlight = this.flightReturn.asFlight();


        trip.setOutwardAirport(outwardFlight.getDepartureAirport());
        trip.setReturnAirport(returnFlight.getDepartureAirport());

        trip.setRetrievedDate(this.retrievedDate.atZone(ZoneId.systemDefault()).toInstant());

        trip.setFlightType("ROUNDTRIP");


        trip.setFlightChoice(this.flightOutward.asTrip().getFlightChoice());
        trip.setOutwardDate(departureDate.atZone(ZoneId.systemDefault()).toInstant());
        trip.setReturnDate(returnDate.atZone(ZoneId.systemDefault()).toInstant());

        trip.setOutwardFlight(outwardFlight);
        trip.setReturnFlight(returnFlight);

        return trip;
    }

}
