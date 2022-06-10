package com.shopme.admin.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.shopme.admin.user.UserRepository;
import com.shopme.common.entity.User;

public class ShopmeUserDetailsService implements UserDetailsService {

	@Autowired
	UserRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		
		User user = userRepository.getUserByEmail(email);
		
		if(null == user)
			throw new UsernameNotFoundException("Could not found user with email " + email);
		return new ShomeUserDetails(user);
		
	}

}
