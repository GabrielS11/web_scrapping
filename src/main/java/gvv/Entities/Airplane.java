package gvv.Entities;

import jakarta.persistence.*;

@Entity
@Table(name = "airplanes", schema = "pdi_flight")
public class Airplane {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Integer id;

    @Column(name = "CODE", nullable = false, length = 400)
    private String code;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "COMPANY_FK", nullable = false)
    private gvv.Entities.Company companyFk;

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

    public gvv.Entities.Company getCompanyFk() {
        return companyFk;
    }

    public void setCompanyFk(gvv.Entities.Company companyFk) {
        this.companyFk = companyFk;
    }

}