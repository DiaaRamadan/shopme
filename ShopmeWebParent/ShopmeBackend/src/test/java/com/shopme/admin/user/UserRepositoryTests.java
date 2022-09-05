package com.shopme.admin.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;

@DataJpaTest(showSql = false)
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
	
	@Test
	public void testCanGetUserByEmail() {
		var email = "deia.atiya100@gmail.com";
		var user = repository.getUserByEmail(email);
		assertThat(user).isNotNull();
	}
	
	@Test
	public void testCountById() {
		var count = repository.countById(1);
		assertThat(count).isGreaterThan(0);
	}
	
	@Test
	public void testCanDisabledUser() {
		repository.updateUserEnabledStatus(11, false);
	}
	
	@Test
	public void testCanEnabledUser() {
		repository.updateUserEnabledStatus(11, true);
	}
	
	@Test
	public void testFirstListPage() {
		
		int pageNumber = 0;
		int pageSize = 4;
		
		Pageable pageable = PageRequest.of(pageNumber, pageSize);
		Page<User> page = repository.findAll(pageable);
		List<User> listUsers = page.getContent();
		listUsers.forEach(user -> System.out.println(user));
		assertThat(listUsers.size()).isEqualTo(pageSize);
	}

	@Test
	public void testSearchUsers() {
		Pageable pageable = PageRequest.of(0, 5);
		String keyWord = "bruce";
		Page<User> page = repository.findAll(keyWord, pageable);
		
		var users = page.getContent();
		
		users.forEach(user -> System.out.print(user));
		
		assertThat(users.size()).isGreaterThan(0);
	}
}
