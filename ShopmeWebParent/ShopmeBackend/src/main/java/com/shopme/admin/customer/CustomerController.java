package com.shopme.admin.customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.admin.user.UserService;
import com.shopme.common.entity.Customer;
import com.shopme.common.exceptions.CustomerNotFoundException;

@Controller
public class CustomerController {

	@Autowired
	private CustomerService customerService;

	@GetMapping("/customers")
	public String listFirstPage(Model model) {
		return listByPage(null, "asc", "firstName", 1, model);
	}

	@GetMapping("/customers/page/{pageNum}")
	public String listByPage(@Param("keyword") String keyword, @Param("sortDir") String sortDir,
			@Param("sortField") String sortField, @Param("pageNum") int pageNum, Model model) {

		var page = customerService.listByPage(pageNum - 1, keyword, sortDir, sortField);
		var customers = page.getContent();

		long startCount = (pageNum - 1) * UserService.USERS_PER_PAGE + 1;
		long endCount = startCount + UserService.USERS_PER_PAGE - 1;

		if (endCount > page.getTotalElements()) {
			endCount = page.getTotalElements();
		}

		var reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("customers", customers);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSortDir", reverseSortDir);
		model.addAttribute("keyword", keyword);

		return "customers/customers";

	}

	@GetMapping("/customers/edit/{id}")
	public String showCustomerForm(Model model, @PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
		try {
			model.addAttribute("customer", customerService.get(id));
			model.addAttribute("countries", customerService.CountriesList());
			model.addAttribute("pageTitle", "Edit customer with ID " + id);
			return "customers/customer_form";
		} catch (CustomerNotFoundException exception) {
			redirectAttributes.addFlashAttribute("message", exception.getMessage());
			return "redirect:/customers";
		}
	}

	@PostMapping("customers/save")
	public String save(Customer customer, RedirectAttributes redirectAttributes) {
		customerService.save(customer);
		redirectAttributes.addFlashAttribute("message", "User with ID [" + customer.getId() + "] saved successfully");
		return "redirect:/customers";
	}

	@GetMapping("/customers/{id}/enabled/{status}")
	public String changeEnabledStatus(@PathVariable("id") Integer id, @PathVariable("status") boolean status,
			RedirectAttributes redirectAttributes) {
		try {
			customerService.updateEnabledStatus(id, status);
			String statusType = status ? "Enabled" : "Disabled";
			redirectAttributes.addFlashAttribute("message", "User with ID [" + id + "] " + statusType + " successfully");
			return "redirect:/customers";

		} catch (CustomerNotFoundException exception) {
			redirectAttributes.addFlashAttribute("message", exception.getMessage());
			return "redirect:/customers";
		}
	}
}
