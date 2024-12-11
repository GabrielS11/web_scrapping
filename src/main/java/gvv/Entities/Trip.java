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

    @Column(name = "FLIGHT_CHOICE", nullable = false, length = 50)
    private String flightChoice;

    @Column(name = "OUTWARD_DATE", nullable = false)
    private Instant outwardDate;

    @Column(name = "RETURN_DATE", nullable = true)
    private Instant returnDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "OUTWARD_AIRPORT", nullable = false)
    private Airport outwardAirport;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RETURN_AIRPORT", nullable = true)
    private Airport returnAirport;

    @Transient
    private Flight outwardFlight; // ida

    @Transient
    private Flight returnFlight; // retorno

    public Flight getOutwardFlight() {
        return outwardFlight;
    }

    public Trip setOutwardFlight(Flight outwardFlight) {
        this.outwardFlight = outwardFlight;
        return this;
    }

    public Flight getReturnFlight() {
        return returnFlight;
    }

    public Trip setReturnFlight(Flight returnFlight) {
        this.returnFlight = returnFlight;
        return this;
    }

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

    public String getFlightChoice() {
        return flightChoice;
    }

    public void setFlightChoice(String flightChoice) {
        this.flightChoice = flightChoice;
    }

    public Instant getOutwardDate() {
        return outwardDate;
    }

    public void setOutwardDate(Instant outwardDate) {
        this.outwardDate = outwardDate;
    }

    public Instant getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Instant returnDate) {
        this.returnDate = returnDate;
    }

    public Airport getOutwardAirport() {
        return outwardAirport;
    }

    public void setOutwardAirport(Airport outwardAirport) {
        this.outwardAirport = outwardAirport;
    }

    public Airport getReturnAirport() {
        return returnAirport;
    }

    public void setReturnAirport(Airport returnAirport) {
        this.returnAirport = returnAirport;
    }

}