package com.shopme.admin.category;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.shopme.common.entity.Category;

public interface CategoryRepository extends PagingAndSortingRepository<Category, Integer> {
	
	@Query("select c from Category  c where c.parent.id is null")
	public List<Category> findRootCategories(Sort sort);
	
	@Query("select c from Category  c where c.parent.id is null")
	public Page<Category> findRootCategories(Pageable pageable );
	
	@Query("select c from Category c where c.name = :name")
	public Category findByName(@Param("name") String name);
	
	@Query("select c from Category c where c.alias = :alias")
	public Category findByAlias(@Param("alias") String alias);
	
	@Query("update Category c set c.enabled = ?2 where c.id = ?1")
	@Modifying
	public void updateEnabledStatus(Integer id, Boolean enabled);
	
	@Query("select c from Category c where name like %?1%")
	public Page<Category> search(String keywork, Pageable pageable);
}
