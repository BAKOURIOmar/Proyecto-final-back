package com.babel.semilla.babel.practicafinal.dtos;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginDTO {
	
	private Integer id;
	private String email;
    private String password;

    
    
    
}
