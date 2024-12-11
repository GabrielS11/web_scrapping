package gvv.Repositories;

import gvv.Entities.Airplane;

public class AirplaneRepository extends BaseRepository<Airplane, Long> {

    public AirplaneRepository() {
        super(Airplane.class);
    }

}