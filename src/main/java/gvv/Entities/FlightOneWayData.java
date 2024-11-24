package gvv.Entities;

import org.openqa.selenium.WebElement;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FlightOneWayData {

    private String name;
    private LocalDateTime departureDate;
    private LocalDateTime destinationDate;
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


    @Override
    public String toString() {
        return "{" +
                "\"name\": \"" + name + "\"," +
                "\"departureDate\": \"" + (departureDate != null ? departureDate.toString() : null) + "\"," +
                "\"destinationDate\": \"" + (destinationDate != null ? destinationDate.toString() : null) + "\"," +
                "\"departureCity\": \"" + departureCity + "\"," +
                "\"destinationCity\": \"" + destinationCity + "\"," +
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

}
