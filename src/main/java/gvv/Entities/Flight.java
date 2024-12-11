package gvv.Entities;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Entity
@Table(name = "flight", schema = "pdi_flight")
public class Flight {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Integer id;

    @Column(name = "NAME", nullable = false, length = 400)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "AIRPLANE_FK", nullable = false)
    private Airplane airplaneFk;

    @Column(name = "DIRECTION_TYPE", nullable = false, length = 50)
    private String directionType;

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

    @ColumnDefault("'Y'")
    @Column(name = "IS_DIRECT", nullable = false, length = 1)
    private String isDirect;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "TRIP_FK", nullable = false)
    private Trip tripFk;

    public Trip getTripFk() {
        return tripFk;
    }

    public void setTripFk(Trip tripFk) {
        this.tripFk = tripFk;
    }

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

    public String getDirectionType() {
        return directionType;
    }

    public void setDirectionType(String directionType) {
        this.directionType = directionType;
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

    public String getIsDirect() {
        return isDirect;
    }

    public void setIsDirect(String isDirect) {
        this.isDirect = isDirect;
    }

}