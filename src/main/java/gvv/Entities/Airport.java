package gvv.Entities;

import jakarta.persistence.*;

@Entity
@Table(name = "airports", schema = "pdi_flight")
public class Airport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CITY_FK", nullable = false)
    private gvv.Entities.City cityFk;

    @Column(name = "CODE", nullable = false, length = 80)
    private String code;

    @Column(name = "DESCRIPTION", nullable = false, length = 300)
    private String description;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public gvv.Entities.City getCityFk() {
        return cityFk;
    }

    public void setCityFk(gvv.Entities.City cityFk) {
        this.cityFk = cityFk;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}