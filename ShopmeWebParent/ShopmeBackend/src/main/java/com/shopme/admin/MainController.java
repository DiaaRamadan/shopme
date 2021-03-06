package com.shopme.admin;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

	
	@GetMapping("")
	public String viewHomePage() {
		return "index";
	}
	
	@GetMapping("/login")
	public String login() {
		Authentication authenticate = SecurityContextHolder.getContext().getAuthentication();
		if(authenticate == null || authenticate instanceof AnonymousAuthenticationToken)
			return "login";
		
		return "redirect:/";
	}
}
