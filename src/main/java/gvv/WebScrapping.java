package gvv;

import gvv.Entities.Flight;
import gvv.Entities.FlightClass;
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

public class WebScrapping {
    /**
     * VARIABLES
     *  {{DEPARTURE_CITY_CODE}} - Código da Cidade de partida (Ex.: LIS [LISBOA])
     *  {{ARRIVAL_CITY_CODE}} - Código da Cidade de chegada (Ex.: OPO [PORTO])
     *  {{ADULTS_QUANTITY}} - Quantidade de adultos (Ex.: 1)
     *  {{FLIGHT_CLASS}} - Classe do voo (Ex.: ECONONOMY)
     *  {{DEPARTURE_DATE}} - Data de Partida do voo (Ex.: 2024-11-12)
     *  {{PAGE_NUMBER}} -
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

    private static Map<String, List<String>> FLIGHTS = new HashMap<>(){{
       put("OPO", List.of("LIS"));
    }};

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");


    private static final String START_TIME = "2024-11-20";

    public static void main(String[] args) {
        startWebScrapping();
    }


    public static void startWebScrapping() {
        for(String departure : FLIGHTS.keySet()) {
            for(String arrival : FLIGHTS.get(departure)) {
                webscrapOneWay(departure, arrival);
            }

        }

        webscrapRoundTrip();
    }
    
    

    public static void webscrapOneWay(String departure, String destination) {
        LocalDateTime currentDate = LocalDateTime.now().plusDays(30);
        String ONE_WAY_DAILY_URL = ONE_WAY_URL
                .replace("{{DEPARTURE_CITY_CODE}}", departure)
                .replace("{{ARRIVAL_CITY_CODE}}", destination)
                .replace("{{ADULTS_QUANTITY}}", "1")
                .replace("{{FLIGHT_CLASS}}", FlightClass.ECONOMY.name().toUpperCase())
                .replace("{{DEPARTURE_DATE}}", currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                ;
        String url = ONE_WAY_DAILY_URL.replace("{{PAGE_NUMBER}}", "1");
        ArrayList<Flight> flights = new ArrayList<>();

        WebDriver driver = getWebDriver();
        try {

            System.out.println("Launching browser...");
            System.out.println("Navigating to: " + url);
            driver.get(url);
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));
            List<WebElement> paginationItems = driver.findElements(By.xpath("//li[contains(@class, 'Pagination-module__item___ZDS-g')]"));

            int totalPages = paginationItems.size();

            List<WebElement> flightElements = driver.findElements(By.xpath("//li[contains(@class, 'List-module__item___TMd8E List-module__item--spacing-medium___foMk1')]"));
            AtomicInteger i = new AtomicInteger();
            flightElements.forEach(flightElement -> {
                try {
                    System.out.println("Iteration n.º " + (i.incrementAndGet()));
                    Flight flight = new Flight();
                    flight.setDepartureCity(departure);
                    flight.setDestinationCity(destination);

                    flight.setCompanyName(flightElement.findElement(By.xpath(".//div[@data-testid='flight_card_carrier_0']//div[contains(@class, 'Text-module__root--variant-small_1___An5P8')]")).getText());

                    WebElement departureDestinationElement = flightElement.findElement(By.xpath(".//div[contains(@class, 'FlightCardBound-desktop-module__segmentDetails___1zsby')]//div[contains(@class, 'Stack-module__root___ohshd Stack-module__root--direction-row___3r3Pe Stack-module__root--grow-false___eaLO-')]"));
                    String departureTime = departureDestinationElement.findElement(By.xpath(".//div[contains(@class, 'Frame-module__flex-direction_column___ms2of FlightCardSegment-desktop-module__timeDateBlock___0Voor') and contains(@style, 'text-align: left')]//div[@data-testid='flight_card_segment_departure_time_0']")).getText();
                    String departureDay = departureDestinationElement.findElement(By.xpath(".//div[contains(@class, 'Frame-module__flex-direction_column___ms2of FlightCardSegment-desktop-module__timeDateBlock___0Voor') and contains(@style, 'text-align: left')]//div[@data-testid='flight_card_segment_departure_date_0']")).getText();

                    String destinationTime = departureDestinationElement.findElement(By.xpath(".//div[contains(@class, 'Frame-module__flex-direction_column___ms2of FlightCardSegment-desktop-module__timeDateBlock___0Voor') and contains(@style, 'text-align: right')]//div[@data-testid='flight_card_segment_destination_time_0']")).getText();
                    String destinationDay = departureDestinationElement.findElement(By.xpath(".//div[contains(@class, 'Frame-module__flex-direction_column___ms2of FlightCardSegment-desktop-module__timeDateBlock___0Voor') and contains(@style, 'text-align: left')]//div[@data-testid='flight_card_segment_departure_date_0']")).getText();

                    flight.setDepartureDate(LocalDateTime.of(currentDate.getYear(), getMonth(departureDay.split(" ")[1]), Integer.parseInt(departureDay.split(" ")[0]), Integer.parseInt(departureTime.split(":")[0]), Integer.parseInt(departureTime.split(":")[1])));
                    // verificar caso o mes seja menor q o mes de partida ent adicionar + 1 ano
                    flight.setDestinationDate(LocalDateTime.of(currentDate.getYear(), getMonth(destinationDay.split(" ")[1]), Integer.parseInt(destinationDay.split(" ")[0]), Integer.parseInt(destinationTime.split(":")[0]), Integer.parseInt(destinationTime.split(":")[1])));

                    flight.setIsDirect(
                            departureDestinationElement.findElement(By.xpath(".//span[@data-testid='flight_card_segment_stops_0']//span[contains(@class, 'Badge-module__text___AGLG9')]")).getText().contains("Direct") ? "Y" : "N"
                    );

                    flight.setPrice(Double.parseDouble(flightElement.findElement(By.xpath(".//div[@data-testid='flight_card_price_main_price']//div[contains(@class, 'FlightCardPrice-module__priceContainer___nXXv2')]")).getText().replace("€", "").replace(",", ".")));


                    flight.setWebElement(flightElement);


                    flights.add(flight);
                } catch (Exception ex){
                    System.err.println("Exception on iteration no " + i);
                    ex.printStackTrace();
                }
            });

            System.out.println("Páginas: " + totalPages);

            AtomicInteger a = new AtomicInteger();
            flights.forEach(flight -> {
                //System.out.println("Iteration n.º " + (a.incrementAndGet()));

                if ("N".equalsIgnoreCase(flight.getIsDirect())) {
                    try {
                        try {
                            WebElement cookieBanner = driver.findElement(By.id("onetrust-banner-sdk"));
                            WebElement acceptCookiesButton = cookieBanner.findElement(By.xpath(".//button[contains(text(), 'Accept')]"));
                            if (acceptCookiesButton.isDisplayed()) {
                                acceptCookiesButton.click();
                                //System.out.println("Cookie banner accepted.");
                            }
                        } catch (NoSuchElementException | ElementNotInteractableException ignored) {}

                        String xpath = String.format(
                                "//li[contains(@class, 'List-module__item___TMd8E') and .//div[contains(text(), '%s')]]",
                                flight.getCompanyName()
                        );

                        WebElement updatedFlightElement = driver.findElement(By.xpath(xpath));
                        WebElement selectFlightButton = updatedFlightElement.findElement(By.xpath(".//button[@data-testid='flight_card_bound_select_flight']"));

                        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", selectFlightButton);
                        selectFlightButton.click();

                        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));

                        List<WebElement> stopElements = driver.findElements(By.xpath("//div[contains(@class, 'TimelineSegment-module__legsWrapper___2VF5X')]//div[starts-with(@data-testid, 'timeline_leg_') and contains(@class, 'Frame-module__align-items_center___DCS7Y Frame-module__flex-direction_row___xHVKZ')]"));

                        for(int c = 1; c <= stopElements.size(); c++) {
                            Flight stop = new Flight();

                            String departureCity =   driver.findElement(By.xpath(String.format("(//div[contains(@class, 'TimelineSegment-module__legsWrapper___2VF5X')]//div[starts-with(@data-testid, 'timeline_leg_') and contains(@class, 'Frame-module__align-items_center___DCS7Y Frame-module__flex-direction_row___xHVKZ')])[%d]//div[contains(@class, 'Frame-module__padding-bottom_4___i8mtx Frame-module__padding-left_8___ZOqOO')]//div[@data-testid='timeline_location_airport_departure']", c))).getText().split(" ")[0];
                            String destinationCity = driver.findElement(By.xpath(String.format("(//div[contains(@class, 'TimelineSegment-module__legsWrapper___2VF5X')]//div[starts-with(@data-testid, 'timeline_leg_') and contains(@class, 'Frame-module__align-items_center___DCS7Y Frame-module__flex-direction_row___xHVKZ')])[%d]//div[contains(@class, 'Frame-module__padding-left_8___ZOqOO')]//div[@data-testid='timeline_location_airport_arrival']", c))).getText().split(" ")[0];

                            stop.setDepartureCity(departureCity);
                            stop.setDestinationCity(destinationCity);

                            String departureDateText = driver.findElement(By.xpath(String.format("(//div[contains(@class, 'TimelineSegment-module__legsWrapper___2VF5X')]//div[starts-with(@data-testid, 'timeline_leg_') and contains(@class, 'Frame-module__align-items_center___DCS7Y Frame-module__flex-direction_row___xHVKZ')])[%d]//div[@data-testid='timeline_location_timestamp_departure']", c))).getText();
                            String destinationDateText = driver.findElement(By.xpath(String.format("(//div[contains(@class, 'TimelineSegment-module__legsWrapper___2VF5X')]//div[starts-with(@data-testid, 'timeline_leg_') and contains(@class, 'Frame-module__align-items_center___DCS7Y Frame-module__flex-direction_row___xHVKZ')])[%d]//div[@data-testid='timeline_location_timestamp_arrival']", c))).getText();

                            stop.setDepartureDate(LocalDateTime.of(currentDate.getYear(), getMonth(departureDateText.split(" ")[2]), Integer.parseInt(departureDateText.split(" ")[1]), Integer.parseInt(departureDateText.split(" ")[4].split(":")[0]), Integer.parseInt(departureDateText.split(" ")[4].split(":")[1])));
                            stop.setDestinationDate(LocalDateTime.of(currentDate.getYear(), getMonth(destinationDateText.split(" ")[2]), Integer.parseInt(destinationDateText.split(" ")[1]), Integer.parseInt(destinationDateText.split(" ")[4].split(":")[0]), Integer.parseInt(destinationDateText.split(" ")[4].split(":")[1])));

                            flight.addStop(stop);

                        }

                        WebElement closeButton = driver.findElement(By.xpath("//div[contains(@class, 'Overlay-module__content___+pCjC')]//button[@aria-label='Close']"));
                        closeButton.click();

                    } catch (ElementClickInterceptedException e) {
                        System.err.println("Element click intercepted for iteration " + a.get());
                        e.printStackTrace();
                    } catch (Exception e) {
                        System.err.println("Error processing direct flight for iteration " + a.get());
                        e.printStackTrace();
                    }
                }

                System.out.printf("%s at %s until %s e o mesmo %s direto, com um preço de %s. Tem um total de %d escalas. %n",
                        flight.getCompanyName(),
                        flight.getDepartureDate().format(DATE_TIME_FORMATTER),
                        flight.getDestinationDate().format(DATE_TIME_FORMATTER),
                        "Y".equalsIgnoreCase(flight.getIsDirect()) ? "é" : "não é",
                        DECIMAL_FORMAT.format(flight.getPrice()),
                        flight.getStops().size()
                );
            });





        } catch(Exception ex){
            ex.printStackTrace();
        } finally {
            driver.close();
        }
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

    public static void webscrapRoundTrip(){

    }


    public static int getMonth(String month){
        return switch (month) {
            case "Dec" -> 12;
            default -> -1;
        };
    }
}
