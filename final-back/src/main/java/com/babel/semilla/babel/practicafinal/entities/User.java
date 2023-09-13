package com.babel.semilla.babel.practicafinal.entities;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="users")
public class User {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="firstName")
    private String firstName;

    @Column(name="lastName")
    private String lastName;


    @Column(name="email" , unique = true)
    private String email;
    
    @Column(name="password")
    private String password;

    @Column(name="country")
    private String country;

    @Column(name="city")
    private String city;

    @Column(name="direccion")
    private String direccion;

    @Column(name="paymentMethod")
    private String paymentMethod; // 'tarjeta' o 'iban'
    
    @Column(name="numsTarjeta")
    private String numsTarjeta; // Número de tarjeta (16 dígitos) o IBAN

    @Column(name="dateOfExpiry")
    @Temporal(TemporalType.DATE)
    private Date dateOfExpiry; // Fecha de caducidad (tarjeta)

    @Column(name="cvv")
    private String cvv; // Código CVV (tarjeta)
    
    @Column(name="iban")
    private String iban; // IBAN (iban)
   
    
    @OneToMany(mappedBy = "user")
    private List<UserCoin> userCoins;
	
}
