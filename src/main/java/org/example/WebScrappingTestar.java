package org.example;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.apache.http.util.Args;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class WebScrappingTestar {

    private static final SimpleDateFormat format = new SimpleDateFormat("dd/mm/yyyy HH:mm");;
    public static void main(String[] Args) throws IOException {
        WebClient webClient = new WebClient();

        // Create a new WebClient instance

        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);


        String url = "https://www.kayak.pt/flights/OPO-LIS/2024-11-22?sort=bestflight_a"; //Temos de alterar a data e o OPO-LIS
        HtmlPage page = webClient.getPage(url);

        List<HtmlElement> products = page.getBody()
                .getElementsByAttribute("div", "class", "nrc6 nrc6-mod-pres-default")
                .stream()
                .filter(element -> element.getElementsByAttribute("div", "class", "JW4C").isEmpty()) // Exclui elementos que contenham filhos com a classe "JW4C"
                .toList();

        // Exibe a quantidade de voos que não têm a classe "JW4C"
        System.out.println("Quantidade de voos disponíveis: " + products.size());

        // Obtenha todos os voos
        List<HtmlElement> flights = page.getByXPath("//div[contains(@class, 'vmXl vmXl-mod-variant-large')]");
        // Se o voo é ou não direto
        HtmlElement[] alt = page.getBody().getElementsByAttribute("span", "class", "JWEO-stops-text").toArray(new HtmlElement[0]);
        // Companhia
        List <HtmlImage> flightElements = page.getByXPath(".//img[contains(@src, 'provider-logos') and @alt]");
        // Preços
        List<HtmlElement> precoElements = page.getBody().getElementsByAttribute("div", "class", "f8F1-price-text");
        // Obtenha todas as informações sobre escalas
        List<HtmlElement> escalas = page.getByXPath("//div[contains(@class, 'vmXl vmXl-mod-variant-default')]//span[contains(@class, 'JWEO-stops-text')]");

        // Para cada voo, capture as informações necessárias
        List<String> companhias = new ArrayList<>();
        List<String> saidas = new ArrayList<>();
        List<String> chegadas = new ArrayList<>();
        List<String> direto = new ArrayList<>();
        List<String> preco = new ArrayList<>();

        for(int i = 0; i < flights.size(); i++){
            Flight flight = new Flight();
            HtmlElement saidaElement = flights.get(i).getFirstByXPath(".//span[1]");
            HtmlElement chegadaElement = flights.get(i).getFirstByXPath(".//span[3]");

            String todayDate = LocalDate.now().getDayOfMonth()+ "/" + LocalDate.now().getMonth().getValue() + 1 + "/" + LocalDate.now().getYear() + " ";
            String saida = saidaElement != null ? todayDate + saidaElement.asNormalizedText() : null;
            String chegada = chegadaElement != null ? todayDate + chegadaElement.asNormalizedText() : null;
            try {
                flight.setDeparture(format.parse(saida));
                flight.setArrival(format.parse(chegada));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            flight.setDirect("direto".equalsIgnoreCase(alt[i].asNormalizedText()));





            flight.setCompanhia(flightElements.get(i).getAltAttribute());

            flight.setPrice(Double.parseDouble(precoElements.get(i).asNormalizedText().replace(",", ".").replace(" €", "")));

            System.out.println(flight);
        }

        webClient.close();
    }
}

