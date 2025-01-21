# Web Scraping Project

## Configuração do Projeto

### Requisitos Iniciais
1. **Identificar a versão do Chrome instalada:**
    - Aceder a `chrome://settings/help`.
    - Verificar a versão do navegador, por exemplo: `Versão 131.0.6778.109 (Compilação oficial) (64 bits)`.

2. **Obter o Chrome Driver:**
    - Fazer download em: [ChromeDriver Downloads](https://developer.chrome.com/docs/chromedriver/downloads?hl=pt-br).
    - Caso a versão do Chrome Driver não esteja listada, aceder a [Chrome for Testing](https://googlechromelabs.github.io/chrome-for-testing/).
    - Baixar o driver correspondente, por exemplo:
        - `chromedriver-win64`: [Download Link](https://storage.googleapis.com/chrome-for-testing-public/131.0.6778.108/win64/chromedriver-win64.zip).
    - Extrair o arquivo e guardar numa pasta do disco.

3. **Configurar o caminho do Chrome Driver no projeto:**
    - Editar a variável `CHROME_DRIVER_PATH` na classe `OptimizedWebScrapping.java` com o caminho completo, incluindo o executável:
      ```java
      private static final String CHROME_DRIVER_PATH = "D:\\Tools\\chromedriver-win64\\chromedriver.exe";
      ```

### Configuração da Base de Dados
4. **Configurar o MariaDB:**
    - O projeto usa MariaDB (MySQL fork) na porta `3306`, com utilizador `root` e sem password.
    - Executar o script SQL localizado em: `web_scrapping/src/main/resources/database.sql`.

    - **Alterar configurações da base de dados (opcional):**
        - Modificar as seguintes tags XML:
            - `web_scrapping/src/main/resources/hibernate-cfg.xml`:
              ```xml
              <property name="hibernate.connection.url">jdbc:mysql://localhost:3306/pdi_flight</property>
              <property name="hibernate.connection.username">root</property>
              <property name="hibernate.connection.password"/>
              ```
            - `web_scrapping/src/main/resources/META-INF/persistence.xml`:
              ```xml
              <property name="jakarta.persistence.jdbc.url" value="jdbc:mysql://localhost:3306/pdi_flight"/>
              <property name="jakarta.persistence.jdbc.user" value="root"/>
              <property name="jakarta.persistence.jdbc.password"/>
              ```

### Considerações Adicionais
5. **Erros conhecidos:**
    - Ignorar erros relacionados a dependências opcionais, como o OpenTelemetry:
      ```
      SEVERE: Error automatically configuring OpenTelemetry SDK. OpenTelemetry will not be enabled.
      ```

6. **Classes deprecadas:**
    - As classes `WebScrapping` e `WebScrappingFilters` são versões antigas e estão marcadas como deprecadas por serem mais lentas (~7x mais que a versão otimizada).
    - Mantêm-se no projeto para documentar a evolução do mesmo.

### Informações Técnicas
7. **Linguagem:** Java 21.

8. **Classe principal:**
    - `OptimizedWebScrapping`:
        - Caminho: `src/main/java/gvv/WebScrappingOptimized/OptimizedWebScrapping.java`.

9. **Carregar dependências:**
    - Abrir o arquivo `pom.xml` e executar `Reload Project` para assegurar que as dependências estão atualizadas.

10. **Editor recomendado:** IntelliJ IDEA.
