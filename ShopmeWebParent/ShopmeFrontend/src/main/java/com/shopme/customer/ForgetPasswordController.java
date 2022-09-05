package com.shopme.customer;

import java.io.UnsupportedEncodingException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.shopme.Utility;
import com.shopme.common.exceptions.CustomerNotFoundException;
import com.shopme.setting.EmailSettingBag;
import com.shopme.setting.SettingService;

@Controller
public class ForgetPasswordController {

	@Autowired
	private CustomerService customerService;

	@Autowired
	private SettingService settingService;

	@GetMapping("/forget-password")
	public String showRequestForm() {

		return "customer/forget_password_form";
	}

	@PostMapping("/forget-password")
	public String processRequestForm(HttpServletRequest request, Model model) {

		String email = request.getParameter("email");
		try {

			String token = customerService.updateRestPasswordToken(email);
			String link = Utility.getSiteUrl(request) + "/reset-password?token=" + token;
			sendEmail(link, email);
			model.addAttribute("message", "We have send a rest password link to your email");

		} catch (Exception e) {
			model.addAttribute("error", e.getMessage());
		}

		return "customer/forget_password_form";
	}

	@GetMapping("/reset-password")
	public String showResetForm(@Param("token") String token, Model model) {

		try {
			customerService.getByResetPasswordToken(token);
			model.addAttribute("token", token);
			return "customer/reset_password_form";
			
		} catch (CustomerNotFoundException e) {
			model.addAttribute("pageTitle", "Invalid token");
			model.addAttribute("message", e.getMessage());
			return "message";
		}
	}

	@PostMapping("/reset-password")
	public String processResetForm(HttpServletRequest request, Model model) {
		
		String token = request.getParameter("token");
		String password = request.getParameter("password");
		
		try {
			customerService.updatePassword(token, password);
			model.addAttribute("title", "Reset your password.");
			model.addAttribute("message", "You have successfully changed your password.");
			return "message";
			
		} catch (CustomerNotFoundException e) {
			
			model.addAttribute("pageTitle", "Invalid token");
			model.addAttribute("message", e.getMessage());
			return "message";
			
		}
	}
	
	private void sendEmail(String link, String email) throws MessagingException, UnsupportedEncodingException {
		EmailSettingBag emailSettingBag = settingService.getEmailSettingBag();
		JavaMailSenderImpl mailSender = Utility.getJavaMailSender(emailSettingBag);

		String subject = "Here's the link to rest password";

		String content = "<p>Hello,</p>" + "<p>You have requested to reset your password.</p>"
				+ "<p>Click the link bellow to change your password.</p>" + "<p><a href=\"" + link
				+ "\">Change my password</a></p>" + "<br>" + "<p>Ignore this email if you do remeber yuor password,"
				+ "or you have not made the request</p>";

		MimeMessage message = mailSender.createMimeMessage();

		MimeMessageHelper helper = new MimeMessageHelper(message);
		helper.setFrom(emailSettingBag.getFromAddress(), emailSettingBag.getSenderName());
		helper.setTo(email);
		helper.setSubject(subject);
		helper.setText(content, true);

		mailSender.send(message);
	}
}
