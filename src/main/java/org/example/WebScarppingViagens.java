package org.example;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.apache.http.util.Args;

import java.io.IOException;
import java.util.List;

public class WebScarppingViagens {
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



        List<HtmlElement> flights = page.getByXPath("//div[contains(@class, 'vmXl vmXl-mod-variant-large')]");

        // Para cada voo diz a hora de partida e chegada
        for (HtmlElement flight : flights ) {
            HtmlImage airlineImage = page.getFirstByXPath(".//img[contains(@src, 'provider-logos') and @alt]");
            String companhia = airlineImage.getAltAttribute();

            // A partir do span obter a hora de chegada e saida
            HtmlElement saidaElement = flight.getFirstByXPath(".//span[1]");
            HtmlElement chegadaElement = flight.getFirstByXPath(".//span[3]");

            String saida = saidaElement != null ? saidaElement.asNormalizedText() : "Hora de partida não encontrada";
            String chegada = chegadaElement != null ? chegadaElement.asNormalizedText() : "Hora de chegada não encontrada";


            HtmlElement diretoOuNaoElement = flight.getFirstByXPath("../following-sibling::div[contains(@class, 'vmXl vmXl-mod-variant-default')]//span[contains(@class, 'JWEO-stops-text')]");
            String diretoOuNaoText = diretoOuNaoElement != null ? diretoOuNaoElement.asNormalizedText() : "Informação de escalas não encontrada";

            System.out.println("Hora de partida: " + saida + ", Hora de chegada: " + chegada);
            System.out.println("Companhia aérea: " + companhia);
            System.out.println("O voo é: " + diretoOuNaoText);


        }

            webClient.close(); // Fecha o WebClient após o uso
    }
}
