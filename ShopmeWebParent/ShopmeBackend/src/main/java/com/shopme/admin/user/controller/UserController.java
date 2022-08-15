package com.shopme.admin.user.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.admin.FileUploadUtil;
import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.admin.paging.PagingAndSortingParam;
import com.shopme.admin.user.UserNotFoundException;
import com.shopme.admin.user.UserService;
import com.shopme.admin.user.export.UserCsvExporter;
import com.shopme.admin.user.export.UserExeclExporter;
import com.shopme.admin.user.export.UserPdfExporter;
import com.shopme.common.entity.User;

@Controller
public class UserController {

	@Autowired
	private UserService userService;

	@GetMapping("/users")
	public String listFirstPage() {
		return "redirect:/users/page/1?sortField=firstName&sortDir=asc";
	}

	@GetMapping("/users/page/{pageNum}")
	public String listByPage(
			@PagingAndSortingParam(listName = "usersList", moduleURL = "/users") PagingAndSortingHelper helper,
			@PathVariable("pageNum") int pageNum, Model model) 
	{

		userService.listByPage(pageNum, helper);
		return "users/users";
	}

	@GetMapping("/users/new")
	public String newUser(Model model) {
		var user = new User();
		user.setEnabled(true);
		model.addAttribute("user", user);
		model.addAttribute("roles", userService.listRoles());
		model.addAttribute("pageTitle", "Create new user");
		return "users/user_form";
	}

	@PostMapping("/users/save")
	public String saveUser(User user, RedirectAttributes redirectAttributes,
			@RequestParam("image") MultipartFile multipartFile) throws IOException {

		if (!multipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			user.setPhotos(fileName);
			User savedUser = userService.save(user);
			String dirName = "user-photos/" + savedUser.getId();
			FileUploadUtil.cleanDir(dirName);
			FileUploadUtil.save(dirName, fileName, multipartFile);
		} else {
			if (user.getPhotos().isEmpty())
				user.setPhotos(null);
			userService.save(user);
		}

		redirectAttributes.addFlashAttribute("message", "The user saved succssfully.");

		return getRedirectUrlFoAffectedUser(user);
	}

	private String getRedirectUrlFoAffectedUser(User user) {
		String firstPartOfEmail = user.getEmail().split("@")[0];

		return "redirect:/users/page/1?sortField=id&sortDir=asc&keyword=" + firstPartOfEmail;
	}

	@GetMapping("/users/edit/{id}")
	public String edit(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
		try {
			var user = userService.get(id);
			model.addAttribute("user", user);
			model.addAttribute("pageTitle", "Edit user (ID: " + id + ") ");
			model.addAttribute("roles", userService.listRoles());
			return "users/user_form";
		} catch (UserNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			return "redirect:/users";
		}
	}

	@GetMapping("/users/delete/{id}")
	public String delete(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
		try {
			userService.delete(id);
			redirectAttributes.addFlashAttribute("message", "The user id (" + id + ") has been deleted");
		} catch (UserNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
		}

		return "redirect:/users";
	}

	@GetMapping("/users/{id}/enabled/{status}")
	public String updateUserEnabledStatus(@PathVariable("id") Integer id, @PathVariable("status") boolean status,
			RedirectAttributes redirectAttributes) {
		try {
			userService.updateUserEnabledStatus(id, status);
			String statusWord = (status) ? "Enabled" : "disaabled";
			redirectAttributes.addFlashAttribute("message", "The user id (" + id + ") has been " + statusWord);
		} catch (UserNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
		}

		return "redirect:/users";
	}

	@GetMapping("/users/export/csv")
	public void exportToCsv(HttpServletResponse response) throws IOException {

		List<User> users = userService.listAll();
		UserCsvExporter exporter = new UserCsvExporter();
		exporter.export(users, response);
	}

	@GetMapping("/users/export/excel")
	public void exportToExcel(HttpServletResponse response) throws IOException {

		List<User> users = userService.listAll();
		UserExeclExporter exporter = new UserExeclExporter();
		exporter.export(users, response);
	}

	@GetMapping("/users/export/pdf")
	public void exportToPdf(HttpServletResponse response) throws IOException {

		List<User> users = userService.listAll();
		UserPdfExporter exporter = new UserPdfExporter();
		exporter.export(users, response);
	}
}
