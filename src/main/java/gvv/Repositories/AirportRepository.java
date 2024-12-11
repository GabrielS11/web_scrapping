package gvv.Repositories;

import gvv.Entities.Airport;

public class AirportRepository extends BaseRepository<Airport, Long> {

    public AirportRepository() {
        super(Airport.class);
        super.initializeCache("description");
    }

    public Airport getOrCreate(Airport entity) {
        if(entity == null) return null;
        return super.findOrCreate("description", entity.getDescription(), entity);
    }



}