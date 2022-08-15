package com.shopme.customer;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Calendar;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.AuthenticationType;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.Customer;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class CustomerReposoitryTest {

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private TestEntityManager entityManager;

	@Test
	public void testCreateCustomer() {

		var country = entityManager.find(Country.class, 1);
		var customer = new Customer();
		customer.setAddressLine1("dokki, cairo");
		customer.setCity("Cairo");
		customer.setCountry(country);
		customer.setCreatedTime(Calendar.getInstance().getTime());
		customer.setEmail("user2@mail.com");
		customer.setEnabled(false);
		customer.setFirstName("user2");
		customer.setLastName("name");
		customer.setPassword("12345678");
		customer.setPhoneNumber("+201046332561");
		customer.setPostalCode("123456");
		customer.setState("Dokki");
		customer.setVerificationCode("code_123");

		var savedCustomer = customerRepository.save(customer);
		assertThat(savedCustomer.getId()).isGreaterThan(0);
	}

	@Test
	public void testUpdateCustomer() {
		var id = 1;
		var customer = customerRepository.findById(id).get();
		var name = "edit user";
		customer.setFirstName(name);
		customerRepository.save(customer);
		var savedCustomer = customerRepository.findById(id).get();
		assertThat(savedCustomer.getFirstName()).isEqualTo(name);
	}
	
	@Test
	public void testListCustomers() {
		var customers = customerRepository.findAll();
		assertThat(customers).size().isGreaterThan(0);
	}
	
	@Test
	public void testGetCustomer() {
		var customer = customerRepository.findById(1);
		assertThat(customer.isPresent()).isTrue();
	}
	
	@Test
	public void testDeleteCustomer() {
		var id = 3;
		customerRepository.deleteById(id);
		var customer = customerRepository.findById(id);
		assertThat(customer.isPresent()).isFalse();
	}
	
	@Test
	public void testFindByEmail() {
		var customer = customerRepository.findByEmail("user@mail.com");
		assertThat(customer).isNotNull();
	}
	
	@Test
	public void testFindByVerificationCode() {
		var customer = customerRepository.findByVerificationCode("code_123");
		assertThat(customer).isNotNull();
	}
	
	@Test
	public void testEnabledCustomer() {
		var id = 1;
		customerRepository.enable(id);
		var customer = customerRepository.findById(id).get();
		assertThat(customer.isEnabled()).isTrue();
	}
	
	@Test
	public void testUpdateAuthenticationType() {
		var id = 1;
		customerRepository.updateAuthenticationType(AuthenticationType.DATABASE, id);
		var customer = customerRepository.findById(id).get();
		assertThat(customer.getAuthenticationType()).isEqualTo(AuthenticationType.DATABASE);
	}

}
