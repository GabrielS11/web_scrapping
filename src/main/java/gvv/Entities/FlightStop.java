package gvv.Entities;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "flight_stops", schema = "pdi_flight")
public class FlightStop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Integer id;

    @Column(name = "NAME", nullable = false, length = 400)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "AIRPLANE_FK", nullable = false)
    private Airplane airplaneFk;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Airplane getAirplaneFk() {
        return airplaneFk;
    }

    public void setAirplaneFk(Airplane airplaneFk) {
        this.airplaneFk = airplaneFk;
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