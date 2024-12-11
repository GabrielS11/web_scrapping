package gvv.Repositories;

import gvv.Entities.Country;

public class CountryRepository extends BaseRepository<Country, Long> {

    public CountryRepository() {
        super(Country.class);
        super.initializeCache("code");
    }

    public Country getOrCreate(Country entity) {
        if(entity == null) return null;
        return super.findOrCreate("code", entity.getCode(), entity);
    }

}