package com.doravantesoft.catalago.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class EmailDTO {
	
	//@JsonProperty(value = "email")
	@NotBlank(message = "Campo obrigatório")
	@Email(message = "Email invállido")
	private String email;
	
	public EmailDTO() {
		
	}

	public EmailDTO(String email) {
		this.email = email;
	}

	public String getEmail() {
		return email;
	}
	
	
	
	
}
