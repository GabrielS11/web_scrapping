package gvv;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import gvv.Entities.FlightClass;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

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
    private String departureCity = "2024-11-20";

    private static final WebClient WEB_CLIENT = new WebClient();

    public static void main(String[] args) {
        startWebScrapping();
    }


    public static void startWebScrapping() {
        webscrapOneWay();
        webscrapRoundTrip();
    }

    public static void webscrapOneWay(){

        String ONE_WAY_DAILY_URL = ONE_WAY_URL
                .replace("{{DEPARTURE_CITY_CODE}}", "OPO")
                .replace("{{ARRIVAL_CITY_CODE}}", "LIS")
                .replace("{{ADULTS_QUANTITY}}", "1")
                .replace("{{FLIGHT_CLASS}}", FlightClass.ECONOMY.name().toUpperCase())
                .replace("{{DEPARTURE_DATE}}", LocalDate.now().plusDays(30).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                ;

        WEB_CLIENT.getOptions().setCssEnabled(false);
        WEB_CLIENT.getOptions().setThrowExceptionOnScriptError(false);
        WEB_CLIENT.getOptions().setThrowExceptionOnFailingStatusCode(false);
        //WEB_CLIENT.getOptions().setJavaScriptEnabled(false);

        int totalPages = 0;
        String url = ONE_WAY_DAILY_URL.replace("{{PAGE_NUMBER}}", "1");






        try {
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



            WebDriver driver = new ChromeDriver(options);
            System.out.println("Launching browser...");
            System.out.println("Navigating to: " + url);
            driver.get(url);
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));
            List<WebElement> paginationItems = driver.findElements(By.xpath("//li[contains(@class, 'Pagination-module__item___ZDS-g')]"));


            totalPages = paginationItems.size();

            System.out.println("Páginas: " + totalPages);

        } catch(Exception ex){
            ex.printStackTrace();
        }



    }

    public static void webscrapRoundTrip(){

    }
}
