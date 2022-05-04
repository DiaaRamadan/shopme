package com.shopme.admin.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Role;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class RoleRepositoryTests {

	@Autowired
	RoleRepository roleRepository;

	@Test
	public void testCreateFirstRow() {

		var roleAdmin = new Role("Admin", "Manage everything");
		var savedRole = roleRepository.save(roleAdmin);
		assertThat(savedRole.getId()).isGreaterThan(0);
	}

	@Test
	public void testCreateRestRoles() {

		var roleSalesPerson = new Role("Salesperson", "Manage product price, customers, shipping, orders and sales reposrts");
		var roleEditor = new Role("Editor", "Manage categories, products, brands, articles and menus");
		var roleShipper = new Role("Shipper", "Manage orders and update order status");
		var roleAssistant = new Role("Assistant", "Manage questions and reviews");
		
		roleRepository.saveAll(List.of(roleSalesPerson, roleAssistant, roleEditor, roleShipper));

	}
}
