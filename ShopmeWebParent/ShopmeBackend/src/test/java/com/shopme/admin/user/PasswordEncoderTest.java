package com.shopme.admin.user;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoderTest {

	@Test
	public void testEncodePassword() {
		BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
		String rawPassword = "name2022";
		String encodedPassword = bCryptPasswordEncoder.encode(rawPassword);
		boolean isMatchesPasswords = bCryptPasswordEncoder.matches(rawPassword, encodedPassword);
		assertThat(isMatchesPasswords).isTrue();
	}
}
