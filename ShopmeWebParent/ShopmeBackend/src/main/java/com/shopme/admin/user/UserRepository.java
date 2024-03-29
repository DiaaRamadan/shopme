package com.shopme.admin.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.shopme.admin.paging.SearchRepository;
import com.shopme.common.entity.User;

public interface UserRepository extends SearchRepository<User, Integer> {

	@Query("select u from User u where u.email = :email")
	public User getUserByEmail(@Param("email") String email);

	public Long countById(Integer id);

	@Query("update User u set u.enabled = ?2 where u.id=?1")
	@Modifying
	public void updateUserEnabledStatus(Integer id, boolean enabled);
	
	@Query("select u from User u where CONCAT(u.id, ' ', u.email, ' ', u.firstName, ' ', u.lastName) LIKE %?1%")
	public Page<User> findAll(String keyword, Pageable pageable);
}
