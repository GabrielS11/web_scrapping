package gvv.Repositories;

import gvv.Entities.Airplane;
import gvv.Entities.Airport;

public class AirportRepository extends BaseRepository<Airport, Long> {

    public AirportRepository() {
        super(Airport.class);
    }

}