package gvv.Repositories;

import gvv.Entities.Airplane;
import gvv.Entities.Airport;
import gvv.Entities.City;

public class CityRepository extends BaseRepository<City, Long> {

    public CityRepository() {
        super(City.class);
        super.initializeCache("description");
    }

    public City getOrCreate(City entity) {
        if(entity == null) return null;
        return super.findOrCreate("description", entity.getDescription(), entity);
    }

}