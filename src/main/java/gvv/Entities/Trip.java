package gvv.Entities;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Entity
@Table(name = "trip", schema = "pdi_flight")
public class Trip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Integer id;

    @Column(name = "FLIGHT_TYPE", nullable = false, length = 50)
    private String flightType;

    @ColumnDefault("current_timestamp()")
    @Column(name = "RETRIEVED_DATE", nullable = false)
    private Instant retrievedDate;

    @Column(name = "FLIGHT_CHOICE", nullable = false)
    private Integer flightChoice;

    @Column(name = "DEPARTURE_DATE", nullable = false)
    private Instant departureDate;

    @Column(name = "ARRIVAL_DATE", nullable = false)
    private Instant arrivalDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "DEPARTURE_AIRPORT", nullable = false)
    private Airport departureAirport;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ARRIVAL_AIRPORT", nullable = false)
    private Airport arrivalAirport;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFlightType() {
        return flightType;
    }

    public void setFlightType(String flightType) {
        this.flightType = flightType;
    }

    public Instant getRetrievedDate() {
        return retrievedDate;
    }

    public void setRetrievedDate(Instant retrievedDate) {
        this.retrievedDate = retrievedDate;
    }

    public Integer getFlightChoice() {
        return flightChoice;
    }

    public void setFlightChoice(Integer flightChoice) {
        this.flightChoice = flightChoice;
    }

    public Instant getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(Instant departureDate) {
        this.departureDate = departureDate;
    }

    public Instant getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(Instant arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    public Airport getDepartureAirport() {
        return departureAirport;
    }

    public void setDepartureAirport(Airport departureAirport) {
        this.departureAirport = departureAirport;
    }

    public Airport getArrivalAirport() {
        return arrivalAirport;
    }

    public void setArrivalAirport(Airport arrivalAirport) {
        this.arrivalAirport = arrivalAirport;
    }

}