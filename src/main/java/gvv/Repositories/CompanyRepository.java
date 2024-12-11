package gvv.Repositories;

import gvv.Entities.Airplane;
import gvv.Entities.Company;

public class CompanyRepository extends BaseRepository<Company, Long> {

    public CompanyRepository() {
        super(Company.class);
        super.initializeCache("name");
    }

    public Company getOrCreate(Company entity) {
        if(entity == null) return null;
        return super.findOrCreate("name", entity.getName(), entity);
    }

}