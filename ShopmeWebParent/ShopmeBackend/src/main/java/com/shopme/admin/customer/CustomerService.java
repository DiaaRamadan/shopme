package com.shopme.admin.customer;

import java.util.List;
import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.shopme.admin.Country.CountryRepository;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.Customer;
import com.shopme.common.exceptions.CustomerNotFoundException;

@Service
@Transactional
public class CustomerService {

	public static final int CUSTOMERS_PER_PAGE = 10;

	@Autowired
	private CustomerReposoitry customerReposoitry;

	@Autowired
	private CountryRepository countryRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public Page<Customer> listByPage(int pageNum, String keyword, String sortDir, String sortField) {

		Sort sort = Sort.by(sortField);
		sort = (sortDir == "asc") ? sort.ascending() : sort.descending();
		Pageable pageable = PageRequest.of(pageNum, CUSTOMERS_PER_PAGE, sort);

		if (null == keyword)
			return customerReposoitry.findAll(pageable);

		return customerReposoitry.findAll(keyword, pageable);

	}

	public void updateEnabledStatus(Integer id, boolean status) throws CustomerNotFoundException {
		if (customerReposoitry.countById(id) == 0) {
			throw new CustomerNotFoundException("Customer with Id [" + id + "] not found");
		}

		customerReposoitry.updateEnabledStatus(id, status);
	}

	public Customer get(Integer id) throws CustomerNotFoundException {
		try {

			return customerReposoitry.findById(id).get();
		} catch (NoSuchElementException exception) {
			throw new CustomerNotFoundException("Customer with Id [" + id + "] not found");
		}
	}

	public List<Country> CountriesList() {
		return countryRepository.findAllByOrderByNameAsc();
	}

	public boolean isEmailUnique(Integer id, String email) {
		Customer customer = customerReposoitry.findByEmail(email);
		if (customer != null && customer.getId() != id) {
			return false;
		}
		return true;
	}

	public void save(Customer customer) {
		Customer savedCustomer = customerReposoitry.findById(customer.getId()).get();
		if (!customer.getPassword().isEmpty()) {
			customer.setPassword(passwordEncoder.encode(customer.getPassword()));
		} else {
			customer.setPassword(savedCustomer.getPassword());
		}
		customer.setCreatedTime(savedCustomer.getCreatedTime());
		customerReposoitry.save(customer);
	}

	public void delete(Integer id) throws CustomerNotFoundException {
		if (customerReposoitry.countById(id) == 0) {
			throw new CustomerNotFoundException("Customer with Id [" + id + "] not found");
		}

		customerReposoitry.deleteById(id);
	}

}
