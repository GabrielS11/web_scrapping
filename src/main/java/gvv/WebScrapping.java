package gvv;

import gvv.Entities.FlightOneWayData;
import gvv.Entities.FlightClass;
import gvv.Entities.FlightRoundTripData;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.text.DecimalFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class WebScrapping {
    /**
     * VARIABLES
     * {{DEPARTURE_CITY_CODE}} - Código da Cidade de partida (Ex.: LIS [LISBOA])
     * {{ARRIVAL_CITY_CODE}} - Código da Cidade de chegada (Ex.: OPO [PORTO])
     * {{ADULTS_QUANTITY}} - Quantidade de adultos (Ex.: 1)
     * {{FLIGHT_CLASS}} - Classe do voo (Ex.: ECONONOMY)
     * {{DEPARTURE_DATE}} - Data de Partida do voo (Ex.: 2024-11-12)
     * {{PAGE_NUMBER}} -
     */
    private static final String ONE_WAY_URL = "https://flights.booking.com/flights/{{DEPARTURE_CITY_CODE}}-{{ARRIVAL_CITY_CODE}}?type=ONEWAY&from={{DEPARTURE_CITY_CODE}}&to={{ARRIVAL_CITY_CODE}}&cabinClass={{FLIGHT_CLASS}}&depart={{DEPARTURE_DATE}}&adults={{ADULTS_QUANTITY}}&page={{PAGE_NUMBER}}";

    /**
     * VARIABLES
     * {{DEPARTURE_CITY_CODE}} - Código da Cidade de partida (Ex.: LIS [LISBOA])
     * {{ARRIVAL_CITY_CODE}} - Código da Cidade de chegada (Ex.: OPO [PORTO])
     * {{ADULTS_QUANTITY}} - Quantidade de adultos (Ex.: 1)
     * {{FLIGHT_cLASS}} - Classe do voo (Ex.: ECONONOMY)
     * {{DEPARTURE_DATE}} - Data de Partida do voo (Ex.: 2024-11-12)
     * {{RETURN_DATE}} - Data de Retorno do voo (Ex.: 2024-11-13)
     */
    private static final String ROUND_TRIP_URL = "https://flights.booking.com/flights/{{DEPARTURE_CITY_CODE}}-{{ARRIVAL_CITY_CODE}}/?type=ROUNDTRIP&adults={{ADULTS_QUANTITY}}&cabinClass={{FLIGHT_CLASS}}&from={{DEPARTURE_CITY_CODE}}&to={{ARRIVAL_CITY_CODE}}&depart={{DEPARTURE_DATE}}&return={{RETURN_DATE}}&page={{PAGE_NUMBER}}";


    /**
     * OBJETIVOS
     * - Buscar os voos de hoje a 30 dias (amanha a 30 dias, quando executado amanhã)
     * É NECESSÁRIO TER UMA LISTA DE DESTINOS PARA IDA E VOLTA E OS DE SÓ IDA
     * - Buscar a atualização do preço à medida que a data de partida chega
     * CONSULTAR TODOS OS VOOS NA BASE DE DADOS, ADQUIRIR O SEU URL, E VERIFICAR O PREÇO DOS BILHETES
     * APENAS ATÉ OS VOOS DE HOJE A 29 DIAS.
     */

    private static Map<String, List<String>> FLIGHTS = new HashMap<>() {{
        put("OPO", List.of("LIS"));
    }};

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");


    public static void main(String[] args) {
        startWebScrapping();
    }


    public static void startWebScrapping() {
        LocalDateTime currentDate = LocalDateTime.now().plusDays(30);
        LocalDateTime returnDate = LocalDateTime.now().plusDays(33);


        String departureCity = "OPO";
        String arrivalCity = "LIS";
        final int adultsQt = 1;
        webscrapRoundTrip(departureCity, arrivalCity, ROUND_TRIP_URL
                        .replace("{{DEPARTURE_CITY_CODE}}", departureCity)
                        .replace("{{ARRIVAL_CITY_CODE}}", arrivalCity)
                        .replace("{{ADULTS_QUANTITY}}", String.valueOf(adultsQt))
                        .replace("{{FLIGHT_CLASS}}", FlightClass.ECONOMY.name().toUpperCase())
                        .replace("{{DEPARTURE_DATE}}", currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                        .replace("{{RETURN_DATE}}", returnDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))

                , currentDate, returnDate
        );
        if(true) return;
        for (String departure : FLIGHTS.keySet()) {
            for (String destination : FLIGHTS.get(departure)) {
                for (FlightClass flightClass : FlightClass.values()) {

                    List<FlightOneWayData> data = webscrapOneWay(departure, destination,
                            ONE_WAY_URL
                                    .replace("{{DEPARTURE_CITY_CODE}}", departure)
                                    .replace("{{ARRIVAL_CITY_CODE}}", destination)
                                    .replace("{{ADULTS_QUANTITY}}", String.valueOf(adultsQt))
                                    .replace("{{FLIGHT_CLASS}}", flightClass.name().toUpperCase())
                                    .replace("{{DEPARTURE_DATE}}", currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))),
                            currentDate
                    ).stream().peek(flightData -> {
                        flightData.setFlightClass(flightClass);
                        flightData.setAdults(adultsQt);
                    }).toList();
                    System.out.println();
                }
            }
        }
    }

    public static ArrayList<FlightOneWayData> webscrapOneWay(String departure, String destination, String request_url, LocalDateTime date) {

        HashMap<Integer, ArrayList<FlightOneWayData>> flights = new HashMap<>();
        WebDriver driver = getWebDriver();

        // Adquirir páginas
        Integer totalPages = null;
        String url = request_url.replace("{{PAGE_NUMBER}}", "");
        try {
            System.out.println("Launching browser...");
            System.out.println("Navigating to: " + url);
            driver.get(url);
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));
            totalPages = Integer.parseInt(driver.findElement(By.xpath("(//li[contains(@class, 'Pagination-module__item___ZDS-g')])[last()]")).getText());
            try {
                WebElement cookieBanner = driver.findElement(By.id("onetrust-banner-sdk"));
                WebElement acceptCookiesButton = cookieBanner.findElement(By.xpath(".//button[contains(text(), 'Accept')]"));
                if (acceptCookiesButton.isDisplayed()) {
                    acceptCookiesButton.click();
                }
            } catch (NoSuchElementException | ElementNotInteractableException ignored) {
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }

        if(totalPages == null) return new ArrayList<>();



        // Iterar por cada página e adquirir os valores
        for(int page = 1; page <= totalPages; page++) {
            flights.put(page, new ArrayList<>());
            url = request_url.replace("{{PAGE_NUMBER}}", String.valueOf(page));
            driver.get(url);
            System.out.println("[");
            try {
                List<WebElement> flightElements = driver.findElements(By.xpath("//li[contains(@class, 'List-module__item___TMd8E List-module__item--spacing-medium___foMk1')]"));
                Integer currentPage = page;

                // Adquirir os valores a partir da página inicial
                flightElements.forEach(flightElement -> {
                    try {
                        FlightOneWayData flight = new FlightOneWayData();
                        flight.setDepartureCity(departure);
                        flight.setDestinationCity(destination);


                        WebElement departureDestinationElement = flightElement.findElement(By.xpath(".//div[contains(@class, 'FlightCardBound-desktop-module__segmentDetails___1zsby')]//div[contains(@class, 'Stack-module__root___ohshd Stack-module__root--direction-row___3r3Pe Stack-module__root--grow-false___eaLO-')]"));
                        String departureTime = departureDestinationElement.findElement(By.xpath(".//div[contains(@class, 'Frame-module__flex-direction_column___ms2of FlightCardSegment-desktop-module__timeDateBlock___0Voor') and contains(@style, 'text-align: left')]//div[@data-testid='flight_card_segment_departure_time_0']")).getText();
                        String departureDay = departureDestinationElement.findElement(By.xpath(".//div[contains(@class, 'Frame-module__flex-direction_column___ms2of FlightCardSegment-desktop-module__timeDateBlock___0Voor') and contains(@style, 'text-align: left')]//div[@data-testid='flight_card_segment_departure_date_0']")).getText();

                        String destinationTime = departureDestinationElement.findElement(By.xpath(".//div[contains(@class, 'Frame-module__flex-direction_column___ms2of FlightCardSegment-desktop-module__timeDateBlock___0Voor') and contains(@style, 'text-align: right')]//div[@data-testid='flight_card_segment_destination_time_0']")).getText();
                        String destinationDay = departureDestinationElement.findElement(By.xpath(".//div[contains(@class, 'Frame-module__flex-direction_column___ms2of FlightCardSegment-desktop-module__timeDateBlock___0Voor') and contains(@style, 'text-align: left')]//div[@data-testid='flight_card_segment_departure_date_0']")).getText();

                        flight.setDepartureDate(LocalDateTime.of(date.getYear(), getMonth(departureDay.split(" ")[1]), Integer.parseInt(departureDay.split(" ")[0]), Integer.parseInt(departureTime.split(":")[0]), Integer.parseInt(departureTime.split(":")[1])));
                        // verificar caso o mes seja menor q o mes de partida ent adicionar + 1 ano
                        flight.setDestinationDate(LocalDateTime.of(date.getYear(), getMonth(destinationDay.split(" ")[1]), Integer.parseInt(destinationDay.split(" ")[0]), Integer.parseInt(destinationTime.split(":")[0]), Integer.parseInt(destinationTime.split(":")[1])));

                        flight.setIsDirect(
                                departureDestinationElement.findElement(By.xpath(".//span[@data-testid='flight_card_segment_stops_0']//span[contains(@class, 'Badge-module__text___AGLG9')]")).getText().contains("Direct") ? "Y" : "N"
                        );

                        flight.setCompanyName(flightElement.findElement(By.xpath(".//div[@data-testid='flight_card_carrier_0']//div[contains(@class, 'Text-module__root--variant-small_1___An5P8')]")).getText());

                        String priceElement = flightElement.findElement(By.xpath(".//div[@data-testid='flight_card_price_main_price']")).getText().replace("€", "").replace(",", ".");
                        if (priceElement.contains("\n")) {
                            flight.setOriginalPrice(Double.parseDouble(priceElement.split("\n")[0]));
                            flight.setDiscountPrice(Double.parseDouble(priceElement.split("\n")[1]));
                        } else flight.setOriginalPrice(Double.parseDouble(priceElement));



                        flights.get(currentPage).add(flight);
                    } catch (Exception ex) {
                        System.err.println("Exception on iteration");
                        ex.printStackTrace();
                    }
                });



                // Abrir o modal e adquirir os dados extra necessários
                flights.get(currentPage).forEach(flight -> {
                    try {

                        String xpath = String.format(
                                "//li[contains(@class, 'List-module__item___TMd8E') and .//div[contains(text(), '%s')]]",
                                flight.getCompanyName()
                        );
                        WebElement updatedFlightElement = driver.findElement(By.xpath(xpath));
                        WebElement selectFlightButton = updatedFlightElement.findElement(By.xpath(".//button[@data-testid='flight_card_bound_select_flight']"));

                        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", selectFlightButton);
                        selectFlightButton.click();

                        List<WebElement> stopElements = driver.findElements(By.xpath("//div[contains(@class, 'TimelineSegment-module__legsWrapper___2VF5X')]//div[starts-with(@data-testid, 'timeline_leg_') and contains(@class, 'Frame-module__align-items_center___DCS7Y Frame-module__flex-direction_row___xHVKZ')]"));

                        for (int c = 1; c <= stopElements.size(); c++) {
                            if ("N".equalsIgnoreCase(flight.getIsDirect())) {
                                FlightOneWayData stop = new FlightOneWayData();

                                String departureCity = driver.findElement(By.xpath(String.format("(//div[contains(@class, 'TimelineSegment-module__legsWrapper___2VF5X')]//div[starts-with(@data-testid, 'timeline_leg_') and contains(@class, 'Frame-module__align-items_center___DCS7Y Frame-module__flex-direction_row___xHVKZ')])[%d]//div[contains(@class, 'Frame-module__padding-bottom_4___i8mtx Frame-module__padding-left_8___ZOqOO')]//div[@data-testid='timeline_location_airport_departure']", c))).getText().split(" ")[0];
                                String destinationCity = driver.findElement(By.xpath(String.format("(//div[contains(@class, 'TimelineSegment-module__legsWrapper___2VF5X')]//div[starts-with(@data-testid, 'timeline_leg_') and contains(@class, 'Frame-module__align-items_center___DCS7Y Frame-module__flex-direction_row___xHVKZ')])[%d]//div[contains(@class, 'Frame-module__padding-left_8___ZOqOO')]//div[@data-testid='timeline_location_airport_arrival']", c))).getText().split(" ")[0];

                                stop.setDepartureCity(departureCity);
                                stop.setDestinationCity(destinationCity);

                                String departureDateText = driver.findElement(By.xpath(String.format("(//div[contains(@class, 'TimelineSegment-module__legsWrapper___2VF5X')]//div[starts-with(@data-testid, 'timeline_leg_') and contains(@class, 'Frame-module__align-items_center___DCS7Y Frame-module__flex-direction_row___xHVKZ')])[%d]//div[@data-testid='timeline_location_timestamp_departure']", c))).getText();
                                String destinationDateText = driver.findElement(By.xpath(String.format("(//div[contains(@class, 'TimelineSegment-module__legsWrapper___2VF5X')]//div[starts-with(@data-testid, 'timeline_leg_') and contains(@class, 'Frame-module__align-items_center___DCS7Y Frame-module__flex-direction_row___xHVKZ')])[%d]//div[@data-testid='timeline_location_timestamp_arrival']", c))).getText();

                                stop.setDepartureDate(LocalDateTime.of(date.getYear(), getMonth(departureDateText.split(" ")[2]), Integer.parseInt(departureDateText.split(" ")[1]), Integer.parseInt(departureDateText.split(" ")[4].split(":")[0]), Integer.parseInt(departureDateText.split(" ")[4].split(":")[1])));
                                stop.setDestinationDate(LocalDateTime.of(date.getYear(), getMonth(destinationDateText.split(" ")[2]), Integer.parseInt(destinationDateText.split(" ")[1]), Integer.parseInt(destinationDateText.split(" ")[4].split(":")[0]), Integer.parseInt(destinationDateText.split(" ")[4].split(":")[1])));


                                stop.setCompanyName(driver.findElement(By.xpath(String.format("(//div[@data-testid='timeline_leg_info_carrier'])[%d]", c))).getText());


                                stop.setAirPlaneNumber(driver.findElement(By.xpath(String.format("(//div[@data-testid='timeline_leg_info_flight_number_and_class'])[%d]", c))).getText().split(" ")[0]);
                                flight.addStop(stop);
                            } else {
                                flight.setAirPlaneNumber(driver.findElement(By.xpath(String.format("(//div[@data-testid='timeline_leg_info_flight_number_and_class'])[%d]", c))).getText().split(" ")[0]);
                            }
                        }
                        WebElement closeButton = driver.findElement(By.xpath("//div[contains(@class, 'Overlay-module__content___+pCjC')]//button[@aria-label='Close']"));
                        closeButton.click();
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println();
                    }
                    System.out.printf("%s at %s until %s e o mesmo %s direto, com um preço de %s%s. Tem um total de %d escalas%s. %n",
                            flight.getCompanyName(),
                            flight.getDepartureDate().format(DATE_TIME_FORMATTER),
                            flight.getDestinationDate().format(DATE_TIME_FORMATTER),
                            "Y".equalsIgnoreCase(flight.getIsDirect()) ? "é" : "não é",
                            flight.getDiscountPrice() != null ? DECIMAL_FORMAT.format(flight.getDiscountPrice()) : DECIMAL_FORMAT.format(flight.getOriginalPrice()),
                            flight.getDiscountPrice() != null ? " (antes era " + DECIMAL_FORMAT.format(flight.getOriginalPrice()) + " €)" : "",
                            flight.getStops().size(),
                            !flight.getStops().isEmpty() ? " (" + String.join(", ", flight.getStops().stream().map(FlightOneWayData::getAirPlaneNumber).toList()) + ")" : " no avião " + flight.getAirPlaneNumber()
                    );
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        System.out.println("]");
        driver.close();
        return flights.values().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public static ArrayList<FlightRoundTripData> webscrapRoundTrip(String departure, String destination, String request_url, LocalDateTime date, LocalDateTime returnDate) {
        HashMap<Integer, ArrayList<FlightRoundTripData>> flights = new HashMap<>();
        WebDriver driver = getWebDriver();

        // Adquirir páginas
        Integer totalPages = null;
        String url = request_url.replace("{{PAGE_NUMBER}}", "");
        try {
            System.out.println("Launching browser...");
            System.out.println("Navigating to: " + url);
            driver.get(url);
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));

            totalPages = Integer.parseInt(driver.findElement(By.xpath("(//li[contains(@class, 'Pagination-module__item___ZDS-g')])[last()]")).getText());
            try {
                WebElement cookieBanner = driver.findElement(By.id("onetrust-banner-sdk"));
                WebElement acceptCookiesButton = cookieBanner.findElement(By.xpath(".//button[contains(text(), 'Accept')]"));
                if (acceptCookiesButton.isDisplayed()) {
                    acceptCookiesButton.click();
                }
            } catch (NoSuchElementException | ElementNotInteractableException ignored) {
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        if(totalPages == null) return new ArrayList<>();

        // Iterar por cada página e adquirir os valores
        for(int page = 8; page <= totalPages; page++) {
            flights.put(page, new ArrayList<>());
            url = request_url.replace("{{PAGE_NUMBER}}", String.valueOf(page));
            driver.get(url);

            try {
                List<WebElement> flightElements = driver.findElements(By.xpath("//li[contains(@class, 'List-module__item___TMd8E List-module__item--spacing-medium___foMk1')]"));
                Integer currentPage = page;
                // Adquirir os valores a partir da página inicial
                flightElements.forEach(flightElement -> {
                    try {
                        FlightRoundTripData trip = new FlightRoundTripData();
                        trip.setDepartureCity(departure);
                        trip.setDestinationCity(destination);
                        for(int segment = 1; segment <= 2; segment++) {
                            FlightOneWayData flight = new FlightOneWayData();


                            WebElement departureDestinationElement = flightElement.findElement(By.xpath(String.format("(.//div[contains(@class, 'FlightCardBound-desktop-module__segmentDetails___1zsby')]//div[contains(@class, 'Stack-module__root___ohshd Stack-module__root--direction-row___3r3Pe Stack-module__root--grow-false___eaLO-')])[%d]", segment)));
                                                                                                                                                                                                                                                                                       //div[starts-with(@data-testid, 'flight_card_segment_departure_time_')
                            String departureTime = departureDestinationElement.findElement(By.xpath(".//div[contains(@class, 'Frame-module__flex-direction_column___ms2of FlightCardSegment-desktop-module__timeDateBlock___0Voor') and contains(@style, 'text-align: left')]//div[starts-with(@data-testid, 'flight_card_segment_departure_time_')]")).getText();
                            String departureDay = departureDestinationElement.findElement(By.xpath(".//div[contains(@class, 'Frame-module__flex-direction_column___ms2of FlightCardSegment-desktop-module__timeDateBlock___0Voor') and contains(@style, 'text-align: left')]//div[starts-with(@data-testid, 'flight_card_segment_departure_date_')]")).getText();

                            String destinationTime = departureDestinationElement.findElement(By.xpath(".//div[contains(@class, 'Frame-module__flex-direction_column___ms2of FlightCardSegment-desktop-module__timeDateBlock___0Voor') and contains(@style, 'text-align: right')]//div[starts-with(@data-testid, 'flight_card_segment_destination_time_')]")).getText();
                            String destinationDay = departureDestinationElement.findElement(By.xpath(".//div[contains(@class, 'Frame-module__flex-direction_column___ms2of FlightCardSegment-desktop-module__timeDateBlock___0Voor') and contains(@style, 'text-align: left')]//div[starts-with(@data-testid, 'flight_card_segment_departure_date_')]")).getText();

                            flight.setDepartureDate(LocalDateTime.of(date.getYear(), getMonth(departureDay.split(" ")[1]), Integer.parseInt(departureDay.split(" ")[0]), Integer.parseInt(departureTime.split(":")[0]), Integer.parseInt(departureTime.split(":")[1])));
                            // verificar caso o mes seja menor q o mes de partida ent adicionar + 1 ano
                            flight.setDestinationDate(LocalDateTime.of(date.getYear(), getMonth(destinationDay.split(" ")[1]), Integer.parseInt(destinationDay.split(" ")[0]), Integer.parseInt(destinationTime.split(":")[0]), Integer.parseInt(destinationTime.split(":")[1])));

                            flight.setIsDirect(
                                    departureDestinationElement.findElement(By.xpath(".//span[starts-with(@data-testid, 'flight_card_segment_stops_')]//span[contains(@class, 'Badge-module__text___AGLG9')]")).getText().contains("Direct") ? "Y" : "N"
                            );

                            flight.setCompanyName(flightElement.findElement(By.xpath(".//div[starts-with(@data-testid, 'flight_card_carrier_')]//div[contains(@class, 'Text-module__root--variant-small_1___An5P8')]")).getText());

                            String priceElement = flightElement.findElement(By.xpath(".//div[@data-testid='flight_card_price_main_price']")).getText().replace("€", "").replace(",", ".");
                            if (priceElement.contains("\n")) {
                                flight.setOriginalPrice(Double.parseDouble(priceElement.split("\n")[0]));
                                flight.setDiscountPrice(Double.parseDouble(priceElement.split("\n")[1]));
                            } else flight.setOriginalPrice(Double.parseDouble(priceElement));



                            if(segment == 1){
                                trip.setFlightOutward(flight);
                            } else {
                                trip.setFlightReturn(flight);
                            }

                        }
                        flights.get(currentPage).add(trip);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        System.out.println();
                    }
                });

                AtomicInteger iteration = new AtomicInteger();
                flights.get(currentPage).forEach(trip -> {
                    try {
                        String xpath = String.format(
                                "//div[@id='flightcard-%d']",
                                iteration.get()
                        );
                        iteration.getAndIncrement();
                        WebElement updatedFlightElement = driver.findElement(By.xpath(xpath));
                        WebElement selectFlightButton = updatedFlightElement.findElement(By.xpath(".//button[@data-testid='flight_card_bound_select_flight']"));

                        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", selectFlightButton);
                        selectFlightButton.click();

                        for (int c = 0; c <= 1; c++) {

                            FlightOneWayData flight = c == 0 ? trip.getFlightOutward() : trip.getFlightReturn();

                            if ("N".equalsIgnoreCase(flight.getIsDirect())) {
                                int totalStops = driver.findElements(By.xpath(String.format("(//div[@data-testid='timeline_segment_%d']//div[contains(@class, 'TimelineSegment-module__legsWrapper___2VF5X')]//div[starts-with(@data-testid, 'timeline_leg_') and translate(substring-after(@data-testid, 'timeline_leg_'), '0123456789', '') = ''])", c))).size();
                                for(int stopPos = 0; stopPos < totalStops; stopPos++) {
                                    FlightOneWayData stop = new FlightOneWayData();

                                    String departureCity = driver.findElement(By.xpath(String.format("(//div[@data-testid='timeline_segment_%d']//div[contains(@class, 'TimelineSegment-module__legsWrapper___2VF5X')]//div[starts-with(@data-testid, 'timeline_leg_') and translate(substring-after(@data-testid, 'timeline_leg_'), '0123456789', '') = ''])[%d]", c, stopPos + 1))).getText().split("\n")[1].split(" ")[0];
                                    String destinationCity = driver.findElement(By.xpath(String.format("(//div[@data-testid='timeline_segment_%d']//div[contains(@class, 'TimelineSegment-module__legsWrapper___2VF5X')]//div[starts-with(@data-testid, 'timeline_leg_') and translate(substring-after(@data-testid, 'timeline_leg_'), '0123456789', '') = ''])[%d]", c, stopPos + 1))).getText().split("\n")[3].split(" ")[0];
                                    stop.setDepartureCity(departureCity);
                                    stop.setDepartureCity(destinationCity);


                                    String departureDateText = driver.findElement(By.xpath(String.format("(//div[@data-testid='timeline_segment_%d']//div[contains(@class, 'TimelineSegment-module__legsWrapper___2VF5X')]//div[starts-with(@data-testid, 'timeline_leg_') and translate(substring-after(@data-testid, 'timeline_leg_'), '0123456789', '') = ''])[%d]//div[@data-testid='timeline_location_timestamp_departure']", c, stopPos+1))).getText();
                                    String destinationDateText = driver.findElement(By.xpath(String.format("(//div[@data-testid='timeline_segment_%d']//div[contains(@class, 'TimelineSegment-module__legsWrapper___2VF5X')]//div[starts-with(@data-testid, 'timeline_leg_') and translate(substring-after(@data-testid, 'timeline_leg_'), '0123456789', '') = ''])[%d]//div[@data-testid='timeline_location_timestamp_arrival']", c, stopPos+1))).getText();

                                    stop.setDepartureDate(LocalDateTime.of(date.getYear(), getMonth(departureDateText.split(" ")[2]), Integer.parseInt(departureDateText.split(" ")[1]), Integer.parseInt(departureDateText.split(" ")[4].split(":")[0]), Integer.parseInt(departureDateText.split(" ")[4].split(":")[1])));
                                    stop.setDestinationDate(LocalDateTime.of(date.getYear(), getMonth(destinationDateText.split(" ")[2]), Integer.parseInt(destinationDateText.split(" ")[1]), Integer.parseInt(destinationDateText.split(" ")[4].split(":")[0]), Integer.parseInt(destinationDateText.split(" ")[4].split(":")[1])));

                                    stop.setCompanyName(driver.findElement(By.xpath(String.format("(//div[@data-testid='timeline_segment_%d']//div[contains(@class, 'TimelineSegment-module__legsWrapper___2VF5X')]//div[starts-with(@data-testid, 'timeline_leg_') and translate(substring-after(@data-testid, 'timeline_leg_'), '0123456789', '') = ''])[%d]//div[@data-testid='timeline_leg_info_carrier']", c, stopPos+1))).getText());

                                    stop.setAirPlaneNumber(driver.findElement(By.xpath(String.format("(//div[@data-testid='timeline_segment_%d']//div[contains(@class, 'TimelineSegment-module__legsWrapper___2VF5X')]//div[starts-with(@data-testid, 'timeline_leg_') and translate(substring-after(@data-testid, 'timeline_leg_'), '0123456789', '') = ''])[%d]//div[@data-testid='timeline_leg_info_flight_number_and_class']", c, stopPos+1))).getText().split(" ")[0]);


                                    flight.addStop(stop);
                                }

                                if(c == 0){
                                    trip.setFlightOutward(flight);
                                } else {
                                    trip.setFlightReturn(flight);
                                }
                            } else {
                                flight.setAirPlaneNumber(driver.findElements(By.xpath(String.format("(//div[@data-testid='timeline_segment_%d']//div[contains(@class, 'TimelineSegment-module__legsWrapper___2VF5X')]//div[starts-with(@data-testid, 'timeline_leg_') and translate(substring-after(@data-testid, 'timeline_leg_'), '0123456789', '') = ''])//div[@data-testid='timeline_leg_info_flight_number_and_class']", c, c+1))).getFirst().getText().split(" ")[0]);
                            }
                        }

                        WebElement closeButton = driver.findElement(By.xpath("//div[contains(@class, 'Overlay-module__content___+pCjC')]//button[@aria-label='Close']"));
                        closeButton.click();

                        System.out.println(trip);
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println();
                    }
                    /*System.out.printf("%s at %s until %s e o mesmo %s direto, com um preço de %s%s. Tem um total de %d escalas%s. %n",
                            flight.getCompanyName(),
                            flight.getDepartureDate().format(DATE_TIME_FORMATTER),
                            flight.getDestinationDate().format(DATE_TIME_FORMATTER),
                            "Y".equalsIgnoreCase(flight.getIsDirect()) ? "é" : "não é",
                            flight.getDiscountPrice() != null ? DECIMAL_FORMAT.format(flight.getDiscountPrice()) : DECIMAL_FORMAT.format(flight.getOriginalPrice()),
                            flight.getDiscountPrice() != null ? " (antes era " + DECIMAL_FORMAT.format(flight.getOriginalPrice()) + " €)" : "",
                            flight.getStops().size(),
                            !flight.getStops().isEmpty() ? " (" + String.join(", ", flight.getStops().stream().map(FlightOneWayData::getAirPlaneNumber).toList()) + ")" : " no avião " + flight.getAirPlaneNumber()
                    );*/
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
            } catch (Exception ex) {
                ex.printStackTrace();
                System.out.println();
            }
        }
        driver.close();
        return flights.values().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private static WebDriver getWebDriver() {
        System.setProperty("webdriver.chrome.driver", "D:\\Tools\\chromedriver-win64\\chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--incognito");
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36");
        return new ChromeDriver(options);
    }

    public static int getMonth(String month) {
        return switch (month) {
            case "Dec" -> 12;
            default -> -1;
        };
    }
}
