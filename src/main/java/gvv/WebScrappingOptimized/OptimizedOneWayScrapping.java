package gvv.WebScrappingOptimized;

import gvv.Entities.FlightClass;
import gvv.Entities.FlightOneWayData;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OptimizedOneWayScrapping {


    public static final int MAX_RETRIES = 3;
    public static final int WAITING_TIME_SECONDS = 220;


    public static List<FlightOneWayData> processPage(WebDriver driver, String pageUrl, String departure, String destination, LocalDateTime date) {

        final List<FlightOneWayData> flights = new ArrayList<>();
        try {
            for(int i = 0; i < MAX_RETRIES; i++) {
                driver.get(pageUrl);
                String pageSource = driver.getPageSource();

                if (pageSource.contains("HTTP ERROR 429") || driver.getTitle().contains("429")) {
                    System.out.println("Resource limit reached, waiting " + WAITING_TIME_SECONDS + " seconds");
                    Thread.sleep(WAITING_TIME_SECONDS * 1000);
                } else i = MAX_RETRIES;
            }


            List<WebElement> flightElements = driver.findElements(By.xpath("//li[contains(@class, 'List-module__item___TMd8E List-module__item--spacing-medium___foMk1')]"));

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

                    flight.setDepartureDate(LocalDateTime.of(date.getYear(), OptimizedWebScrapping.getMonth(departureDay.split(" ")[1]), Integer.parseInt(departureDay.split(" ")[0]), Integer.parseInt(departureTime.split(":")[0]), Integer.parseInt(departureTime.split(":")[1])));
                    // verificar caso o mes seja menor q o mes de partida ent adicionar + 1 ano
                    flight.setDestinationDate(LocalDateTime.of(date.getYear(), OptimizedWebScrapping.getMonth(destinationDay.split(" ")[1]), Integer.parseInt(destinationDay.split(" ")[0]), Integer.parseInt(destinationTime.split(":")[0]), Integer.parseInt(destinationTime.split(":")[1])));

                    flight.setIsDirect(
                            departureDestinationElement.findElement(By.xpath(".//span[@data-testid='flight_card_segment_stops_0']//span[contains(@class, 'Badge-module__text___AGLG9')]")).getText().contains("Direct") ? "Y" : "N"
                    );

                    flight.setCompanyName(flightElement.findElement(By.xpath(".//div[@data-testid='flight_card_carrier_0']//div[contains(@class, 'Text-module__root--variant-small_1___An5P8')]")).getText());

                    String priceElement = flightElement.findElement(By.xpath(".//div[@data-testid='flight_card_price_main_price']")).getText().replace("€", "").replace(",", ".");
                    if (priceElement.contains("\n")) {
                        flight.setOriginalPrice(Double.parseDouble(OptimizedWebScrapping.replaceDotsExceptLast(priceElement.split("\n")[0])));
                        flight.setDiscountPrice(Double.parseDouble(OptimizedWebScrapping.replaceDotsExceptLast(priceElement.split("\n")[1])));
                    } else flight.setOriginalPrice(Double.parseDouble(OptimizedWebScrapping.replaceDotsExceptLast(priceElement)));
                    flights.add(flight);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    OptimizedWebScrapping.doNothingToHaveABreakPoint();
                }
            });
            // Abrir o modal e adquirir os dados extra necessários
            return flights.stream().filter(flight -> {
                try {
                    String xpath = String.format(
                            "//li[contains(@class, 'List-module__item___TMd8E') and .//div[contains(text(), '%s')]]",
                            flight.getCompanyName()
                    );
                    WebElement updatedFlightElement = driver.findElement(By.xpath(xpath));
                    for(int selectFlightRetries = 0; selectFlightRetries < MAX_RETRIES; selectFlightRetries++) {
                        try {
                            WebElement selectFlightButton = updatedFlightElement.findElement(By.xpath(".//button[@data-testid='flight_card_bound_select_flight']"));

                            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", selectFlightButton);
                            selectFlightButton.click();

                            selectFlightRetries = MAX_RETRIES;
                        } catch (ElementClickInterceptedException intercepted) {
                            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
                            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("LoadingScreen-module__loadingScreen___TJHLs")));
                        }
                    }
                    if (OptimizedWebScrapping.removeNoFlightsModal(driver)) return false;
                    List<WebElement> stopElements = driver.findElements(By.xpath("//div[contains(@class, 'TimelineSegment-module__legsWrapper___2VF5X')]//div[starts-with(@data-testid, 'timeline_leg_') and contains(@class, 'Frame-module__align-items_center___DCS7Y Frame-module__flex-direction_row___xHVKZ')]"));
                    for (int c = 1; c <= stopElements.size(); c++) {
                        try {
                            if ("N".equalsIgnoreCase(flight.getIsDirect())) {
                                FlightOneWayData stop = new FlightOneWayData();

                                String[] departureCity = driver.findElement(By.xpath(String.format("(//div[contains(@class, 'TimelineSegment-module__legsWrapper___2VF5X')]//div[starts-with(@data-testid, 'timeline_leg_') and contains(@class, 'Frame-module__align-items_center___DCS7Y Frame-module__flex-direction_row___xHVKZ')])[%d]//div[contains(@class, 'Frame-module__padding-bottom_4___i8mtx Frame-module__padding-left_8___ZOqOO')]//div[@data-testid='timeline_location_airport_departure']", c))).getText().split(" · ");
                                String[] destinationCity = driver.findElement(By.xpath(String.format("(//div[contains(@class, 'TimelineSegment-module__legsWrapper___2VF5X')]//div[starts-with(@data-testid, 'timeline_leg_') and contains(@class, 'Frame-module__align-items_center___DCS7Y Frame-module__flex-direction_row___xHVKZ')])[%d]//div[contains(@class, 'Frame-module__padding-left_8___ZOqOO')]//div[@data-testid='timeline_location_airport_arrival']", c))).getText().split(" · ");

                                stop.setDepartureCity(departureCity[0]);
                                stop.setDepartureAirport(departureCity[1]);
                                stop.setDestinationCity(destinationCity[0]);
                                stop.setDestinationAirport(destinationCity[1]);

                                String departureDateText = driver.findElement(By.xpath(String.format("(//div[contains(@class, 'TimelineSegment-module__legsWrapper___2VF5X')]//div[starts-with(@data-testid, 'timeline_leg_') and contains(@class, 'Frame-module__align-items_center___DCS7Y Frame-module__flex-direction_row___xHVKZ')])[%d]//div[@data-testid='timeline_location_timestamp_departure']", c))).getText();
                                String destinationDateText = driver.findElement(By.xpath(String.format("(//div[contains(@class, 'TimelineSegment-module__legsWrapper___2VF5X')]//div[starts-with(@data-testid, 'timeline_leg_') and contains(@class, 'Frame-module__align-items_center___DCS7Y Frame-module__flex-direction_row___xHVKZ')])[%d]//div[@data-testid='timeline_location_timestamp_arrival']", c))).getText();

                                stop.setDepartureDate(LocalDateTime.of(date.getYear(), OptimizedWebScrapping.getMonth(departureDateText.split(" ")[2]), Integer.parseInt(departureDateText.split(" ")[1]), Integer.parseInt(departureDateText.split(" ")[4].split(":")[0]), Integer.parseInt(departureDateText.split(" ")[4].split(":")[1])));
                                stop.setDestinationDate(LocalDateTime.of(date.getYear(), OptimizedWebScrapping.getMonth(destinationDateText.split(" ")[2]), Integer.parseInt(destinationDateText.split(" ")[1]), Integer.parseInt(destinationDateText.split(" ")[4].split(":")[0]), Integer.parseInt(destinationDateText.split(" ")[4].split(":")[1])));


                                stop.setCompanyName(driver.findElement(By.xpath(String.format("(//div[@data-testid='timeline_leg_info_carrier'])[%d]", c))).getText());


                                stop.setAirPlaneNumber(driver.findElement(By.xpath(String.format("(//div[@data-testid='timeline_leg_info_flight_number_and_class'])[%d]", c))).getText().split(" · ")[0]);
                                stop.setFlightClass(FlightClass.getFlightClass(driver.findElement(By.xpath(String.format("(//div[@data-testid='timeline_leg_info_flight_number_and_class'])[%d]", c))).getText().split(" · ")[1]));
                                flight.addStop(stop);
                            } else {
                                flight.setAirPlaneNumber(driver.findElement(By.xpath(String.format("(//div[@data-testid='timeline_leg_info_flight_number_and_class'])[%d]", c))).getText().split(" ")[0]);
                            }
                        } catch (Exception ex) {
                            //ex.printStackTrace();
                            OptimizedWebScrapping.doNothingToHaveABreakPoint();
                            return false;
                        }
                    }
                    try {
                        WebElement closeButton = driver.findElement(By.xpath("//div[contains(@class, 'Overlay-module__content___+pCjC')]//button[@aria-label='Close']"));
                        closeButton.click();
                    } catch (Exception ex) {
                        OptimizedWebScrapping.doNothingToHaveABreakPoint();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    OptimizedWebScrapping.doNothingToHaveABreakPoint();
                    return false;
                }
                return true;
            }).collect(Collectors.toCollection(ArrayList::new));

        } catch (Exception e) {
            e.printStackTrace();
            OptimizedWebScrapping.doNothingToHaveABreakPoint();
        }

        return List.of();
    }
}


