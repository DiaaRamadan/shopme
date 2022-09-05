package com.shopme.customer;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.Utility;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.Customer;
import com.shopme.security.CustomerUserDetails;
import com.shopme.security.oauth.CustomerOAuth2User;
import com.shopme.setting.EmailSettingBag;
import com.shopme.setting.SettingService;

@Controller
public class CustomerController {

	@Autowired
	private CustomerService customerService;

	@Autowired
	private SettingService settingService;

	@GetMapping("register")
	public String showRegisterForm(Model model) {
		model.addAttribute("countries", customerService.countriesList());
		model.addAttribute("pageTitle", "Customer Registration");
		model.addAttribute("customer", new Customer());
		return "register/register_form";
	}

	@PostMapping("create-customer")
	public String createCustomer(Customer customer, Model model, HttpServletRequest request)
			throws UnsupportedEncodingException, MessagingException {
		customerService.registerCustomer(customer);
		sendVerificationMail(request, customer);
		model.addAttribute("pageTitle", "Registration successed");

		return "register/register_success";
	}

	@GetMapping("/verify")
	public String verifyAcount(@RequestParam("code") String code, Model model) {

		boolean verified = customerService.verify(code);
		return "register/" + (verified ? "verify_success" : "verify_fail");

	}

	@GetMapping("/account-details")
	public String showAccountForm(Model model, HttpServletRequest request) {

		String email = Utility.getEmailOfAuthenticatedCustomer(request);

		Customer customer = customerService.getCustomerByEmail(email);
		List<Country> countries = customerService.countriesList();

		model.addAttribute("customer", customer);
		model.addAttribute("countries", countries);
		return "customer/account_form";
	}

	@PostMapping("/update-account-details")
	public String updateAccountDetail(Customer customer, Model model, RedirectAttributes redirectAttributes, HttpServletRequest request) {
		customerService.update(customer);

		updateAuthenticatedCustomer(customer, request);

		redirectAttributes.addFlashAttribute("message", "Your account details has been updated.");
		return "redirect:/account-details";
	}

	private void updateAuthenticatedCustomer(Customer customer, HttpServletRequest request) {

		var principal = request.getUserPrincipal();
			
		if (principal instanceof UsernamePasswordAuthenticationToken
				|| principal instanceof RememberMeAuthenticationToken) {
			
			CustomerUserDetails customerUserDetails = getCustomerUserDetailsObject(principal);
			Customer authenticatedCustomer = customerUserDetails.getCustomer();
			authenticatedCustomer.setFirstName(customer.getFirstName());
			authenticatedCustomer.setLastName(customer.getLastName());
			
		} else if (principal instanceof OAuth2AuthenticationToken) {

			OAuth2AuthenticationToken auth2AuthenticationToken = (OAuth2AuthenticationToken) principal;
			CustomerOAuth2User auth2User = (CustomerOAuth2User) auth2AuthenticationToken.getPrincipal();
			auth2User.setFullName(customer.getFullName());
		}

	}
	
	private CustomerUserDetails getCustomerUserDetailsObject(Object principal) {
		
		CustomerUserDetails userDetails = null;
		if(principal instanceof UsernamePasswordAuthenticationToken) {
			UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
			userDetails = (CustomerUserDetails) token.getPrincipal();
		}else if(principal instanceof RememberMeAuthenticationToken) {
			RememberMeAuthenticationToken token = (RememberMeAuthenticationToken) principal;
			userDetails = (CustomerUserDetails) token.getPrincipal();
		}
		
		return userDetails;
	}

	private void sendVerificationMail(HttpServletRequest request, Customer customer)
			throws MessagingException, UnsupportedEncodingException {

		EmailSettingBag emailSettingBag = settingService.getEmailSettingBag();
		JavaMailSenderImpl mailSender = Utility.getJavaMailSender(emailSettingBag);

		String toAddress = customer.getEmail();
		String subject = emailSettingBag.getCustomerVerifySubject();
		String content = emailSettingBag.getCustomerVerifyContent();

		MimeMessage message = mailSender.createMimeMessage();

		MimeMessageHelper helper = new MimeMessageHelper(message);
		helper.setFrom(emailSettingBag.getFromAddress(), emailSettingBag.getSenderName());
		helper.setTo(toAddress);
		helper.setSubject(subject);

		String verifyUrl = Utility.getSiteUrl(request) + "/verify?code=" + customer.getVerificationCode();
		content = content.replace("[[name]]", customer.getFullName());
		content = content.replace("[[url]]", verifyUrl);
		helper.setText(content);
		mailSender.send(message);

	}

}
