package gvv;

public class WebScrapping {
    /**
     * VARIABLES
     *  {{DEPARTURE_CITY_CODE}} - Código da Cidade de partida (Ex.: LIS [LISBOA])
     *  {{ARRIVAL_CITY_CODE}} - Código da Cidade de chegada (Ex.: OPO [PORTO])
     *  {{ADULTS_QUANTITY}} - Quantidade de adultos (Ex.: 1)
     *  {{FLIGHT_cLASS}} - Classe do voo (Ex.: ECONONOMY)
     *  {{DEPARTURE_DATE}} - Data de Partida do voo (Ex.: 2024-11-12)
     *
     */
    private static final String ONE_WAY_URL = "https://flights.booking.com/flights/{{DEPARTURE_CITY_CODE}}-{{ARRIVAL_CITY_CODE}}?type=ONEWAY&from={{DEPARTURE_CITY_CODE}}&to={{ARRIVAL_CITY_CODE}}&cabinClass={{FLIGHT_CLASS}}&depart={{DEPARTURE_DATE}}&adults={{ADULTS_QUANTITY}}&page={{PAGE_NUMBER}}";

    /**
     * VARIABLES
     *  {{DEPARTURE_CITY_CODE}} - Código da Cidade de partida (Ex.: LIS [LISBOA])
     *  {{ARRIVAL_CITY_CODE}} - Código da Cidade de chegada (Ex.: OPO [PORTO])
     *  {{ADULTS_QUANTITY}} - Quantidade de adultos (Ex.: 1)
     *  {{FLIGHT_cLASS}} - Classe do voo (Ex.: ECONONOMY)
     *  {{DEPARTURE_DATE}} - Data de Partida do voo (Ex.: 2024-11-12)
     *  {{RETURN_DATE}} - Data de Retorno do voo (Ex.: 2024-11-13)
     *
     */
    private static final String ROUND_TRIP_URL = "https://flights.booking.com/flights/{{DEPARTURE_CITY_CODE}}-{{ARRIVAL_CITY_CODE}}/?type=ROUNDTRIP&adults={{ADULTS_QUANTITY}}&cabinClass={{FLIGHT_CLASS}}&from={{DEPARTURE_CITY_CODE}}&to={{ARRIVAL_CITY_CODE}}&depart={{DEPARTURE_DATE}}&return={{RETURN_DATE}}";


    /**
     * OBJETIVOS
     *  - Buscar os voos de hoje a 30 dias (amanha a 30 dias, quando executado amanhã)
     *      É NECESSÁRIO TER UMA LISTA DE DESTINOS PARA IDA E VOLTA E OS DE SÓ IDA
     *  - Buscar a atualização do preço à medida que a data de partida chega
     *      CONSULTAR TODOS OS VOOS NA BASE DE DADOS, ADQUIRIR O SEU URL, E VERIFICAR O PREÇO DOS BILHETES
     *      APENAS ATÉ OS VOOS DE HOJE A 29 DIAS.
     * */
    private String departureCity = "2024-11-20";
}
