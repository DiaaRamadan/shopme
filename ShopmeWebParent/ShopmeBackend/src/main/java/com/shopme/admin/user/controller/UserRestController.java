package com.shopme.admin.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopme.admin.user.UserService;

@RestController
public class UserRestController {
	
	@Autowired
	UserService userService;
	
	@PostMapping("/users/check-email")
	public String checkEmailDuplication(@Param("id") Integer id, @Param("email") String email) {
		
		return userService.isEmailUnique(id, email) ? "Ok" : "Duplicated";
	}

}
