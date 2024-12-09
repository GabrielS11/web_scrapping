-- MariaDB (MYSQL)
-- Criar a base de dados
CREATE DATABASE IF NOT EXISTS PDI_FLIGHT;
USE PDI_FLIGHT;

-- Tabela: COMPANIES
CREATE TABLE COMPANIES (
                           ID INT(6) AUTO_INCREMENT PRIMARY KEY,
                           NAME VARCHAR(400) NOT NULL
);

-- Tabela: AIRPLANES
CREATE TABLE AIRPLANES (
                           ID INT(6) AUTO_INCREMENT PRIMARY KEY,
                           CODE VARCHAR(400) NOT NULL,
                           COMPANY_FK INT(6) NOT NULL,
                           FOREIGN KEY (COMPANY_FK) REFERENCES COMPANIES(ID)
);

-- Tabela: COUNTRIES
CREATE TABLE COUNTRIES (
                           ID INT(6) AUTO_INCREMENT PRIMARY KEY,
                           CODE VARCHAR(80) NOT NULL,
                           DESCRIPTION VARCHAR(300) NOT NULL
);

-- Tabela: CITIES
CREATE TABLE CITIES (
                        ID INT(6) AUTO_INCREMENT PRIMARY KEY,
                        COUNTRY_FK INT(6) NOT NULL,
                        CODE VARCHAR(80) NOT NULL,
                        DESCRIPTION VARCHAR(300) NOT NULL,
                        FOREIGN KEY (COUNTRY_FK) REFERENCES COUNTRIES(ID)
);

-- Tabela: AIRPORTS
CREATE TABLE AIRPORTS (
                          ID INT(6) AUTO_INCREMENT PRIMARY KEY,
                          CITY_FK INT(6) NOT NULL,
                          CODE VARCHAR(80) NOT NULL,
                          DESCRIPTION VARCHAR(300) NOT NULL,
                          FOREIGN KEY (CITY_FK) REFERENCES CITIES(ID)
);

-- Tabela: TRIP
CREATE TABLE TRIP (
                      ID INT(6) AUTO_INCREMENT PRIMARY KEY,
                      FLIGHT_TYPE VARCHAR(50) NOT NULL CHECK (FLIGHT_TYPE IN ('ROUNDTRIP', 'ONEWAY')),
                      RETRIEVED_DATE DATETIME NOT NULL DEFAULT NOW(),
                      FLIGHT_CHOICE INT(6) NOT NULL check ( FLIGHT_CHOICE in ('ECONOMY', 'PREMIUM_ECONOMY', 'BUSINESS', 'FIRST')),
                      DEPARTURE_DATE DATETIME NOT NULL,
                      ARRIVAL_DATE DATETIME NOT NULL,
                      DEPARTURE_AIRPORT INT(6) NOT NULL,
                      ARRIVAL_AIRPORT INT(6) NOT NULL,
                      FOREIGN KEY (DEPARTURE_AIRPORT) REFERENCES AIRPORTS(ID),
                      FOREIGN KEY (ARRIVAL_AIRPORT) REFERENCES AIRPORTS(ID)
);

-- Tabela: FLIGHT
CREATE TABLE FLIGHT (
                        ID INT(6) AUTO_INCREMENT PRIMARY KEY,
                        NAME VARCHAR(400) NOT NULL,
                        AIRPLANE_FK INT(6) NOT NULL,
                        DIRECTION_TYPE VARCHAR(50) NOT NULL CHECK (DIRECTION_TYPE IN ('DEPARTURE', 'RETURN')),
                        DEPARTURE_DATE DATETIME NOT NULL,
                        ARRIVAL_DATE DATETIME NOT NULL,
                        DEPARTURE_AIRPORT INT(6) NOT NULL,
                        ARRIVAL_AIRPORT INT(6) NOT NULL,
                        IS_DIRECT VARCHAR(1) NOT NULL DEFAULT 'Y' CHECK (IS_DIRECT IN ('Y', 'N')),
                        TRIP_FK INT(6) NOT NULL,
                        FOREIGN KEY (AIRPLANE_FK) REFERENCES AIRPLANES(ID),
                        FOREIGN KEY (DEPARTURE_AIRPORT) REFERENCES AIRPORTS(ID),
                        FOREIGN KEY (ARRIVAL_AIRPORT) REFERENCES AIRPORTS(ID),
                        FOREIGN KEY (TRIP_FK) REFERENCES TRIP(ID)
);

-- Tabela: FLIGHT_STOPS
CREATE TABLE FLIGHT_STOPS (
                              ID INT(6) AUTO_INCREMENT PRIMARY KEY,
                              NAME VARCHAR(400) NOT NULL,
                              AIRPLANE_FK INT(6) NOT NULL,
                              DEPARTURE_DATE DATETIME NOT NULL,
                              ARRIVAL_DATE DATETIME NOT NULL,
                              DEPARTURE_AIRPORT INT(6) NOT NULL,
                              ARRIVAL_AIRPORT INT(6) NOT NULL,
                              FOREIGN KEY (AIRPLANE_FK) REFERENCES AIRPLANES(ID),
                              FOREIGN KEY (DEPARTURE_AIRPORT) REFERENCES AIRPORTS(ID),
                              FOREIGN KEY (ARRIVAL_AIRPORT) REFERENCES AIRPORTS(ID)
);
