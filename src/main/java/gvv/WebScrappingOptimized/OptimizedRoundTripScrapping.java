package gvv.WebScrappingOptimized;

import gvv.Types.FlightOneWayData;
import gvv.Types.FlightRoundTripData;
import org.openqa.selenium.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class OptimizedRoundTripScrapping {


    public static final int MAX_RETRIES = 3;
    public static final int WAITING_TIME_SECONDS = 220;


    public static List<FlightRoundTripData> processPage(WebDriver driver, String pageUrl, String departure, String destination, LocalDateTime date) {

        final List<FlightRoundTripData> flights = new ArrayList<>();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(8));
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
                    FlightRoundTripData trip = new FlightRoundTripData();
                    trip.setRetrievedDate(date);
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

                        flight.setDepartureDate(LocalDateTime.of(date.getYear(), OptimizedWebScrapping.getMonth(departureDay.split(" ")[1]), Integer.parseInt(departureDay.split(" ")[0]), Integer.parseInt(departureTime.split(":")[0]), Integer.parseInt(departureTime.split(":")[1])));
                        // verificar caso o mes seja menor q o mes de partida ent adicionar + 1 ano
                        flight.setDestinationDate(LocalDateTime.of(date.getYear(), OptimizedWebScrapping.getMonth(destinationDay.split(" ")[1]), Integer.parseInt(destinationDay.split(" ")[0]), Integer.parseInt(destinationTime.split(":")[0]), Integer.parseInt(destinationTime.split(":")[1])));

                        flight.setIsDirect(
                                departureDestinationElement.findElement(By.xpath(".//span[starts-with(@data-testid, 'flight_card_segment_stops_')]//span[contains(@class, 'Badge-module__text___AGLG9')]")).getText().contains("Direct") ? "Y" : "N"
                        );

                        flight.setCompanyName(flightElement.findElement(By.xpath(".//div[starts-with(@data-testid, 'flight_card_carrier_')]//div[contains(@class, 'Text-module__root--variant-small_1___An5P8')]")).getText());

                        String priceElement = OptimizedWebScrapping.replaceDotsExceptLast(flightElement.findElement(By.xpath(".//div[@data-testid='flight_card_price_main_price']")).getText().replace("€", "").replace(",", "."));
                        if (priceElement.contains("\n")) {
                            flight.setOriginalPrice(Double.parseDouble(priceElement.split("\n")[0]));
                            flight.setDiscountPrice(Double.parseDouble(priceElement.split("\n")[1]));
                        } else flight.setOriginalPrice(Double.parseDouble(priceElement));

                        if(segment == 1){
                            flight.setDepartureCity(departure);
                            flight.setDestinationCity(destination);
                            trip.setFlightOutward(flight);
                        } else {
                            flight.setDepartureCity(destination);
                            flight.setDestinationCity(departure);
                            trip.setFlightReturn(flight);
                        }

                    }
                    flights.add(trip);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    OptimizedWebScrapping.doNothingToHaveABreakPoint();
                }
            });

            AtomicInteger iteration = new AtomicInteger();
            return flights.stream().filter(trip -> {
                try {
                    String xpath = String.format(
                            "//div[@id='flightcard-%d']",
                            iteration.get()
                    );
                    iteration.getAndIncrement();
                    WebElement updatedFlightElement = driver.findElement(By.xpath(xpath));

                    if (OptimizedWebScrapping.openAndRetryInCaseOfFailure(driver, updatedFlightElement, MAX_RETRIES))
                        return false;
                    for (int c = 0; c <= 1; c++) {

                        FlightOneWayData flight = c == 0 ? trip.getFlightOutward() : trip.getFlightReturn();

                        if ("N".equalsIgnoreCase(flight.getIsDirect())) {
                            int totalStops = driver.findElements(By.xpath(String.format("(//div[@data-testid='timeline_segment_%d']//div[contains(@class, 'TimelineSegment-module__legsWrapper___2VF5X')]//div[starts-with(@data-testid, 'timeline_leg_') and translate(substring-after(@data-testid, 'timeline_leg_'), '0123456789', '') = ''])", c))).size();
                            for(int stopPos = 0; stopPos < totalStops; stopPos++) {
                                FlightOneWayData stop = new FlightOneWayData();

                                String departureCity = driver.findElement(By.xpath(String.format("(//div[@data-testid='timeline_segment_%d']//div[contains(@class, 'TimelineSegment-module__legsWrapper___2VF5X')]//div[starts-with(@data-testid, 'timeline_leg_') and translate(substring-after(@data-testid, 'timeline_leg_'), '0123456789', '') = ''])[%d]", c, stopPos + 1))).getText().split("\n")[1].split(" ")[0];
                                String destinationCity = driver.findElement(By.xpath(String.format("(//div[@data-testid='timeline_segment_%d']//div[contains(@class, 'TimelineSegment-module__legsWrapper___2VF5X')]//div[starts-with(@data-testid, 'timeline_leg_') and translate(substring-after(@data-testid, 'timeline_leg_'), '0123456789', '') = ''])[%d]", c, stopPos + 1))).getText().split("\n")[3].split(" ")[0];
                                stop.setDepartureCity(departureCity);
                                stop.setDestinationCity(destinationCity);


                                String departureDateText = driver.findElement(By.xpath(String.format("(//div[@data-testid='timeline_segment_%d']//div[contains(@class, 'TimelineSegment-module__legsWrapper___2VF5X')]//div[starts-with(@data-testid, 'timeline_leg_') and translate(substring-after(@data-testid, 'timeline_leg_'), '0123456789', '') = ''])[%d]//div[@data-testid='timeline_location_timestamp_departure']", c, stopPos+1))).getText();
                                String destinationDateText = driver.findElement(By.xpath(String.format("(//div[@data-testid='timeline_segment_%d']//div[contains(@class, 'TimelineSegment-module__legsWrapper___2VF5X')]//div[starts-with(@data-testid, 'timeline_leg_') and translate(substring-after(@data-testid, 'timeline_leg_'), '0123456789', '') = ''])[%d]//div[@data-testid='timeline_location_timestamp_arrival']", c, stopPos+1))).getText();

                                stop.setDepartureDate(LocalDateTime.of(date.getYear(), OptimizedWebScrapping.getMonth(departureDateText.split(" ")[2]), Integer.parseInt(departureDateText.split(" ")[1]), Integer.parseInt(departureDateText.split(" ")[4].split(":")[0]), Integer.parseInt(departureDateText.split(" ")[4].split(":")[1])));
                                stop.setDestinationDate(LocalDateTime.of(date.getYear(), OptimizedWebScrapping.getMonth(destinationDateText.split(" ")[2]), Integer.parseInt(destinationDateText.split(" ")[1]), Integer.parseInt(destinationDateText.split(" ")[4].split(":")[0]), Integer.parseInt(destinationDateText.split(" ")[4].split(":")[1])));

                                stop.setCompanyName(driver.findElement(By.xpath(String.format("(//div[@data-testid='timeline_segment_%d']//div[contains(@class, 'TimelineSegment-module__legsWrapper___2VF5X')]//div[starts-with(@data-testid, 'timeline_leg_') and translate(substring-after(@data-testid, 'timeline_leg_'), '0123456789', '') = ''])[%d]//div[@data-testid='timeline_leg_info_carrier']", c, stopPos+1))).getText());

                                stop.setAirPlaneNumber(driver.findElement(By.xpath(String.format("(//div[@data-testid='timeline_segment_%d']//div[contains(@class, 'TimelineSegment-module__legsWrapper___2VF5X')]//div[starts-with(@data-testid, 'timeline_leg_') and translate(substring-after(@data-testid, 'timeline_leg_'), '0123456789', '') = ''])[%d]//div[@data-testid='timeline_leg_info_flight_number_and_class']", c, stopPos+1))).getText().split(" ")[0]);
                                if(stopPos == 0 || stopPos+1 == totalStops){
                                    flight.setAirPlaneNumber(stop.getAirPlaneNumber());
                                }
                                if(flight.getAirPlaneNumber() == null){
                                    System.out.println("why?");
                                }
                                flight.addStop(stop);
                            }
                            if(c == 0){
                                trip.setFlightOutward(flight);
                            } else {
                                trip.setFlightReturn(flight);
                            }
                        } else {
                            flight.setAirPlaneNumber(driver.findElements(By.xpath(String.format("(//div[@data-testid='timeline_segment_%d']//div[contains(@class, 'TimelineSegment-module__legsWrapper___2VF5X')]//div[starts-with(@data-testid, 'timeline_leg_') and translate(substring-after(@data-testid, 'timeline_leg_'), '0123456789', '') = ''])//div[@data-testid='timeline_leg_info_flight_number_and_class']", c))).getFirst().getText().split(" ")[0]);
                        }
                    }
                    WebElement closeButton = driver.findElement(By.xpath("//div[contains(@class, 'Overlay-module__content___+pCjC')]//button[@aria-label='Close']"));
                    closeButton.click();
                } catch (Exception e) {
                    OptimizedWebScrapping.doNothingToHaveABreakPoint();
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


