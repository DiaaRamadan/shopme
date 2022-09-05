package com.shopme.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.shopme.security.oauth.CustomerOAuth2UserService;
import com.shopme.security.oauth.DatabaseLoginSuccessHandler;
import com.shopme.security.oauth.OAuth2LoginSuccessHandler;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private CustomerOAuth2UserService auth2UserService;
	
	@Autowired
	private OAuth2LoginSuccessHandler auth2LoginSuccessHandler;
	
	@Autowired
	private DatabaseLoginSuccessHandler databaseLoginSuccessHandler;
	
	@Bean
	public PasswordEncoder getPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().antMatchers("/customer", "/account-details", "/update-account-details", "/cart")
		.authenticated().anyRequest().permitAll()
		.and()
		.formLogin().loginPage("/login").successHandler(databaseLoginSuccessHandler)
		.usernameParameter("email").permitAll()
		.and()
		.oauth2Login().loginPage("/login")
		.userInfoEndpoint()
		.userService(auth2UserService)
		.and().successHandler(auth2LoginSuccessHandler)
		.and()
		.logout().permitAll()
		.and().rememberMe().key("123456789_abcdefgh").tokenValiditySeconds(14 * 24 * 60 * 60);
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/images/**", "/js/**", "/webjars/**");
	}

	@Bean
	public UserDetailsService userDetailsService() {
		return new CustomerUserDetailsService();
	}

	@Bean
	public DaoAuthenticationProvider daoAuthenticationProvider() {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(userDetailsService());
		authenticationProvider.setPasswordEncoder(getPasswordEncoder());
		return authenticationProvider;
	}

}
