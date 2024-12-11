package gvv.Repositories;

import gvv.Entities.Airplane;
import gvv.Entities.City;

public class CityRepository extends BaseRepository<City, Long> {

    public CityRepository() {
        super(City.class);
    }

}