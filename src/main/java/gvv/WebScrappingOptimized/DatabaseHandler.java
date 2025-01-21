package gvv.WebScrappingOptimized;

import gvv.Entities.*;
import gvv.Repositories.*;
import gvv.Types.FlightOneWayData;
import gvv.Types.FlightRoundTripData;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.List;

public class DatabaseHandler {

    private static final EntityManagerFactory entityManagerFactory =
            Persistence.createEntityManagerFactory("default");

    private static final ThreadLocal<EntityManager> threadLocalEntityManager = ThreadLocal.withInitial(entityManagerFactory::createEntityManager);

    public static void processOneWay(List<FlightOneWayData> data) {
        AirplaneRepository airplaneRepository = new AirplaneRepository();
        AirportRepository airportRepository = new AirportRepository();
        CityRepository cityRepository = new CityRepository();
        CompanyRepository companyRepository = new CompanyRepository();
        CountryRepository countryRepository = new CountryRepository();
        FlightRepository flightRepository = new FlightRepository();
        FlightStopRepository flightStopRepository = new FlightStopRepository();
        TripRepository tripRepository = new TripRepository();

        try {
            for (FlightOneWayData d : data) {
                try {
                    Trip trip = d.asTrip();


                    Flight flight = processFlight(d, airplaneRepository, airportRepository, cityRepository, companyRepository, countryRepository);
                    trip.setOutwardAirport(flight.getDepartureAirport());
                    tripRepository.save(trip);

                    flight.setTripFk(trip);
                    flight.setName(flight.getDepartureAirport().getCityFk().getCountryFk().getDescription() +
                            " - " + flight.getArrivalAirport().getCityFk().getCountryFk().getDescription());
                    flight = flightRepository.save(flight);


                    if (!d.getStops().isEmpty()) {
                        for (FlightOneWayData stop : d.getStops()) {
                            FlightStop stopFlight = processFlightStop(stop, airplaneRepository, airportRepository, cityRepository, companyRepository, countryRepository);
                            stopFlight.setFlightFk(flight);
                            stopFlight.setName(stopFlight.getDepartureAirport().getCityFk().getCountryFk().getDescription() +
                                    " - " + stopFlight.getArrivalAirport().getCityFk().getCountryFk().getDescription());
                            flightStopRepository.save(stopFlight);
                        }
                    }
                } catch (Exception ignored) {

                }
            }
        } finally {
            DatabaseHandler.closeEntityManager();
        }
    }

    public static void processRoundTrip(List<FlightRoundTripData> data) {
        AirplaneRepository airplaneRepository = new AirplaneRepository();
        AirportRepository airportRepository = new AirportRepository();
        CityRepository cityRepository = new CityRepository();
        CompanyRepository companyRepository = new CompanyRepository();
        CountryRepository countryRepository = new CountryRepository();
        FlightRepository flightRepository = new FlightRepository();
        FlightStopRepository flightStopRepository = new FlightStopRepository();
        TripRepository tripRepository = new TripRepository();
        try {
        for (FlightRoundTripData d : data) {
            try {
                Trip trip = d.asTrip();


                Flight outwardFlight = processFlight(d.getFlightOutward(), airplaneRepository, airportRepository, cityRepository, companyRepository, countryRepository);
                Flight returnFlight = processFlight(d.getFlightReturn(), airplaneRepository, airportRepository, cityRepository, companyRepository, countryRepository);
                trip.setOutwardAirport(outwardFlight.getDepartureAirport());
                trip.setReturnAirport(returnFlight.getDepartureAirport());
                tripRepository.save(trip);

                outwardFlight.setTripFk(trip);
                outwardFlight.setName(outwardFlight.getDepartureAirport().getCityFk().getCountryFk().getDescription() +
                        " - " + outwardFlight.getArrivalAirport().getCityFk().getCountryFk().getDescription());
                outwardFlight = flightRepository.save(outwardFlight);


                returnFlight.setTripFk(trip);
                returnFlight.setName(returnFlight.getDepartureAirport().getCityFk().getCountryFk().getDescription() +
                        " - " + returnFlight.getArrivalAirport().getCityFk().getCountryFk().getDescription());
                returnFlight = flightRepository.save(returnFlight);


                if (!d.getFlightOutward().getStops().isEmpty()) {
                    for (FlightOneWayData stop : d.getFlightOutward().getStops()) {
                        FlightStop stopFlight = processFlightStop(stop, airplaneRepository, airportRepository, cityRepository, companyRepository, countryRepository);
                        stopFlight.setFlightFk(outwardFlight);
                        stopFlight.setName(stopFlight.getDepartureAirport().getCityFk().getCountryFk().getDescription() +
                                " - " + stopFlight.getArrivalAirport().getCityFk().getCountryFk().getDescription());
                        flightStopRepository.save(stopFlight);
                    }
                }


                if (!d.getFlightReturn().getStops().isEmpty()) {
                    for (FlightOneWayData stop : d.getFlightReturn().getStops()) {
                        FlightStop stopFlight = processFlightStop(stop, airplaneRepository, airportRepository, cityRepository, companyRepository, countryRepository);
                        stopFlight.setFlightFk(returnFlight);
                        stopFlight.setName(stopFlight.getDepartureAirport().getCityFk().getCountryFk().getDescription() +
                                " - " + stopFlight.getArrivalAirport().getCityFk().getCountryFk().getDescription());
                        flightStopRepository.save(stopFlight);
                    }
                }

            } catch (Exception ignored) {

            }
        }
        } finally {
            DatabaseHandler.closeEntityManager();
        }
    }

    public static Flight processFlight(
            FlightOneWayData data,
            AirplaneRepository airplaneRepository,
            AirportRepository airportRepository,
            CityRepository cityRepository,
            CompanyRepository companyRepository,
            CountryRepository countryRepository
    ) throws Exception {
        Flight flight = data.asFlight();


        Country departureCountry = countryRepository.getOrCreate(flight.getDepartureAirport().getCityFk().getCountryFk());
        flight.getDepartureAirport().getCityFk().setCountryFk(departureCountry);

        City departureCity = cityRepository.getOrCreate(flight.getDepartureAirport().getCityFk());
        flight.getDepartureAirport().setCityFk(departureCity);

        Airport departureAirport = airportRepository.getOrCreate(flight.getDepartureAirport());
        flight.setDepartureAirport(departureAirport);


        Country arrivalCountry = countryRepository.getOrCreate(flight.getArrivalAirport().getCityFk().getCountryFk());
        flight.getArrivalAirport().getCityFk().setCountryFk(arrivalCountry);

        City arrivalCity = cityRepository.getOrCreate(flight.getArrivalAirport().getCityFk());
        flight.getArrivalAirport().setCityFk(arrivalCity);

        Airport arrivalAirport = airportRepository.getOrCreate(flight.getArrivalAirport());
        flight.setArrivalAirport(arrivalAirport);


        Company company = companyRepository.getOrCreate(flight.getAirplaneFk().getCompanyFk());
        flight.getAirplaneFk().setCompanyFk(company);
        Airplane airplane = airplaneRepository.getOrCreate(flight.getAirplaneFk());
        flight.setAirplaneFk(airplane);

        return flight;
    }

    public static FlightStop processFlightStop(
            FlightOneWayData data,
            AirplaneRepository airplaneRepository,
            AirportRepository airportRepository,
            CityRepository cityRepository,
            CompanyRepository companyRepository,
            CountryRepository countryRepository
    ) throws Exception {
        FlightStop stop = data.asFlightStop();


        Country departureCountry = countryRepository.getOrCreate(stop.getDepartureAirport().getCityFk().getCountryFk());
        stop.getDepartureAirport().getCityFk().setCountryFk(departureCountry);

        City departureCity = cityRepository.getOrCreate(stop.getDepartureAirport().getCityFk());
        stop.getDepartureAirport().setCityFk(departureCity);

        Airport departureAirport = airportRepository.getOrCreate(stop.getDepartureAirport());
        stop.setDepartureAirport(departureAirport);

        Country arrivalCountry = countryRepository.getOrCreate(stop.getArrivalAirport().getCityFk().getCountryFk());
        stop.getArrivalAirport().getCityFk().setCountryFk(arrivalCountry);

        City arrivalCity = cityRepository.getOrCreate(stop.getArrivalAirport().getCityFk());
        stop.getArrivalAirport().setCityFk(arrivalCity);

        Airport arrivalAirport = airportRepository.getOrCreate(stop.getArrivalAirport());
        stop.setArrivalAirport(arrivalAirport);


        Company company = companyRepository.getOrCreate(stop.getAirplaneFk().getCompanyFk());
        stop.getAirplaneFk().setCompanyFk(company);
        Airplane airplane = airplaneRepository.getOrCreate(stop.getAirplaneFk());
        stop.setAirplaneFk(airplane);

        return stop;
    }



    public static EntityManager getEntityManager() {
        return threadLocalEntityManager.get();
    }

    public static void closeEntityManager() {
        EntityManager em = threadLocalEntityManager.get();
        if (em != null && em.isOpen()) {
            em.close();
        }
        threadLocalEntityManager.remove();
    }
}
