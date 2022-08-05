package com.shopme;

import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.springframework.mail.javamail.JavaMailSenderImpl;

import com.shopme.setting.EmailSettingBag;

public class Uitily {

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
}
