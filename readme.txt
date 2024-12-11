Configurar o Projeto:

1. Identificar a versão do seu browser (Chrome)
	a. Aceder a chrome://settings/help
	b. Detectar a versão, por exemplo, Versão 131.0.6778.109 (Compilação oficial) (64 bits)
2. Aceder a https://developer.chrome.com/docs/chromedriver/downloads?hl=pt-br para fazer o download do Chrome Driver
	a. Em caso de não aparecer a versão listada, aceder a https://googlechromelabs.github.io/chrome-for-testing/ pois a mesma pode ser de testes.
	b. Fazer download do chromedriver:
		i. chromedriver	win64	https://storage.googleapis.com/chrome-for-testing-public/131.0.6778.108/win64/chromedriver-win64.zip	200
	c. Extrair e guardar em uma pasta do disco.
3. Aceder ao projeto, na classe OptimizedWebScrapping.java, e na variável CHROME_DRIVEER_PATH atualizar com o caminho para o driver (e inclusive): "D:\\Tools\\chromedriver-win64\\chromedriver.exe"

4. O projeto está a executar MariaDB (MySQL fork), dito isso, será necessário configurar na porta 3306, de password nula, e utilizador root e executar o script, localizado no projeto em web_scrapping/src/main/resources/database.sql
	a. Caso pretenta alterar os valores de conexões, deverá aceder aos respetivos ficheiros listados de seguida, e alterar os respetivos valores das tags XML:
		i. web_scrapping/src/main/resources/hibernate-cfg.xml (hibernate.connection.url, hibernate.connection.username, hibernate.connection.password).
		ii. web_scrapping/src/main/resources/META-INF/persistence.xml (jakarta.persistence.jdbc.url, jakarta.persistence.jdbc.user, jakarta.persistence.jdbc.password).

5. O projeto haverá de soltar alguns erros, erros estes que devem ser ignorados:
	1. Dependências opcionais:
		i. O erro que se segue da-se ao facto de não pretendermos usar o OpenTelemetry apesar de estarmos a usar um driver que usa-o para certas funcionalidades das quais não usufruimos
			io.opentelemetry.api.GlobalOpenTelemetry maybeAutoConfigureAndSetGlobal
				SEVERE: Error automatically configuring OpenTelemetry SDK. OpenTelemetry will not be enabled.
				io.opentelemetry.sdk.autoconfigure.spi.ConfigurationException: OTLP gRPC Metrics Exporter enabled but opentelemetry-exporter-otlp not found on classpath. Make sure to add it as a dependency to enable this feature.

6. As classes "WebScrapping" e "WebScrappingFilters" são a 1ª versão do nosso projeto, a qual originou a versão OptimizedWebScrapping. 
   As mesmas foram declaradas como deprecadas pois tomam imenso tempo no scrapping (Cerca de 7 vezes mais que a versão Otimizada).
   Mantém-se no projeto para demonstrar a evolução ao longo do projeto, um esforço de melhoria após alcançar o resultado.

7. A linguagem usada é Java, 21.

8. A classe main é OptimizedWebScrapping.java (src/main/java/gvv/WebScrappingOptimized/OptimizedWebScrapping.java)

9. Antes de executar deve aceder ao pom.xml e executar Reload Project para carregar as dependências.

10. O editor recomendado é Intellij Idea.