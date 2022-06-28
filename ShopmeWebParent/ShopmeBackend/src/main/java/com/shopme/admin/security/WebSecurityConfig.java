package com.shopme.admin.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Bean
	public UserDetailsService detailsService() {
		return new ShopmeUserDetailsService();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
		daoAuthenticationProvider.setUserDetailsService(detailsService());
		daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());

		return daoAuthenticationProvider;
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {

		auth.authenticationProvider(authenticationProvider());
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.authorizeRequests().antMatchers("/users/**").hasAuthority("Admin").antMatchers("/categories/**")
				.hasAnyAuthority("Admin", "Editor").antMatchers("/brands/**").hasAnyAuthority("Admin", "Editor")

				.antMatchers("/products/edit/**", "/products/save", "/products/check_unique")
				.hasAnyAuthority("Admin", "Editor", "Salesperson")

				.antMatchers("/products/new", "/products/delete/**").hasAnyAuthority("Admin", "Editor")

				.antMatchers("/products", "/products/", "/products/details/**", "/products/page/**")
				.hasAnyAuthority("Admin", "Editor", "Salesperson", "Shipper")

				.antMatchers("/products/**").hasAnyAuthority("Admin", "Editor")

				.anyRequest().authenticated().and().formLogin().loginPage("/login").usernameParameter("email")
				.defaultSuccessUrl("/").permitAll().and().logout().permitAll().and().rememberMe().key("soma-loma-boma")
				.userDetailsService(detailsService()).tokenValiditySeconds(7 * 27 * 60 * 60);
	}

	@Override
	public void configure(WebSecurity web) {

		web.ignoring().antMatchers("/images/**", "/js/**", "/webjars/**", "/javax.faces.resource/**", "/richtext/**");
	}

}
