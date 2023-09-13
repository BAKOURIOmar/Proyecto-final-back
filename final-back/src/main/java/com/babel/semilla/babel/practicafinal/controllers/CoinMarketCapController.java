package com.babel.semilla.babel.practicafinal.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Optional;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.babel.semilla.babel.practicafinal.entities.Coin;
import com.babel.semilla.babel.practicafinal.repositories.CoinRepository;

@RestController
@RequestMapping("/api/coinmarketcap")
public class CoinMarketCapController {

    @Autowired
    private CoinRepository coinRepository;

    @GetMapping("/latest/{id}")
    public String getLatestCryptocurrencyData(@PathVariable Integer id) {
        String apiUrl = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest?start=1&limit=5";
        String apiKey = "22d693e6-b0d0-493f-8a00-7ad60b25cfa3";

        try {
            HttpClient httpClient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(apiUrl);
            httpGet.addHeader("X-CMC_PRO_API_KEY", apiKey);

            HttpResponse response = httpClient.execute(httpGet);
            String responseBody = EntityUtils.toString(response.getEntity());
            
            inicCoins(responseBody);
            

           
            return responseBody;
        } catch (Exception e) {
            e.printStackTrace();
            return "Error occurred while fetching data from CoinMarketCap API.";
        }
    }
    
    private void inicCoins(String responseBody) throws JsonMappingException, JsonProcessingException {
    	
    	 
    	ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(responseBody);
        JsonNode dataNode = rootNode.get("data");
    	
    	 for (JsonNode coinNode : dataNode) {
             String name = coinNode.get("name").asText();
             String symbol = coinNode.get("symbol").asText();

             // Verifica si ya existe una entrada en la tabla coin con el mismo nombre
             Optional<Coin> existingCoin = coinRepository.findByName(name);

             if (!existingCoin.isPresent()) {
                 // Crea una nueva entrada si no existe
                 Coin newCoin = new Coin();
                 newCoin.setName(name);
                 newCoin.setSymbol(symbol);
                 // Configura otros campos según sea necesario
                 System.out.println(newCoin);
                 coinRepository.save(newCoin);
             }
         }

         // Agregar manualmente la entrada para el Euro si aún no existe
         Optional<Coin> euroCoin = coinRepository.findByName("EURO");

         if (!euroCoin.isPresent()) {
             Coin newEuroCoin = new Coin();
             newEuroCoin.setName("EURO");
             newEuroCoin.setSymbol("EUR");
             // Configura otros campos según sea necesario
             coinRepository.save(newEuroCoin);
         }
    	
    }
}
