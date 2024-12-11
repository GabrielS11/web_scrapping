package gvv.Repositories;

import gvv.Entities.Airplane;
import gvv.Entities.Country;

public class CountryRepository extends BaseRepository<Country, Long> {

    public CountryRepository() {
        super(Country.class);
    }

}