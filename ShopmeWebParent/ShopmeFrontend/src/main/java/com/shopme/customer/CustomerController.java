package com.shopme.customer;

import java.io.UnsupportedEncodingException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.shopme.Uitily;
import com.shopme.common.entity.Customer;
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

	private void sendVerificationMail(HttpServletRequest request, Customer customer)
			throws MessagingException, UnsupportedEncodingException {

		EmailSettingBag emailSettingBag = settingService.getEmailSettingBag();
		JavaMailSenderImpl mailSender = Uitily.getJavaMailSender(emailSettingBag);

		String toAddress = customer.getEmail();
		String subject = emailSettingBag.getCustomerVerifySubject();
		String content = emailSettingBag.getCustomerVerifyContent();

		MimeMessage message = mailSender.createMimeMessage();

		MimeMessageHelper helper = new MimeMessageHelper(message);
		helper.setFrom(emailSettingBag.getFromAddress(), emailSettingBag.getSenderName());
		helper.setTo(toAddress);
		helper.setSubject(subject);

		String verifyUrl = Uitily.getSiteUrl(request) + "/verify?code=" + customer.getVerificationCode();
		content = content.replace("[[name]]", customer.getFullName());
		content = content.replace("[[url]]", verifyUrl);
		helper.setText(content);
		mailSender.send(message);

	}

}