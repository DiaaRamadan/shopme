package com.shopme;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.shopme.category.CategoryService;

@Controller
public class MainController {

	@Autowired
	private CategoryService categoryService;
	
	@GetMapping("")
	public String viewHomePage(Model model) {
		model.addAttribute("categories", categoryService.listNoChildrenCategories());
		return "index";
	}
	
	@GetMapping("/login")
	public String login() {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return (authentication == null || authentication instanceof AnonymousAuthenticationToken) ?  "login" : "redirect:/";
	}

}
