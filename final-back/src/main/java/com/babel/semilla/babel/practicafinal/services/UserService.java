package com.babel.semilla.babel.practicafinal.services;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.babel.semilla.babel.practicafinal.dtos.PaymentMethodDTO;
import com.babel.semilla.babel.practicafinal.dtos.UserDTO;
import com.babel.semilla.babel.practicafinal.dtos.UserLoginDTO;
import com.babel.semilla.babel.practicafinal.entities.Coin;
import com.babel.semilla.babel.practicafinal.entities.Transaction;
import com.babel.semilla.babel.practicafinal.entities.TransactionType;
import com.babel.semilla.babel.practicafinal.entities.User;
import com.babel.semilla.babel.practicafinal.entities.UserCoin;
import com.babel.semilla.babel.practicafinal.repositories.CoinRepository;
import com.babel.semilla.babel.practicafinal.repositories.TransactionRepository;
import com.babel.semilla.babel.practicafinal.repositories.UserCoinRepository;
import com.babel.semilla.babel.practicafinal.repositories.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


@Service
public class UserService {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CoinRepository coinRepository;
	
	@Autowired
	private UserCoinRepository userCoinRepository;
	
	@Autowired
	private TransactionRepository transactionRepository;
	
	@Autowired
	private ModelMapper modelMapper;
	
	
	
	
	public UserDTO login(UserLoginDTO login) {
		User userLocal = userRepository.findByEmail(login.getEmail());
	        
	    if (userLocal == null || !userLocal.getPassword().equals(login.getPassword())) {
	    	throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas");
	    }
	    return modelMapper.map(userLocal,UserDTO.class);
	}
	
	
	
	
	
	//crear Usuario
	public  UserDTO register(UserDTO userDTO) {
		User user =modelMapper.map(userDTO,User.class);
		
		User userLocal = userRepository.findByEmail(user.getEmail());
    	
    	if(userLocal != null){
            System.out.println("El usuario ya existe");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("El usuario %s ya existe ", user.getEmail()));
        }
    	if (userDTO.getEmail() == null || userDTO.getEmail().isEmpty()) {
    		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("algo terrible ha occurido"));
        }
		
		
		
		User createdUser = userRepository.save(user);
		return modelMapper.map(createdUser,UserDTO.class);
	}
	
	public UserDTO getUserByEmail(String email) {
		User userLocal = userRepository.findByEmail(email);
		if(userLocal == null){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("El usuario con el email %s no se ha encontrado", email));
		}
		
		return modelMapper.map(userLocal,UserDTO.class);
	}
	
	public List<UserDTO> getUsers(){
		 List<User> users = userRepository.findAll();
	        return users.stream()
	                .map(user -> modelMapper.map(user, UserDTO.class))
	                .collect(Collectors.toList());
	}





	public UserDTO updatePassword(String email, String newPassword) {
		User userLocal = userRepository.findByEmail(email);
		if(userLocal == null){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("El usuario con el email %s no se ha encontrado",email));
		}
		userLocal.setPassword(newPassword);
		User updatedUser = userRepository.save(userLocal);
		return modelMapper.map(updatedUser,UserDTO.class);
	}





	public UserDTO updatePaymentMethod(String email, PaymentMethodDTO newPaymantMethod) {
		User userLocal = userRepository.findByEmail(email);
		if(userLocal == null ){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("El usuario con el email %s no se ha encontrado",email));
		}
		else if(newPaymantMethod == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("algo terrible ha occurido"));
		}
		userLocal.setPaymentMethod(newPaymantMethod.getPaymentMethod());
		userLocal.setNumsTarjeta(newPaymantMethod.getNumsTarjeta());
		userLocal.setDateOfExpiry(newPaymantMethod.getDateOfExpiry());
		userLocal.setCvv(newPaymantMethod.getCvv());
		userLocal.setIban(newPaymantMethod.getIban());
		
		User updatedUser = userRepository.save(userLocal);
		return modelMapper.map(updatedUser,UserDTO.class);
	}
	
	
	public UserDTO depositMoneyInEuros(String userEmail, float amount) {
        User user = userRepository.findByEmail(userEmail);
        //System.out.println(user);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("El usuario con el email %s no se ha encontrado", userEmail));
        }

        if (amount <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Monto no válido para depósito");
        }

        // Buscar la moneda EURO en la lista de monedas del usuario
        Coin euroCoin = coinRepository.findByName("EURO")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Moneda de euro no encontrada"));
        System.out.println(euroCoin);
        // Verificar si ya existe una entrada de la moneda EURO en la lista del usuario
        UserCoin euroUserCoin = user.getUserCoins().stream()
                .filter(uc -> uc.getCoin().equals(euroCoin))
                .findFirst()
                .orElse(null);
        
        if (euroUserCoin == null) {
            // Si no existe, crear una nueva entrada para la moneda EURO con la cantidad ingresada
            euroUserCoin = new UserCoin();
            euroUserCoin.setUser(user);
            euroUserCoin.setCoin(euroCoin);
            euroUserCoin.setQuantity(amount);
            UserCoin createdEuroUserCoin =userCoinRepository.save(euroUserCoin);
            System.out.println(createdEuroUserCoin);
            user.getUserCoins().add(createdEuroUserCoin);
            //System.out.println(user);
        } else {
            // Si ya existe, aumentar la cantidad
            euroUserCoin.setQuantity(euroUserCoin.getQuantity() + amount);
        }

        User updatedUser = userRepository.save(user);
        
        
        // Crear y guardar la transacción de depósito
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setCoin(euroCoin);
        transaction.setDate(new Date());
        transaction.setAmount(amount);
        transaction.setType(TransactionType.INGRESO_EUROS);

        transactionRepository.save(transaction);

        return modelMapper.map(updatedUser, UserDTO.class);
    }
	
	public UserDTO withdrawMoneyInEuros(String userEmail, float amount) {
	    User user = userRepository.findByEmail(userEmail);

	    if (user == null) {
	        throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("El usuario con el email %s no se ha encontrado", userEmail));
	    }

	    if (amount <= 0) {
	        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Monto no válido para retiro");
	    }
	    Coin euroCoin = coinRepository.findByName("EURO")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Moneda de euro no encontrada"));
	    // Busca la moneda EURO en la lista de monedas del usuario
	    Optional<UserCoin> euroUserCoinOptional = user.getUserCoins().stream()
	            .filter(uc -> uc.getCoin().equals(euroCoin))
	            .findFirst();

	    if (euroUserCoinOptional.isPresent()) {
	        UserCoin euroUserCoin = euroUserCoinOptional.get();

	        // Verifica si el usuario tiene suficiente saldo en euros
	        if (euroUserCoin.getQuantity() >= amount) {
	            // Realiza la operación de retiro
	            euroUserCoin.setQuantity(euroUserCoin.getQuantity() - amount);

	            // Si el saldo de euros llega a cero, puedes eliminar la entrada de la tabla user_coin
	            if (euroUserCoin.getQuantity() == 0) {
	                userCoinRepository.delete(euroUserCoin);
	                user.getUserCoins().remove(euroUserCoin);
	            }

	            User updatedUser = userRepository.save(user);
	            
	            // Crear y guardar la transacción de retiro
	            Transaction transaction = new Transaction();
	            transaction.setUser(user);
	            transaction.setCoin(euroCoin);
	            transaction.setDate(new Date());
	            transaction.setAmount(amount);
	            transaction.setType(TransactionType.RETIRO_EUROS);

	            transactionRepository.save(transaction);
	            
	            return modelMapper.map(updatedUser, UserDTO.class);
	        } else {
	            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Saldo insuficiente en euros");
	        }
	    } else {
	        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El usuario no tiene euros");
	    }
	}
	
	public UserDTO buyCryptocurrency(String userEmail, String cryptocurrencyName, float amountInEuros) {
	    User user = userRepository.findByEmail(userEmail);

	    if (user == null) {
	        throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("El usuario con el email %s no se ha encontrado", userEmail));
	    }

	    if (amountInEuros <= 0) {
	        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Monto no válido para la compra");
	    }

	    // Obtener la criptomoneda deseada por nombre
	    Coin cryptocurrency = coinRepository.findByName(cryptocurrencyName)
	            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Criptomoneda no encontrada "));

	    // Verificar si el usuario tiene suficiente saldo en euros
	    Optional<UserCoin> euroUserCoinOptional = user.getUserCoins().stream()
	            .filter(uc -> uc.getCoin().getName().equals("EURO"))
	            .findFirst();

	    if (euroUserCoinOptional.isPresent()) {
	        UserCoin euroUserCoin = euroUserCoinOptional.get();

	        if (euroUserCoin.getQuantity() >= amountInEuros) {
	            // Calcular la cantidad de la criptomoneda que el usuario puede comprar
	            float cryptocurrencyPriceInEuros = getCryptocurrencyPriceInEuros(cryptocurrency.getSymbol());
	            float cryptocurrencyAmount = amountInEuros / cryptocurrencyPriceInEuros;

	            // Verificar si el usuario ya posee la criptomoneda
	            Optional<UserCoin> cryptocurrencyUserCoinOptional = user.getUserCoins().stream()
	                    .filter(uc -> uc.getCoin().equals(cryptocurrency))
	                    .findFirst();

	            if (cryptocurrencyUserCoinOptional.isPresent()) {
	                UserCoin cryptocurrencyUserCoin = cryptocurrencyUserCoinOptional.get();
	                cryptocurrencyUserCoin.setQuantity(cryptocurrencyUserCoin.getQuantity() + cryptocurrencyAmount);
	            } else {
	                UserCoin cryptocurrencyUserCoin = new UserCoin();
	                cryptocurrencyUserCoin.setUser(user);
	                cryptocurrencyUserCoin.setCoin(cryptocurrency);
	                cryptocurrencyUserCoin.setQuantity(cryptocurrencyAmount);
	                UserCoin createdCryptocurrencyUserCoin =userCoinRepository.save(cryptocurrencyUserCoin);
	                user.getUserCoins().add(createdCryptocurrencyUserCoin);
	            }

	            // Actualizar el saldo de euros del usuario
	            euroUserCoin.setQuantity(euroUserCoin.getQuantity() - amountInEuros);

	            // Crear y guardar la transacción de compra
	            Transaction transaction = new Transaction();
	            transaction.setUser(user);
	            transaction.setCoin(cryptocurrency);
	            transaction.setDate(new Date());
	            transaction.setAmount(cryptocurrencyAmount);
	            transaction.setType(TransactionType.COMPRA);

	            transactionRepository.save(transaction);
	            userRepository.save(user);

	            return modelMapper.map(user, UserDTO.class);
	        } else {
	            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Saldo insuficiente en euros");
	        }
	    } else {
	        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El usuario no tiene euros");
	    }
	}

	public UserDTO sellCryptocurrency(String userEmail, String cryptocurrencyName, float amountInCryptocurrency) {
	    User user = userRepository.findByEmail(userEmail);

	    if (user == null) {
	        throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("El usuario con el email %s no se ha encontrado", userEmail));
	    }

	    if (amountInCryptocurrency <= 0) {
	        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Monto no válido para la venta");
	    }

	    // Obtener la criptomoneda deseada por nombre
	    Coin cryptocurrency = coinRepository.findByName(cryptocurrencyName)
	            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Criptomoneda no encontrada "));

	    // Verificar si el usuario posee suficiente saldo de la criptomoneda
	    Optional<UserCoin> cryptocurrencyUserCoinOptional = user.getUserCoins().stream()
	            .filter(uc -> uc.getCoin().equals(cryptocurrency))
	            .findFirst();

	    if (cryptocurrencyUserCoinOptional.isPresent()) {
	        UserCoin cryptocurrencyUserCoin = cryptocurrencyUserCoinOptional.get();

	        if (cryptocurrencyUserCoin.getQuantity() >= amountInCryptocurrency) {
	            // Calcular el valor en euros de la cantidad de criptomoneda a vender
	            float cryptocurrencyPriceInEuros = getCryptocurrencyPriceInEuros(cryptocurrency.getSymbol());
	            float amountInEuros = amountInCryptocurrency * cryptocurrencyPriceInEuros;

	            // Verificar si el usuario ya posee euros
	            Optional<UserCoin> euroUserCoinOptional = user.getUserCoins().stream()
	                    .filter(uc -> uc.getCoin().getName().equals("EURO"))
	                    .findFirst();

	            if (euroUserCoinOptional.isPresent()) {
	                UserCoin euroUserCoin = euroUserCoinOptional.get();
	                euroUserCoin.setQuantity(euroUserCoin.getQuantity() + amountInEuros);
	            } else {
	                UserCoin euroUserCoin = new UserCoin();
	                euroUserCoin.setUser(user);
	                euroUserCoin.setCoin(coinRepository.findByName("EURO").orElse(null));
	                euroUserCoin.setQuantity(amountInEuros);
	                user.getUserCoins().add(euroUserCoin);
	            }

	            // Actualizar la cantidad de la criptomoneda del usuario
	            cryptocurrencyUserCoin.setQuantity(cryptocurrencyUserCoin.getQuantity() - amountInCryptocurrency);

	            // Crear y guardar la transacción de venta
	            Transaction transaction = new Transaction();
	            transaction.setUser(user);
	            transaction.setCoin(cryptocurrency);
	            transaction.setDate(new Date());
	            transaction.setAmount(amountInCryptocurrency);
	            transaction.setType(TransactionType.VENTA);

	            transactionRepository.save(transaction);
	            userRepository.save(user);

	            return modelMapper.map(user, UserDTO.class);
	        } else {
	            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Saldo insuficiente en Criptomoneda");
	        }
	    } else {
	        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El usuario no tiene la criptomoneda especificada");
	    }
	}

    
    private float getCryptocurrencyPriceInEuros(String cryptocurrencySymbol) {
        String apiUrl = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest?start=1&limit=5";
        String apiKey = "22d693e6-b0d0-493f-8a00-7ad60b25cfa3";

        try {
            HttpClient httpClient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(apiUrl);
            httpGet.addHeader("X-CMC_PRO_API_KEY", apiKey);

            HttpResponse response = httpClient.execute(httpGet);
            String responseBody = EntityUtils.toString(response.getEntity());

            // Parsear el JSON de respuesta
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(responseBody);

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
            throw new RuntimeException("Criptomoneda no encontrada en la respuesta.");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Se produjo un error al obtener el precio de la criptomoneda en euros.");
        }
    }

}