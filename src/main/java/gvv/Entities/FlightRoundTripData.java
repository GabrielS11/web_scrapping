package gvv.Entities;

import org.openqa.selenium.WebElement;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FlightRoundTripData {

    private String departureCity;
    private String destinationCity;

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

    @Override
    public String toString() {
        return "{" +
                "\"departureCity\": \"" + departureCity + "\"," +
                "\"destinationCity\": \"" + destinationCity + "\"," +
                "\"flightOutward\": " + (flightOutward != null ? flightOutward.toString() : "null") + "," +
                "\"flightReturn\": " + (flightReturn != null ? flightReturn.toString() : "null") +
                "}";
    }

}
