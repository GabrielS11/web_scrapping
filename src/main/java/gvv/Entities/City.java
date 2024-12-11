package gvv.Entities;

import jakarta.persistence.*;

@Entity
@Table(name = "cities", schema = "pdi_flight")
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "COUNTRY_FK", nullable = false)
    private gvv.Entities.Country countryFk;

    @Column(name = "DESCRIPTION", nullable = false, length = 300)
    private String description;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public gvv.Entities.Country getCountryFk() {
        return countryFk;
    }

    public void setCountryFk(gvv.Entities.Country countryFk) {
        this.countryFk = countryFk;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}