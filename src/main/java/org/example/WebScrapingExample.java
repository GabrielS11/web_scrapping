package org.example;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;


import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class WebScrapingExample {
    public static void main(String[] args) throws IOException {
        // URL da página que queremos fazer scraping
        WebClient webClient = null;
        for (int i = 1; i < 5; i++) {
            String url = "https://ispgaya.pt/pt/instituicao/corpo-docente?page=" + i;

            // Create a new WebClient instance
            webClient = new WebClient();
            webClient.getOptions().setCssEnabled(false);
            webClient.getOptions().setJavaScriptEnabled(false);

            HtmlPage page = webClient.getPage(url);

            //retira quantos professores estao por pagina
            HtmlElement[] products = page.getBody().getElementsByAttribute("div", "class", "transition ease-in-out duration-300 hover:-translate-y-1 col-span-6 sm:col-span-4 lg:col-span-3 xl:col-span-2").toArray(new HtmlElement[0]);


            /* Tambem retira os links do professores de uma maneira mais otimizada
            for(HtmlElement prof : products){
                String tUrl = prof.getElementsByTagName("a").getFirst().getAttribute("href");
                System.out.println(tUrl);
            }

             */

            //Retira os links de cada professor
            List<HtmlElement> profSection = page.getByXPath("//div[@class='transition ease-in-out duration-300 hover:-translate-y-1 col-span-6 sm:col-span-4 lg:col-span-3 xl:col-span-2']");

            System.out.println("Na pagina "+ i + " podemos encontrar os links dos professores: ");
            System.out.println("Nome; Grau; Categoria; Regime contratual; Ano de Entrada; Outras Habilitaçoes");





            System.out.println("Esta pagina contem " + products.length + " professores.");

        }
        webClient.close();
    }
}


/* Good try but ur bad
            for(HtmlElement section: profSection){
                List<HtmlAnchor> profLinks = section.getByXPath(".//a");

                for (HtmlAnchor link: profLinks){
                    String professorUrl = link.getHrefAttribute();
                    HtmlPage profPage = webClient.getPage(professorUrl);
                    //Retirar cada informaçao de cada prof (ver se da para fazer de uma maneira mais eficiente)
                        //nome
                    HtmlElement nameElement = profPage.getFirstByXPath("//p[@class='font-bold text-3xl']");
                    String name = nameElement != null ? nameElement.getTextContent().trim() : "Nome não encontrado";
                        //mestre
                    HtmlElement grauElement = profPage.getFirstByXPath("//p[@class='mt-1 font-semibold']");
                    String grau = grauElement != null ? grauElement.getTextContent().trim() : "Grau não encontrado";
                        //Categoria
                    HtmlElement categoriaElement = profPage.getFirstByXPath("//p[@class='mt-1 font-semibold md:max-w-[17ch] 2xl:max-w-none']");
                    String categoria = categoriaElement != null ? categoriaElement.getTextContent().trim() : "Categoria não encontrado";
                        //Regime contratual
                    HtmlElement regimeElement = profPage.getFirstByXPath("//p[@class='mt-1 font-semibold']");
                    String regime = regimeElement != null ? regimeElement.getTextContent().trim() : "Regime não encontrado";
                        //Ano de entrada
                    HtmlElement anoElement = profPage.getFirstByXPath("//p[@class='mt-1 font-semibold']");
                    String ano = anoElement != null ? anoElement.getTextContent().trim() : "Ano de entrada não encontrado";
                        //Outras habilitaçoes
                    HtmlElement habiElement = profPage.getFirstByXPath("//p[@class='mt-1 font-semibold md:max-w-[25ch] 2xl:max-w-none']");
                    String habi = habiElement != null ? habiElement.getTextContent().trim() : "Outras Habilições não encontrado";
                    System.out.println("    " + name + "; " + grau + "; " + categoria + "; " + regime + "; " + ano + "; " + habi);
                }
            }

             */
