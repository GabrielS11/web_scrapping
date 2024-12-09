package gvv;

import com.gargoylesoftware.htmlunit.WebClient;
import gvv.Types.FlightClass;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class WebScrappingFilters {

    private static final String ONE_WAY_URL = "https://flights.booking.com/flights/{{DEPARTURE_CITY_CODE}}-{{ARRIVAL_CITY_CODE}}?type=ONEWAY&from={{DEPARTURE_CITY_CODE}}&to={{ARRIVAL_CITY_CODE}}&cabinClass={{FLIGHT_CLASS}}&depart={{DEPARTURE_DATE}}&adults={{ADULTS_QUANTITY}}&page={{PAGE_NUMBER}}";
    /**
     * Recolhe companhias aerias (Filtros)
     * Hora de partida e Hora de chegada, companhia, direto(True or False)
     * Criar um mapa para buscar as cidades ( DISCORD), (Fazer depois)*/
    public static void main(String[] args) throws IOException {
        WebClient webClient = new WebClient();



        String ONE_WAY_DAILY_URL = ONE_WAY_URL
                .replace("{{DEPARTURE_CITY_CODE}}", "OPO")
                .replace("{{ARRIVAL_CITY_CODE}}", "LIS")
                .replace("{{ADULTS_QUANTITY}}", "1")
                .replace("{{FLIGHT_CLASS}}", FlightClass.ECONOMY.name().toUpperCase())
                .replace("{{DEPARTURE_DATE}}", LocalDate.now().plusDays(30).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                ;
        String url = ONE_WAY_DAILY_URL.replace("{{PAGE_NUMBER}}", "1");

        System.setProperty("webdriver.chrome.driver", "C:\\Drivers\\chromedriver-win64\\chromedriver.exe");
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

        List<WebElement> paginationItems = driver.findElements(By.xpath("//li[contains(@class, 'List-module__item___TMd8E List-module__item--spacing-medium___foMk1')]"));

        System.out.println("Contem voos: " + paginationItems.size());

        List<String> companiesName = new ArrayList<>();
        // executa e me mostra onde est
        List<WebElement> Companies = driver.findElements((By.xpath("//div[@data-testid='airlines_airline']//div[contains(@class, 'Text-module__root--variant-body_2___QdAaF Text-module__root--color-neutral___9GMX+')]")));
        for (WebElement company : Companies) {
            // Obtenha o texto dentro do div
            String companyName = company.getAttribute("textContent");
            companiesName.add(companyName);
        }
        System.out.println(companiesName);

        //Fazer agora hora de partida e chegada
        List<WebElement> horaChegadas = driver.findElements((By.xpath("//*[@data-testid[starts-with(., 'flight_card_segment_destination_time_')]]")));
        List<WebElement> horaPartidas = driver.findElements((By.xpath("//*[@data-testid[starts-with(., 'flight_card_segment_departure_time_')]]")));

        List<String> ListaPartidaHora = new ArrayList<>();
        List<String> ListaChegadaHora = new ArrayList<>();

        // Extrai as horas de partida
        for (WebElement horaPartida : horaPartidas) {
            String time = horaPartida.getText();
            if (time != null && !time.isEmpty()) {
                ListaPartidaHora.add(time);
            }
        }

        // Extrai as horas de chegada
        for (WebElement horaChegada : horaChegadas) {
            String time = horaChegada.getText();
            if (time != null && !time.isEmpty()) {
                ListaChegadaHora.add(time);
            }
        }

        System.out.println("Hora de Partida: " + ListaPartidaHora);
        System.out.println("Hora de chegada: " + ListaChegadaHora);

        //Saber se é direto ou nao
        List<WebElement> Diretos = driver.findElements(By.xpath("//span[contains(@class, 'Badge-module__text___AGLG9')]"));
        List<String> Direct = new ArrayList<>();

        for (WebElement Direto : Diretos) {
            String type = Direto.getText();
            Direct.add(type);
        }
        System.out.println("Direto, ou quantas paragens: " + Direct);


    }

}
