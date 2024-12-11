package gvv.Types;

public class AirportType {

    private String country;
    private String airportName;
    private String countryCode;
    private String city;
    private String state;

    public AirportType(){

    }

    public AirportType(String country, String airportName, String countryCode, String city, String state) {
        this.country = country;
        this.airportName = airportName;
        this.countryCode = countryCode;
        this.city = city;
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public AirportType setCountry(String country) {
        this.country = country;
        return this;
    }

    public String getAirportName() {
        return airportName;
    }

    public AirportType setAirportName(String airportName) {
        this.airportName = airportName;
        return this;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public AirportType setCountryCode(String countryCode) {
        this.countryCode = countryCode;
        return this;
    }

    public String getCity() {
        return city;
    }

    public AirportType setCity(String city) {
        this.city = city;
        return this;
    }

    public String getState() {
        return state;
    }

    public AirportType setState(String state) {
        this.state = state;
        return this;
    }
}
