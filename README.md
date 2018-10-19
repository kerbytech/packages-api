# packages-api
Simple API for CRUD access of packages

<img width="1909" alt="swagger-ui" src="https://user-images.githubusercontent.com/567384/47230672-c494c380-d3c2-11e8-97a7-1b5c74425694.png">

## Overview
A Java REST API written with the Spring Boot Framework and embedded H2 database to enable CRUD access of Packages. The price of a Package can be requested in any of the currencies supported by Fixer API (exposed by the /currency endpoint).

A Package takes the following form: 
 - name
 - description
 - price (Default USD)
 - products [
    - name
    - description
    - usdPrice
 ]

**API documentation (and interaction) can be performed via the auto-documented Swagger UI.**


## Deployment

The REST API can be deployed through Maven or as a Java Application. 
A `fixer.api.key` is required to connect to the Fixer API. This is best injected as a runtime parameter.

### Run in Maven
1. Build using maven `mvn clean install`
2. Launch service `mvn spring-boot:run -Dspring-boot.run.arguments=--fixer.api.key=REPLACE_WITH_KEY`

### Run as Java Application
1. Import into IDE
2. Build using maven `mvn clean install`
3. Run api/com.kerby.example.api.Application main with Program Arguments `--fixer.api.key="REPLACE_WITH_KEY"`

Once deployed API documentation can be found at: **localhost:8080/api/swagger-ui.html**

## Tests

There are jUnit tests covering all components. 

Execute using: `mvn tests`
