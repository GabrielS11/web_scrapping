package gvv.Repositories;

import gvv.Entities.Airplane;

public class AirplaneRepository extends BaseRepository<Airplane, Long> {



    public AirplaneRepository() {
        super(Airplane.class);
        super.initializeCache("code");
    }

    public Airplane getOrCreate(Airplane entity) {
        if(entity == null) return null;
        return super.findOrCreate("code", entity.getCode(), entity);
    }

}