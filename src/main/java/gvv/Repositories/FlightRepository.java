package gvv.Repositories;

import gvv.Entities.Airplane;
import gvv.Entities.Flight;

public class FlightRepository extends BaseRepository<Flight, Long> {

    public FlightRepository() {
        super(Flight.class);
    }

}