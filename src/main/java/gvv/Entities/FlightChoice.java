package gvv.Entities;

import jakarta.persistence.*;

@Entity
@Table(name = "flight_choices", schema = "pdi_flight")
public class FlightChoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Integer id;

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