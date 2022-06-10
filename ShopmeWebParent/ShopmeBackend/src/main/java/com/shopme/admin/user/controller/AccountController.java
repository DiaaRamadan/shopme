package com.shopme.admin.user.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.admin.FileUploadUtil;
import com.shopme.admin.security.ShomeUserDetails;
import com.shopme.admin.user.UserService;
import com.shopme.common.entity.User;

@Controller
public class AccountController {
	
	@Autowired
	private UserService userService;
	
	@GetMapping("/account")
	public String viewDetails(@AuthenticationPrincipal ShomeUserDetails logedInUser, Model model) {
		String email = logedInUser.getUsername();
		model.addAttribute("user", userService.getUserByEmail(email));
		
		return "users/account_form";
	}
	
	@PostMapping("/account/update")
	public String saveDetails(User user, RedirectAttributes redirectAttributes,
			@AuthenticationPrincipal ShomeUserDetails loggedInUser,
			@RequestParam("image") MultipartFile multipartFile) throws IOException {

		if (!multipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			user.setPhotos(fileName);
			User savedUser = userService.updateUserAccount(user);
			String dirName = "user-photos/" + savedUser.getId();
			FileUploadUtil.cleanDir(dirName);
			FileUploadUtil.save(dirName, fileName, multipartFile);
		} else {
			if (user.getPhotos().isEmpty())
				user.setPhotos(null);
			userService.updateUserAccount(user);
		}
		
		loggedInUser.setFirstName(user.getFirstName());
		loggedInUser.setLastName(user.getLastName());

		redirectAttributes.addFlashAttribute("message", "Your account has been updated.");
		
		return "redirect:/account";
	}

}
