package com.babel.semilla.babel.practicafinal.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.babel.semilla.babel.practicafinal.dtos.PaymentMethodDTO;
import com.babel.semilla.babel.practicafinal.dtos.UserDTO;
import com.babel.semilla.babel.practicafinal.dtos.UserLoginDTO;
import com.babel.semilla.babel.practicafinal.services.UserService;


@RestController
@RequestMapping("/user")
public class UserController {
	@Autowired
    private UserService userService;
	
	
	@PostMapping("/auth/login")
	public ResponseEntity<UserDTO> login(@RequestBody UserLoginDTO request)
	{
		return ResponseEntity.ok(userService.login(request));
	}
   
	@PostMapping("/auth/register")
	public ResponseEntity<UserDTO> register(@RequestBody UserDTO request)
	{
		return ResponseEntity.ok(userService.register(request));
	}
	
	@GetMapping("/{email}")
	public ResponseEntity<UserDTO> getUserByEmail(@PathVariable String email)
	{
		return ResponseEntity.ok(userService.getUserByEmail(email));
	}
	
	@PutMapping("/{email}/updatepassword")
	public ResponseEntity<UserDTO> updatePassword(@PathVariable String email,@RequestBody String  newPassword)
	{
		return ResponseEntity.ok(userService.updatePassword(email,newPassword));
	}
	@PutMapping("/{email}/updatepaymentmethod")
	public ResponseEntity<UserDTO> updatePaymentMethod(@PathVariable String email,@RequestBody PaymentMethodDTO  newPaymantMethod)
	{
		return ResponseEntity.ok(userService.updatePaymentMethod(email,newPaymantMethod));
	}
	
	@PostMapping("/depositeMoney")
	public ResponseEntity<UserDTO> depositMoneyInEuros(@RequestParam(value = "email", required = true) String email,
														@RequestParam(value = "amount", required = true) float amount)
	{
		return ResponseEntity.ok(userService.depositMoneyInEuros(email,amount));
	}
	@PostMapping("/withdrawMoney")
	public ResponseEntity<UserDTO> withdrawMoneyInEuros(@RequestParam(value = "email", required = true) String email,
														@RequestParam(value = "amount", required = true) float amount)
	{
		return ResponseEntity.ok(userService.withdrawMoneyInEuros(email,amount));
	}
	
	@PostMapping("/buyCryptocurrency")
	public ResponseEntity<UserDTO> buyCryptocurrency(@RequestParam(value = "email", required = true) String email,
														@RequestParam(value = "criptoName", required = true) String criptoName,
														@RequestParam(value = "amount", required = true) float amount)
	{
		return ResponseEntity.ok(userService.buyCryptocurrency(email,criptoName,amount));
	}
	@PostMapping("/sellCryptocurrency")
	public ResponseEntity<UserDTO> sellCryptocurrency(@RequestParam(value = "email", required = true) String email,
														@RequestParam(value = "criptoName", required = true) String criptoName,
														@RequestParam(value = "amount", required = true) float amount)
	{
		return ResponseEntity.ok(userService.sellCryptocurrency(email,criptoName,amount));
	}
	
	@GetMapping("/users")
	public ResponseEntity<List<UserDTO>> getUsers()
	{
	return ResponseEntity.ok(userService.getUsers());   
	}
	


    
	
	
}
