package com.shopme.customer;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.AuthenticationType;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.Customer;
import com.shopme.setting.CountryRepository;

import net.bytebuddy.utility.RandomString;

@Service
@Transactional
public class CustomerService {

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private CountryRepository countryRepository;

	public List<Country> countriesList() {
		return countryRepository.findAllByOrderByNameAsc();
	}

	public boolean isEmailUnique(String email) {
		return customerRepository.findByEmail(email) == null;
	}

	public void registerCustomer(Customer customer) {
		encodePassword(customer);
		customer.setEnabled(false);
		customer.setCreatedTime(Calendar.getInstance().getTime());
		customer.setAuthenticationType(AuthenticationType.DATABASE);
		String randomCode = RandomString.make(64);
		customer.setVerificationCode(randomCode);
		customerRepository.save(customer);

	}

	public boolean verify(String verificationCode) {
		Customer customer = customerRepository.findByVerificationCode(verificationCode);
		if (customer == null || customer.isEnabled())
			return false;

		customerRepository.enable(customer.getId());
		return true;
	}

	public void enabled(Integer customerId) {
		customerRepository.enable(customerId);
	}

	public void updateAuthenticationType(Customer customer, AuthenticationType authenticationType) {
		if (!customer.getAuthenticationType().equals(authenticationType))
			customerRepository.updateAuthenticationType(authenticationType, customer.getId());

	}

	public Customer getCustomerByEmail(String email) {
		return customerRepository.findByEmail(email);
	}

	private void encodePassword(Customer customer) {
		var encodedPassword = passwordEncoder.encode(customer.getPassword());
		customer.setPassword(encodedPassword);

	}

	public void addNewCustomerUponOAuthLogin(String name, String email, String countryCode) {

		var customer = new Customer();
		setName(name,customer);
		customer.setEmail(email);
		customer.setEnabled(true);
		customer.setCreatedTime(new Date());
		customer.setAuthenticationType(AuthenticationType.GOOGLE);
		customer.setPassword("");
		customer.setAddressLine1("");
		customer.setAddressLine2("");
		customer.setCity("");
		customer.setState("");
		customer.setPhoneNumber("");
		customer.setPostalCode("");
		customer.setCountry(countryRepository.findByCode(countryCode));

		customerRepository.save(customer);

	}

	public void setName(String name, Customer customer) {
		String[] splitedName = name.split(" ");
		if (splitedName.length < 2)
			customer.setFirstName(name);
		customer.setFirstName(splitedName[0]);
		customer.setLastName(splitedName[1]);
	}
}
