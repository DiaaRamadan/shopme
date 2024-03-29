package com.shopme.security.oauth;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.shopme.common.entity.AuthenticationType;
import com.shopme.customer.CustomerService;

@Component
public class OAuth2LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

	@Autowired
	@Lazy
	private CustomerService customerService;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws ServletException, IOException {

		CustomerOAuth2User auth2User = (CustomerOAuth2User) authentication.getPrincipal();

		String name = auth2User.getName();
		String email = auth2User.getEmail();
		String countryCode = request.getLocale().getCountry();

		var customer = customerService.getCustomerByEmail(email);

		if (customer == null) {

			customerService.addNewCustomerUponOAuthLogin(name, email, countryCode);

		} else {
			auth2User.setFullName(customer.getFullName());
			customerService.updateAuthenticationType(customer, AuthenticationType.GOOGLE);
		}

		super.onAuthenticationSuccess(request, response, authentication);
	}

}
