package com.shopme.customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomerRestController {


	@Autowired
	private CustomerService customerService;
	
	@GetMapping("/customer/email/check-unique")
	public String isEmailUnique(@Param("email") String email) {
		if(customerService.isEmailUnique(email)) return "OK";
		else return "Duplicate";
	}
	
}
