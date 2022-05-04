package com.shopme.admin.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

	@Autowired
	private UserService userService;

	@GetMapping("/users")
	public String listAll(Model model) {
		var users = userService.listAll();
		System.out.println(users);
		model.addAttribute("usersList", users);
		return "users";
	}
}
