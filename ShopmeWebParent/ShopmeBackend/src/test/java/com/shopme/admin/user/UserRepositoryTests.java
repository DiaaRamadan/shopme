package com.shopme.admin.user;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class UserRepositoryTests {

	@Autowired
	private UserRepository repository;

	@Autowired
	private TestEntityManager entityManager;

	@Test
	public void testCreateUserWithOneRole() {

		var role = entityManager.find(Role.class, 1);
		var user = new User("deia.atiya100@gmail.com", "12345678", "Diaa", "Ramadan");
		user.addRole(role);

		var savedUser = repository.save(user);
		assertThat(savedUser.getId()).isGreaterThan(0);
	}

	@Test
	public void testCreateUserWithTwoRole() {

		var user = new User("deia2.atiya100@gmail.com", "12345678", "Diaa2", "Ramadan");
		var roleEditor = new Role(3);
		var roleAssistant = new Role(5);
		user.addRole(roleEditor);
		user.addRole(roleAssistant);
		var savedUser = repository.save(user);
		assertThat(savedUser.getId()).isGreaterThan(0);
	}

	@Test
	public void testCanListUsers() {
		var users = repository.findAll();

		users.forEach(user -> System.out.println(user));
	}

	@Test
	public void testCanGetUserById() {
		User user = repository.findById(1).get();
		System.out.println(user);
		assertThat(user).isNotNull();
	}

	@Test
	public void testCanUpdateUser() {
		var user = repository.findById(1).get();
		user.setEnabled(true);
		user.setEmail("test@mail.com");
		repository.save(user);
	}

	@Test
	public void testCanUpdateUserRoles() {
		var user = repository.findById(3).get();
		var role = new Role(4);
		var roleEditor = new Role(3);
		user.getRoles().remove(roleEditor);
		user.addRole(role);
		
		repository.save(user);
	}
	
	@Test
	public void testCanDeleteUser() {
		var userId = 3;
		repository.deleteById(userId);
	}

}
