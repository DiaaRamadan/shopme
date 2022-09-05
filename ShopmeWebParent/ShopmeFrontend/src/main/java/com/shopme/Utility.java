package com.shopme;

import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import com.shopme.security.oauth.CustomerOAuth2User;
import com.shopme.setting.EmailSettingBag;

public class Utility {

	public static String getSiteUrl(HttpServletRequest request) {
		String siteUrl = request.getRequestURL().toString();
		return siteUrl.replace(request.getServletPath(), "");
	}

	public static JavaMailSenderImpl getJavaMailSender(EmailSettingBag emailSettingBag) {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost(emailSettingBag.getHost());
		mailSender.setPort(emailSettingBag.getPort());
		mailSender.setUsername(emailSettingBag.getUsername());
		mailSender.setPassword(emailSettingBag.getPassword());

		Properties properties = new Properties();
		properties.setProperty("mail.smtp.auth", emailSettingBag.getStmpAuth());
		properties.setProperty("mail.smtp.starttls.enable", emailSettingBag.getStmpSecured());

		mailSender.setJavaMailProperties(properties);

		return mailSender;
	}

	public static String getEmailOfAuthenticatedCustomer(HttpServletRequest request) {

		var principal = request.getUserPrincipal();
		if (principal == null)
			return null;
		String email = null;
		if (principal instanceof UsernamePasswordAuthenticationToken
				|| principal instanceof RememberMeAuthenticationToken) {
			email = principal.getName();
		} else if (principal instanceof OAuth2AuthenticationToken) {

			OAuth2AuthenticationToken auth2AuthenticationToken = (OAuth2AuthenticationToken) principal;
			CustomerOAuth2User auth2User = (CustomerOAuth2User) auth2AuthenticationToken.getPrincipal();
			email = auth2User.getEmail();
		}

		return email;
	}
}
