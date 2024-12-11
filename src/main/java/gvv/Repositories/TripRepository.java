package gvv.Repositories;

import gvv.Entities.Airplane;
import gvv.Entities.Trip;

public class TripRepository extends BaseRepository<Trip, Long> {

    public TripRepository() {
        super(Trip.class);
    }

}