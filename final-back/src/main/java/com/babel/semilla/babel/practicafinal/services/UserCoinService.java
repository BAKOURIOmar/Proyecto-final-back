package com.babel.semilla.babel.practicafinal.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.babel.semilla.babel.practicafinal.dtos.CoinDTO;
import com.babel.semilla.babel.practicafinal.dtos.UserCoinDTO;
import com.babel.semilla.babel.practicafinal.dtos.UserDTO;
import com.babel.semilla.babel.practicafinal.entities.Coin;
import com.babel.semilla.babel.practicafinal.entities.User;
import com.babel.semilla.babel.practicafinal.entities.UserCoin;
import com.babel.semilla.babel.practicafinal.repositories.CoinRepository;
import com.babel.semilla.babel.practicafinal.repositories.UserCoinRepository;
import com.babel.semilla.babel.practicafinal.repositories.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class UserCoinService {
	@Autowired
	private UserCoinRepository userCoinRepository ;
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CoinRepository coinRepository;
	
	@Autowired
	private ModelMapper modelMapper;
	
	public List<UserCoinDTO>getUserCoinsByIdUser(Integer idUser){
		
		List<UserCoin> userCoins = userCoinRepository.findByUser_Id(idUser);
		
		List<UserCoinDTO> allCoinsDTOs =getAllUserCoinsFromCoinMarketCap(idUser);
		List<UserCoinDTO> userCoinsDTOs = userCoins.stream()
                .map(userCoin -> modelMapper.map(userCoin, UserCoinDTO.class))
                .collect(Collectors.toList());
		
		allCoinsDTOs.forEach(userCoinsDTOResp -> {
			 userCoinsDTOs.forEach(userCoinsDTO -> {
			     
				 if(userCoinsDTOResp.getCoin().getSymbol().equals(userCoinsDTO.getCoin().getSymbol()) ) {
				 userCoinsDTOResp.setQuantity(userCoinsDTO.getQuantity());
				 }
			    });
		       
		    });
		  return allCoinsDTOs;
	}
	
	private float getCryptocurrencyPriceInEuros(String cryptocurrencySymbol) {
      
		try {
            // Parsear el JSON de respuesta
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(getCoinMarketCapCriptos());

            // Buscar la criptomoneda por su símbolo
            JsonNode dataNode = rootNode.get("data");
            if (dataNode != null && dataNode.isArray()) {
                for (JsonNode cryptoNode : dataNode) {
                    String symbol = cryptoNode.get("symbol").asText();
                    if (cryptocurrencySymbol.equals(symbol)) {
                        // Encontramos la criptomoneda, obtener su precio en euros
                        JsonNode quoteNode = cryptoNode.get("quote");
                        if (quoteNode != null) {
                            JsonNode euroNode = quoteNode.get("USD");
                            if (euroNode != null) {
                                return euroNode.get("price").floatValue();
                            }
                        }
                    }
                }
            }

            // Si no se encontró la criptomoneda
            throw new RuntimeException("Cryptocurrency not found in the response.");
		 } catch (Exception e) {
	            e.printStackTrace();
	            throw new RuntimeException("Error occurred while fetching data from CoinMarketCap API.");
	        }
    }
	private String getCoinMarketCapCriptos() {
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
	            throw new RuntimeException("Se produjo un error al obtener el precio de la criptomoneda en euros.");
	        }
	        
	}

	private List<UserCoinDTO> getAllUserCoinsFromCoinMarketCap(Integer idUser) {
    // Obtener el usuario por su ID
    User user = userRepository.findById(idUser)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + idUser));


    try {
       

        // Analizar el JSON de respuesta
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(getCoinMarketCapCriptos());

        List<UserCoinDTO> userCoinsDTOs = new ArrayList<>();
        
     // Mapear User a UserDTO
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);
        
        Coin euroCoin =coinRepository.findBySymbol("EUR")
                .orElseThrow(() -> new RuntimeException("Coin not found with symbol: EUR"));
        
        CoinDTO euroCoinDTO =modelMapper.map(euroCoin, CoinDTO.class);
        
        UserCoinDTO euroUserCoin = new UserCoinDTO();
        euroUserCoin.setUser(userDTO);
        euroUserCoin.setCoin(euroCoinDTO);
        userCoinsDTOs.add(euroUserCoin);

        if (rootNode != null && rootNode.get("data") != null && rootNode.get("data").isArray()) {
            for (JsonNode cryptoNode : rootNode.get("data")) {
                String symbol = cryptoNode.get("symbol").asText();

                // Obtener la moneda por su símbolo desde la base de datos
                Coin coin = coinRepository.findBySymbol(symbol)
                        .orElseThrow(() -> new RuntimeException("Coin not found with symbol: " + symbol));
                
             // Mapear Coin a CoinDTO
                CoinDTO coinDTO = modelMapper.map(coin, CoinDTO.class);
             
                
                

                UserCoinDTO userCoinDTO = new UserCoinDTO();
                userCoinDTO.setUser(userDTO);
                userCoinDTO.setCoin(coinDTO);
                userCoinDTO.setMarketPrice(getCryptocurrencyPriceInEuros(symbol));
                
                
                userCoinsDTOs.add(userCoinDTO);
                
                
            }
        }
       
        return userCoinsDTOs;
    } catch (Exception e) {
        e.printStackTrace();
        throw new RuntimeException("Error occurred while fetching cryptocurrency data from CoinMarketCap.");
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
