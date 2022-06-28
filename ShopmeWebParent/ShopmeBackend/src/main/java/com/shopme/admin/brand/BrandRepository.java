package com.shopme.admin.brand;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.shopme.common.entity.Brand;

public interface BrandRepository extends PagingAndSortingRepository<Brand, Integer> {

	public Brand findByName(String name);
	
	@Query("select b from Brand b where name like %?1%")
	public Page<Brand> findAll(String keyword, Pageable pageable);
	
	@Query("select new Brand(b.id, b.name) from Brand b order by b.name asc")
	public List<Brand> findAll();
	
}
