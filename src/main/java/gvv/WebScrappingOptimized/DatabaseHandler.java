package gvv.WebScrappingOptimized;

import gvv.Entities.*;
import gvv.Repositories.*;
import gvv.Types.FlightOneWayData;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class DatabaseHandler {


    private static Connection connection;

    private static final AirplaneRepository airplaneRepository = new AirplaneRepository();
    private static final AirportRepository airportRepository = new AirportRepository();
    private static final CityRepository cityRepository = new CityRepository();
    private static final CompanyRepository companyRepository = new CompanyRepository();
    private static final CountryRepository countryRepository = new CountryRepository();
    private static final FlightRepository flightRepository = new FlightRepository();
    private static final FlightStopRepository flightStopRepository = new FlightStopRepository();
    private static final TripRepository tripRepository = new TripRepository();

    public static void processOneWay(List<FlightOneWayData> data){
        for(FlightOneWayData d : data){
            try {
                Trip trip = d.asTrip();

                Flight flight = processFlight(d);
                trip.setOutwardAirport(flight.getDepartureAirport());
                tripRepository.save(trip);

                flight.setTripFk(trip);
                flight.setName(flight.getDepartureAirport().getCityFk().getCountryFk().getDescription() + " - " + flight.getArrivalAirport().getCityFk().getCountryFk().getDescription());
                flight = flightRepository.save(flight);

                if(!d.getStops().isEmpty()){
                    for(FlightOneWayData stop: d.getStops()){
                        FlightStop stopFlight = processFlightStop(stop);
                        stopFlight.setFlightFk(flight);
                        stopFlight.setName(stopFlight.getDepartureAirport().getCityFk().getCountryFk().getDescription() + " - " + stopFlight.getArrivalAirport().getCityFk().getCountryFk().getDescription());
                        flightStopRepository.save(stopFlight);
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public static void processOneWay(FlightOneWayData data){
        processOneWay(List.of(data));
    }


    public static Flight processFlight(FlightOneWayData data) throws Exception {
        Flight flight = data.asFlight();


        // Departure
        Country departureCountry = countryRepository.save(flight.getDepartureAirport().getCityFk().getCountryFk());
        flight.getDepartureAirport().getCityFk().setCountryFk(departureCountry);

        City departureCity = cityRepository.save(flight.getDepartureAirport().getCityFk());
        flight.getDepartureAirport().setCityFk(departureCity);

        Airport departureAirport = airportRepository.save(flight.getDepartureAirport());

        flight.setDepartureAirport(departureAirport);


        // Arrival
        Country arrivalCountry = countryRepository.save(flight.getArrivalAirport().getCityFk().getCountryFk());
        flight.getArrivalAirport().getCityFk().setCountryFk(arrivalCountry);

        City arrivalCity = cityRepository.save(flight.getArrivalAirport().getCityFk());
        flight.getArrivalAirport().setCityFk(arrivalCity);

        Airport arrivalAirport = airportRepository.save(flight.getArrivalAirport());

        flight.setArrivalAirport(arrivalAirport);


        Company company = companyRepository.save(flight.getAirplaneFk().getCompanyFk());
        Airplane airplane = airplaneRepository.save(flight.getAirplaneFk());
        airplane.setCompanyFk(company);
        airplane.setCode(flight.getAirplaneFk().getCode());
        flight.setAirplaneFk(airplane);


        return flight;
    }

    public static FlightStop processFlightStop(FlightOneWayData data) throws Exception {
        FlightStop stop = data.asFlightStop();


        // Departure
        Country departureCountry = countryRepository.save(stop.getDepartureAirport().getCityFk().getCountryFk());
        stop.getDepartureAirport().getCityFk().setCountryFk(departureCountry);

        City departureCity = cityRepository.save(stop.getDepartureAirport().getCityFk());
        stop.getDepartureAirport().setCityFk(departureCity);

        Airport departureAirport = airportRepository.save(stop.getDepartureAirport());

        stop.setDepartureAirport(departureAirport);


        // Arrival
        Country arrivalCountry = countryRepository.save(stop.getArrivalAirport().getCityFk().getCountryFk());
        stop.getArrivalAirport().getCityFk().setCountryFk(arrivalCountry);

        City arrivalCity = cityRepository.save(stop.getArrivalAirport().getCityFk());
        stop.getArrivalAirport().setCityFk(arrivalCity);

        Airport arrivalAirport = airportRepository.save(stop.getArrivalAirport());

        stop.setArrivalAirport(arrivalAirport);


        Company company = companyRepository.save(stop.getAirplaneFk().getCompanyFk());
        Airplane airplane = airplaneRepository.save(stop.getAirplaneFk());
        airplane.setCompanyFk(company);
        airplane.setCode(stop.getAirplaneFk().getCode());
        stop.setAirplaneFk(airplane);


        return stop;
    }


    public static void connect(){
        String url = "jdbc:mysql://localhost:3306/PDI_flight";
        String user = "root";
        String password = ""; // If no password, leave it empty
        try {
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        return connection;

    }

    public static void close() {
        try {
            if(connection != null && !connection.isClosed()){
                connection.commit();
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
