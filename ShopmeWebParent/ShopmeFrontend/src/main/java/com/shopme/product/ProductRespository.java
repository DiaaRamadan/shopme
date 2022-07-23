package com.shopme.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.shopme.common.entity.Product;

public interface ProductRespository extends PagingAndSortingRepository<Product, Integer>{

	@Query("SELECT p FROM Product p where p.enabled = true AND p.category.id = ?1 ORDER BY p.name asc")
	public Page<Product> listByCategory(Integer categoryId, Pageable pagable);
	
	public Product findByAlias(String alias);
}
