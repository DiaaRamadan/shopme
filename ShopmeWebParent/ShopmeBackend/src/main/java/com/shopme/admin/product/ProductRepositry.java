package com.shopme.admin.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.shopme.common.entity.Product;

public interface ProductRepositry extends PagingAndSortingRepository<Product, Integer>{

	@Query("SELECT p FROM Product p where p.name = ?1")
	public Product findByName(String name);
	
	@Query("UPDATE Product p set p.enabled = ?2 where p.id = ?1")
	@Modifying
	public void updateEnabledStatus(Integer id, Boolean enabled);
	
	@Query("SELECT p from Product p where p.name like %?1%"
			+ " OR p.shortDescription like %?1%"
			+ " OR p.fullDescription like %?1%"
			+ " OR p.brand.name like %?1%"
			+ " OR p.category.name like %?1%")
	public Page<Product> findAll(String keyword, Pageable pagable);
	
}
