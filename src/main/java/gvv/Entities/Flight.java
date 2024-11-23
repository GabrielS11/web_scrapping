package gvv.Entities;

import org.openqa.selenium.WebElement;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Flight {
    private int id;
    private String name;
    private LocalDateTime departureDate;
    private LocalDateTime destinationDate;
    private String departureCity;
    private String destinationCity;
    private String isDirect;
    private LocalDateTime retrievedDate = LocalDateTime.now();
    private String companyName;
    private double price;
    private WebElement webElement;

    private List<Flight> stops = new ArrayList<>();

    public Flight() {
    }

    public WebElement getWebElement() {
        return webElement;
    }

    public Flight setWebElement(WebElement webElement) {
        this.webElement = webElement;
        return this;
    }

    public double getPrice() {
        return price;
    }

    public Flight setPrice(double price) {
        this.price = price;
        return this;
    }

    public int getId() {
        return id;
    }

    public Flight setId(int id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Flight setName(String name) {
        this.name = name;
        return this;
    }

    public LocalDateTime getDepartureDate() {
        return departureDate;
    }

    public Flight setDepartureDate(LocalDateTime departureDate) {
        this.departureDate = departureDate;
        return this;
    }

    public LocalDateTime getDestinationDate() {
        return destinationDate;
    }

    public Flight setDestinationDate(LocalDateTime destinationDate) {
        this.destinationDate = destinationDate;
        return this;
    }

    public String getDepartureCity() {
        return departureCity;
    }

    public Flight setDepartureCity(String departureCity) {
        this.departureCity = departureCity;
        return this;
    }

    public String getDestinationCity() {
        return destinationCity;
    }

    public Flight setDestinationCity(String destinationCity) {
        this.destinationCity = destinationCity;
        return this;
    }

    public String getIsDirect() {
        return isDirect;
    }

    public Flight setIsDirect(String isDirect) {
        this.isDirect = isDirect;
        return this;
    }

    public LocalDateTime getRetrievedDate() {
        return retrievedDate;
    }

    public Flight setRetrievedDate(LocalDateTime retrievedDate) {
        this.retrievedDate = retrievedDate;
        return this;
    }

    public String getCompanyName() {
        return companyName;
    }

    public Flight setCompanyName(String companyName) {
        this.companyName = companyName;
        return this;
    }

    public Flight addStop(Flight stop) {
        stops.add(stop);
        return this;
    }

    public List<Flight> getStops() {
        return stops;
    }

    public Flight setStops(List<Flight> stops) {
        this.stops = stops;
        return this;
    }
}
