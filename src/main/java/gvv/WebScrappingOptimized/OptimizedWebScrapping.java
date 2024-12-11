package gvv.WebScrappingOptimized;

import gvv.Types.FlightClass;
import gvv.Types.FlightOneWayData;
import gvv.Types.FlightRoundTripData;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.NoSuchElementException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class OptimizedWebScrapping {


    // VITOR DESKTOP
    private static final String CHROME_DRIVEER_PATH = "D:\\Tools\\chromedriver-win64\\chromedriver.exe";
    // VITOR PORTÁTIL
    //private static final String CHROME_DRIVEER_PATH = "C:\\Drivers\\chromedriver-win64\\chromedriver.exe";

    private static final String ONE_WAY_URL = "https://flights.booking.com/flights/{{DEPARTURE_CITY_CODE}}-{{ARRIVAL_CITY_CODE}}?type=ONEWAY&from={{DEPARTURE_CITY_CODE}}&to={{ARRIVAL_CITY_CODE}}&cabinClass={{FLIGHT_CLASS}}&depart={{DEPARTURE_DATE}}&adults={{ADULTS_QUANTITY}}&page={{PAGE_NUMBER}}";
    private static final String ROUND_TRIP_URL = "https://flights.booking.com/flights/{{DEPARTURE_CITY_CODE}}-{{ARRIVAL_CITY_CODE}}/?type=ROUNDTRIP&adults={{ADULTS_QUANTITY}}&cabinClass={{FLIGHT_CLASS}}&from={{DEPARTURE_CITY_CODE}}&to={{ARRIVAL_CITY_CODE}}&depart={{DEPARTURE_DATE}}&return={{RETURN_DATE}}&page={{PAGE_NUMBER}}";

    private static final Map<String, List<String[]>> FLIGHTS = new HashMap<>() {{
        ArrayList<String[]> arr = new ArrayList<>();
        arr.add(new String[]{"HND", "Tokyo Haneda Airport"});
        put("OPO - Francisco Sá Carneiro Airport", arr);
                /*List.of(
                new String[]{"HND", "Tokyo Haneda Airport"},
                new String[]{"GIG", "Rio de Janeiro/Galeao International Airport"},
                new String[]{"ANR", "Antwerp International Airport"}*/
        //));
    }};


    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    public static void main(String[] args) {
        startWebScrapping();
    }


    public static void startWebScrapping() {
        final int maxThreadPool = 1;
        ExecutorService executor = Executors.newFixedThreadPool(maxThreadPool);
        List<Future<?>> tasks = new ArrayList<>();

        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime departureDate = LocalDateTime.now().plusDays(30);
        LocalDateTime returnDate = LocalDateTime.now().plusDays(34);
        int adultsQt = 1;



        for (String departureCityAirport : FLIGHTS.keySet()) {
            String departure = departureCityAirport.split(" - ")[0];
            String departureAirportName = departureCityAirport.split(" - ")[1];
            for (String[] destinationArr : FLIGHTS.get(departureCityAirport)) {
                String destination = destinationArr[0];
                String destinationAirportName = destinationArr[1];

                for (FlightClass flightClass : FlightClass.values()) {
                    tasks.add(executor.submit(() -> {
                        WebDriver driver = getWebDriver(); // Cada thread usa seu próprio WebDriver
                        try {
                            int totalPages = getTotalPages(driver, ONE_WAY_URL, departure, destination, departureDate, LocalDateTime.now(), adultsQt, flightClass);
                            for (int page = 1; page <= totalPages; page++) {
                                System.out.printf("Processing page %d/%d for %s -> %s [ONE WAY] [%s]%n", page, totalPages, departure, destination, flightClass.name().toUpperCase());

                                String pageUrl = ONE_WAY_URL.replace("{{DEPARTURE_CITY_CODE}}", departure)
                                        .replace("{{ARRIVAL_CITY_CODE}}", destination)
                                        .replace("{{ADULTS_QUANTITY}}", String.valueOf(adultsQt))
                                        .replace("{{FLIGHT_CLASS}}", flightClass.name().toUpperCase())
                                        .replace("{{DEPARTURE_DATE}}", departureDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                                        .replace("{{PAGE_NUMBER}}", String.valueOf(page));

                                List<FlightOneWayData> flights = OptimizedOneWayScrapping.processPage(driver, pageUrl, departure, destination, departureDate).stream().peek(flightData -> {
                                    flightData.setFlightClass(flightClass);
                                    flightData.setAdults(adultsQt);
                                    flightData.setDepartureAirport(departureAirportName);
                                    flightData.setDestinationAirport(destinationAirportName);
                                }).toList();


                                String writePath = "./flights/" + departureCityAirport + "/" + String.join(" - ", destinationArr) + "/" + flightClass + "/ONE-WAY/" + currentTime.format(DATE_TIME_FORMATTER).replace(" ", "").replace(":", "h") + "m.json";
                                writeToFile(writePath, "[", false);
                                for(int i = 0; i < flights.size(); i++) {
                                    FlightOneWayData flight = flights.get(i);
                                    //System.out.print(flight);

                                    writeToFile(writePath, flight.toString() + (i+1 == flights.size() ? "" : "," ), true);
                                }
                                writeToFile(writePath, "]",true);
                                DatabaseHandler.processOneWay(flights);
                                if (page < totalPages) {
                                    System.out.println("Resetting limit (waiting 70 seconds) before continuing...");
                                    Thread.sleep(25000*maxThreadPool);
                                }

                            }

                            if(true) return;

                            // IDA E VOLTA
                            totalPages = getTotalPages(driver, ROUND_TRIP_URL, departure, destination, departureDate, returnDate, adultsQt, flightClass);
                            for (int page = 1; page <= totalPages; page++) {
                                System.out.printf("Processing page %d/%d for %s -> %s [ROUNDTRIP] [%s]%n", page, totalPages, departure, destination, flightClass.name().toUpperCase());

                                String pageUrl = ROUND_TRIP_URL.replace("{{DEPARTURE_CITY_CODE}}", departure)
                                        .replace("{{ARRIVAL_CITY_CODE}}", destination)
                                        .replace("{{ADULTS_QUANTITY}}", String.valueOf(adultsQt))
                                        .replace("{{FLIGHT_CLASS}}", flightClass.name().toUpperCase())
                                        .replace("{{DEPARTURE_DATE}}", departureDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                                        .replace("{{RETURN_DATE}}", returnDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                                        .replace("{{PAGE_NUMBER}}", String.valueOf(page));

                                List<FlightRoundTripData> flights = OptimizedRoundTripScrapping.processPage(driver, pageUrl, departure, destination, departureDate).stream().peek(flightData -> {
                                    flightData.setDepartureCity(departure);
                                    flightData.setDepartureCity(destination);
                                    flightData.getFlightOutward().setDepartureCity(departure);
                                    flightData.getFlightOutward().setDestinationCity(destination);
                                    flightData.getFlightOutward().setFlightClass(flightClass);
                                    flightData.getFlightOutward().setAdults(adultsQt);

                                    flightData.setDepartureAirport(departureAirportName);
                                    flightData.setDestinationAirport(destinationAirportName);

                                    flightData.setDepartureDate(departureDate);
                                    flightData.setReturnDate(returnDate);
                                    flightData.getFlightReturn().setFlightClass(flightClass);
                                    flightData.getFlightReturn().setAdults(adultsQt);
                                }).toList();


                                String writePath = "./flights/" + departureCityAirport + "/" + String.join(" - ", destinationArr) + "/" + flightClass + "/ROUND-TRIP/" + currentTime.format(DATE_TIME_FORMATTER).replace(" ", "").replace(":", "h") + "m.json";
                                writeToFile(writePath, "[", false);
                                for(int i = 0; i < flights.size(); i++) {
                                    FlightRoundTripData flight = flights.get(i);
                                    System.out.print(flight);

                                    writeToFile(writePath, flight.toString() + (i+1 == flights.size() ? "" : "," ), true);
                                }
                                writeToFile(writePath, "]",true);

                                if (page < totalPages) {
                                    System.out.println("Resetting limit (waiting 70 seconds) before continuing...");
                                    Thread.sleep(25000*maxThreadPool);
                                }
                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            driver.quit(); // Fechar o WebDriver ao terminar
                        }
                    }));



                }
            }
        }

        // Aguardar todas as threads
        tasks.forEach(task -> {
            try {
                task.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        executor.shutdown();
    }

    public static boolean removeNoFlightsModal(WebDriver driver) {
        try {
            driver.manage().timeouts().implicitlyWait(Duration.ofMillis(0));

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofMillis(1000));

            WebElement overlay = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//div[contains(@class, 'Overlay-module__root___8ZZO+') and contains(@class, 'Overlay-module__root--visible___VhicQ') and .//h1[contains(text(), \"This flight's not available\")]]")
            ));

            if (overlay.getText().contains("This flight's not available")) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].style.display='none';", overlay);
                return true;
            }
        } catch (TimeoutException ignored) {
            // Modal not found in 200ms
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Restore the implicit wait
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        }
        return false;
    }

    private static void handleCookies(WebDriver driver) {
        try {
            WebElement cookieBanner = driver.findElement(By.id("onetrust-banner-sdk"));
            WebElement acceptCookiesButton = cookieBanner.findElement(By.xpath(".//button[contains(text(), 'Accept')]"));
            if (acceptCookiesButton.isDisplayed()) {
                acceptCookiesButton.click();
            }
        } catch (NoSuchElementException | ElementNotInteractableException ignored) {

        }
    }


    private static WebDriver getWebDriver() {
        System.setProperty("webdriver.chrome.driver", CHROME_DRIVEER_PATH);
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
        ChromeDriver driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        return driver;
    }

    public static String replaceDotsExceptLast(String input) {
        int lastDotIndex = input.lastIndexOf(".");
        if (lastDotIndex == -1) {
            return input;
        }
        String beforeLastDot = input.substring(0, lastDotIndex).replace(".", "");
        String afterLastDot = input.substring(lastDotIndex);
        return beforeLastDot + afterLastDot;
    }

    public static int getMonth(String month) {
        return switch (month) {
            case "Jan" -> 1;
            case "Feb" -> 2;
            case "Mar" -> 3;
            case "Apr" -> 4;
            case "May" -> 5;
            case "Jun" -> 6;
            case "Jul" -> 7;
            case "Aug" -> 8;
            case "Sep" -> 9;
            case "Oct" -> 10;
            case "Nov" -> 11;
            case "Dec" -> 12;
            default -> -1;
        };
    }


    public static void doNothingToHaveABreakPoint(){
        return;
    }

    public static int getTotalPages(WebDriver driver, String baseUrl, String departure, String destination, LocalDateTime departureDate, LocalDateTime returnDate, int adultsQt, FlightClass flightClass) {
        Integer totalPages = null;
        String url = baseUrl.replace("{{DEPARTURE_CITY_CODE}}", departure)
                .replace("{{ARRIVAL_CITY_CODE}}", destination)
                .replace("{{ADULTS_QUANTITY}}", String.valueOf(adultsQt))
                .replace("{{FLIGHT_CLASS}}", flightClass.name().toUpperCase())
                .replace("{{DEPARTURE_DATE}}", departureDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .replace("{{RETURN_DATE}}", returnDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .replace("{{PAGE_NUMBER}}", "");
        try {
            System.out.println("Launching browser...");
            System.out.println("Navigating to: " + url);
            driver.get(url);
            totalPages = Integer.parseInt(driver.findElement(By.xpath("(//li[contains(@class, 'Pagination-module__item___ZDS-g')])[last()]")).getText());
            handleCookies(driver);
            return totalPages;
        } catch (Exception ex) {
            doNothingToHaveABreakPoint();
        }
        return 0;
    }

    public static boolean openAndRetryInCaseOfFailure(WebDriver driver, WebElement updatedFlightElement, int maxRetries) {
        for(int selectFlightRetries = 0; selectFlightRetries < maxRetries; selectFlightRetries++) {
            try {
                WebElement selectFlightButton = updatedFlightElement.findElement(By.xpath(".//button[@data-testid='flight_card_bound_select_flight']"));

                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", selectFlightButton);
                selectFlightButton.click();

                selectFlightRetries = maxRetries;
            } catch (ElementClickInterceptedException intercepted) {
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
                wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("LoadingScreen-module__loadingScreen___TJHLs")));
            }
        }
        if (OptimizedWebScrapping.removeNoFlightsModal(driver)) return true;
        return false;
    }


    public static void writeToFile(String filePath, String content, boolean append) {
        File file = new File(filePath);
        try {
            if (file.getParentFile() != null && !file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
            try (FileWriter writer = new FileWriter(file, append)) {
                writer.write(content);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}






