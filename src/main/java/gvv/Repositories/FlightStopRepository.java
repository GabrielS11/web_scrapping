package gvv.Repositories;

import gvv.Entities.Airplane;
import gvv.Entities.FlightStop;

public class FlightStopRepository extends BaseRepository<FlightStop, Long> {

    public FlightStopRepository() {
        super(FlightStop.class);
    }

}