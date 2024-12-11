package gvv.WebScrappingOptimized;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import gvv.Entities.Airport;
import gvv.Types.AirportType;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class Utils {

    private static Map<String, AirportType> AIRPORTS = new HashMap<>();

    public static Map<String, AirportType> getAirports() {
        if (!AIRPORTS.isEmpty()) {
            return AIRPORTS;
        }
        try {
            InputStream inputStream = Utils.class.getClassLoader().getResourceAsStream("airports.json");
            if (inputStream == null) {
                throw new IOException("File not found: airports.json");
            }
            ObjectMapper mapper = new ObjectMapper();
            AIRPORTS = mapper.readValue(inputStream, new TypeReference<>() {});
            return AIRPORTS;


        } catch (IOException ignored) {

        }
        return AIRPORTS;
    }


    public static AirportType getAirport(String airportCode) {
        return getAirports().get(airportCode);
    }
}
