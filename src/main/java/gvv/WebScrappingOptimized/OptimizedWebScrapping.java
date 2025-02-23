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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.NoSuchElementException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class OptimizedWebScrapping {

    private static final String CHROME_DRIVEER_PATH = "D:\\Tools\\chromedriver-win64\\chromedriver.exe";

    private static final String ONE_WAY_URL = "https://flights.booking.com/flights/{{DEPARTURE_CITY_CODE}}-{{ARRIVAL_CITY_CODE}}?type=ONEWAY&from={{DEPARTURE_CITY_CODE}}&to={{ARRIVAL_CITY_CODE}}&cabinClass={{FLIGHT_CLASS}}&depart={{DEPARTURE_DATE}}&adults={{ADULTS_QUANTITY}}&page={{PAGE_NUMBER}}";
    private static final String ROUND_TRIP_URL = "https://flights.booking.com/flights/{{DEPARTURE_CITY_CODE}}-{{ARRIVAL_CITY_CODE}}/?type=ROUNDTRIP&adults={{ADULTS_QUANTITY}}&cabinClass={{FLIGHT_CLASS}}&from={{DEPARTURE_CITY_CODE}}&to={{ARRIVAL_CITY_CODE}}&depart={{DEPARTURE_DATE}}&return={{RETURN_DATE}}&page={{PAGE_NUMBER}}";

    private static final Map<String, List<String[]>> FLIGHTS = new HashMap<>() {{
        /*put("OPO - Francisco Sá Carneiro Airport", Collections.singletonList(
                new String[]{"HND", "Tokyo Haneda Airport"}
        ));*/
        put("OPO - Francisco Sá Carneiro Airport", List.of(
                new String[]{"HND", "Tokyo Haneda Airport"},
                new String[]{"GIG", "Rio de Janeiro/Galeao International Airport"},
                new String[]{"ANR", "Antwerp International Airport"}
        ));
    }};


    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    public static void main(String[] args) {
        try {
            startWebScrapping();
        } catch (Exception ex){
            System.out.println("An error message was detected but ingored.");
        }
    }


    public static void startWebScrapping() {
        // Número de Threads a ocorrer em simultâneo - Isto é irá acontecer o scrapping to ECONOMY E O PREMIUM_ECONOMY ao mesmo tempo, por exemplo
        final int maxThreadPool = 1;
        final int cooldown = 28000;
        ExecutorService executor = Executors.newFixedThreadPool(maxThreadPool);
        List<Future<?>> tasks = new ArrayList<>();

        LocalDateTime currentTime = LocalDateTime.now();
        // Hoje a 30 dias, será a data de partida do voo
        LocalDateTime departureDate = currentTime.plusDays(30);
        // Hoje a 34 dias será a data de retorno
        LocalDateTime returnDate = currentTime.plusDays(34);

        // Máximo de adultos a ser percorridos
        int adultsQt = 1;

        // Percorrer todas as partidas
        for (String departureCityAirport : FLIGHTS.keySet()) {
            String departure = departureCityAirport.split(" - ")[0];
            String departureAirportName = departureCityAirport.split(" - ")[1];
            // Percorrer todas os destinos de cada partida
            for (String[] destinationArr : FLIGHTS.get(departureCityAirport)) {
                String destination = destinationArr[0];
                String destinationAirportName = destinationArr[1];
                // Percorrer todas as classes (consultar FLIGHTCLASS) dos voos, para cada destino
                for (FlightClass flightClass : FlightClass.values()) {
                    tasks.add(executor.submit(() -> {
                        WebDriver driver = getWebDriver(); // Cada thread usa seu próprio WebDriver
                        try {
                            int totalPages;
                            // buscar o total de páginas daquela partida, para aquele destino e para aquela classe
                            totalPages = getTotalPages(driver, ONE_WAY_URL, departure, destination, departureDate, LocalDateTime.now(), adultsQt, flightClass);
                            for (int page = 1; page <= totalPages && true; page++) {
                                System.out.printf("Processing page %d/%d for %s -> %s [ONE WAY] [%s]%n", page, totalPages, departure, destination, flightClass.name().toUpperCase());

                                // Preparar o URL
                                String pageUrl = ONE_WAY_URL.replace("{{DEPARTURE_CITY_CODE}}", departure)
                                        .replace("{{ARRIVAL_CITY_CODE}}", destination)
                                        .replace("{{ADULTS_QUANTITY}}", String.valueOf(adultsQt))
                                        .replace("{{FLIGHT_CLASS}}", flightClass.name().toUpperCase())
                                        .replace("{{DEPARTURE_DATE}}", departureDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                                        .replace("{{PAGE_NUMBER}}", String.valueOf(page));


                                // Invocar o scrapping to OneWay - Ida apenas.
                                List<FlightOneWayData> flights = OptimizedOneWayScrapping.processPage(driver, pageUrl, departure, destination, departureDate).stream().peek(flightData -> {
                                    flightData.setFlightClass(flightClass);
                                    flightData.setAdults(adultsQt);
                                    flightData.setDepartureAirport(departureAirportName);
                                    flightData.setDestinationAirport(destinationAirportName);
                                    flightData.setRetrievedDate(LocalDateTime.now());
                                }).toList();

                                //DatabaseHandler.processOneWay(flights);
                                if (page < totalPages && !flights.isEmpty()) {
                                    System.out.println("Resetting limit (waiting " + (cooldown * maxThreadPool) / 1000 + " seconds) before continuing...");
                                    Thread.sleep(cooldown * maxThreadPool);
                                }
                            }


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
                                    flightData.setRetrievedDate(LocalDateTime.now());

                                    flightData.setDepartureAirport(departureAirportName);
                                    flightData.setDestinationAirport(destinationAirportName);

                                    flightData.setDepartureDate(departureDate);
                                    flightData.setReturnDate(returnDate);
                                    flightData.getFlightReturn().setFlightClass(flightClass);
                                    flightData.getFlightReturn().setAdults(adultsQt);
                                }).toList();

                                //DatabaseHandler.processRoundTrip(flights);

                                if (page < totalPages && !flights.isEmpty()) {
                                    System.out.println("Resetting limit (waiting " + (cooldown * maxThreadPool) / 1000 + " seconds) before continuing...");
                                    Thread.sleep(cooldown * maxThreadPool);
                                }
                            }

                        } catch (Exception ignored) {

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
            } catch (Exception ignored) {
            }
        });
        executor.shutdown();
    }


    // Remover o modal de no flights, no caso ele não remove, apenas esconde, porque se removermos o site solta um erro, então preferimos esconder.
    public static boolean removeNoFlightsModal(WebDriver driver) {
        try {
            driver.manage().timeouts().implicitlyWait(Duration.ofMillis(0));

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofMillis(1000));

            WebElement overlay = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//div[contains(@class, 'Overlay-module__root___eAAPR') and contains(@class, 'Overlay-module__root--visible___qlGig') and .//h1[contains(text(), \"This flight's not available\")]]")
            ));

            if (overlay.getText().contains("This flight's not available")) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].style.display='none';", overlay);
                return true;
            }
        } catch (Exception ignored) {
        } finally {
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        }
        return false;
    }

    // Aceitar o popup do cookies
    private static void handleCookies(WebDriver driver) {
        try {
            WebElement cookieBanner = driver.findElement(By.id("onetrust-banner-sdk"));
            WebElement acceptCookiesButton = cookieBanner.findElement(By.xpath(".//button[contains(text(), 'Accept')]"));
            if (acceptCookiesButton.isDisplayed()) {
                acceptCookiesButton.click();
            }
        } catch (Exception ignored) {

        }
    }

    // Configurar o webdriver
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


    // Função para debug.
    public static void doNothingToHaveABreakPoint(){
        return;
    }

    // Função para adquirir todas as paginas
    public static int getTotalPages(WebDriver driver, String baseUrl, String departure, String destination, LocalDateTime departureDate, LocalDateTime returnDate, int adultsQt, FlightClass flightClass) {
        Integer totalPages = null;
        String url = baseUrl.replace("{{DEPARTURE_CITY_CODE}}", departure)
                .replace("{{ARRIVAL_CITY_CODE}}", destination)
                .replace("{{ADULTS_QUANTITY}}", String.valueOf(adultsQt))
                .replace("{{FLIGHT_CLASS}}", flightClass.name().toUpperCase())
                .replace("{{DEPARTURE_DATE}}", departureDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .replace("{{RETURN_DATE}}", returnDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .replace("{{PAGE_NUMBER}}", "")
                ;
        try {
            System.out.println("Launching browser...");
            System.out.println("Navigating to: " + url);
            driver.get(url);
            totalPages = Integer.parseInt(driver.findElement(By.xpath("(//li[contains(@class, 'Pagination-module__item___gZxdK')])[last()]")).getText());
            handleCookies(driver);
            return totalPages;
        } catch (Exception ex) {
            ex.printStackTrace();
            doNothingToHaveABreakPoint();
        }
        return 0;
    }


    public static boolean openAndRetryInCaseOfFailure(WebDriver driver, WebElement updatedFlightElement, int maxRetries) {
        for(int selectFlightRetries = 0; selectFlightRetries < maxRetries; selectFlightRetries++) {
            try {
                // Enocntra o botão
                WebElement selectFlightButton = updatedFlightElement.findElement(By.xpath(".//button[@data-testid='flight_card_bound_select_flight']"));

                // Move-se o html até a zona do botão pois, não é permitido pelo DOM clicar num botão que não está na vista do utilizador.
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", selectFlightButton);
                selectFlightButton.click();

                selectFlightRetries = maxRetries;
            } catch (ElementClickInterceptedException intercepted) {
                // Caso dê erro, é porque tem uma loading screen na frente dele, então tentamos remover, e voltamos a tentar.
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
                wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("LoadingScreen-module__loadingScreen___TJHLs")));
            }
        }
        return OptimizedWebScrapping.removeNoFlightsModal(driver);
    }


    // Inicialmente estavamos a guardar em JSON os dados, e não em Base de Dados.
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

        } catch (IOException ignored) {
        }
    }
}






