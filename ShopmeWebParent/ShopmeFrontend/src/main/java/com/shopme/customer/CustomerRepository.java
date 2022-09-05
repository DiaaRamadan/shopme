package com.shopme.customer;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.shopme.common.entity.AuthenticationType;
import com.shopme.common.entity.Customer;

public interface CustomerRepository extends CrudRepository<Customer, Integer> {

	@Query("SELECT c FROM Customer c WHERE c.email = ?1")
	public Customer findByEmail(String email);
	
	@Query("SELECT c FROM Customer c WHERE c.verificationCode = ?1")
	public Customer findByVerificationCode(String verificationCode);
	
	@Query("UPDATE Customer c SET c.enabled = true, c.verificationCode = null WHERE c.id = ?1")
	@Modifying
	public void enable(Integer customerId);
	
	@Query("UPDATE Customer c set c.authenticationType = ?1 WHERE c.id = ?2")
	@Modifying
	public void updateAuthenticationType(AuthenticationType authenticationType, Integer id);
	
	public Customer findByResetPasswordToken(String token);
}
