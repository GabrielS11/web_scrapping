package gvv.WebScrappingOptimized;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AirportJsonConverter {
    public static void main(String[] args) {
        String sourceUrl = "https://raw.githubusercontent.com/mwgg/Airports/refs/heads/master/airports.json";
        String countryCodeUrl = "https://raw.githubusercontent.com/lukes/ISO-3166-Countries-with-Regional-Codes/refs/heads/master/all/all.json";
        String outputFilePath = "src/main/resources/airports.json";
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            Map<String, Map<String, Object>> airports = objectMapper.readValue(new URL(sourceUrl),
                    new TypeReference<>() {});

            List<Map<String, Object>> countryCodes = objectMapper.readValue(new URL(countryCodeUrl),
                    new TypeReference<>() {});

            Map<String, String> countryCodeToName = countryCodes.stream()
                    .collect(Collectors.toMap(
                            entry -> (String) entry.get("alpha-2"),
                            entry -> (String) entry.get("name")
                    ));

            ObjectNode transformedJson = objectMapper.createObjectNode();

            airports.forEach((icao, data) -> {
                String iata = (String) data.get("iata");
                String countryCode = (String) data.get("country");

                if (iata != null && !iata.isEmpty() && countryCode != null) {
                    ObjectNode newAirportData = objectMapper.createObjectNode();
                    String countryName = countryCodeToName.getOrDefault(countryCode, "Unknown Country");
                    newAirportData.put("country", countryName);
                    newAirportData.put("airportName",  (String) data.get("name"));
                    newAirportData.put("countryCode", (String) data.get("country"));
                    newAirportData.put("city", (String) data.get("city"));
                    newAirportData.put("state", (String) data.get("state"));
                    transformedJson.set(iata, newAirportData);
                }
            });

            File outputFile = Paths.get(outputFilePath).toFile();
            outputFile.getParentFile().mkdirs();
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(outputFile, transformedJson);

            System.out.println("Transformed JSON has been written to: " + outputFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Erro ao processar o JSON: " + e.getMessage());
        }
    }
}
