package gvv.Repositories;

import gvv.Entities.Airplane;
import gvv.Entities.Company;

public class CompanyRepository extends BaseRepository<Company, Long> {

    public CompanyRepository() {
        super(Company.class);
    }

}