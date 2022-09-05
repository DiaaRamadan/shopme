package com.shopme.admin.paging;

import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

@Lazy
public interface SearchRepository<T, ID> extends PagingAndSortingRepository<T, ID> {

	
	public Page<T> findAll(String keyword, Pageable pageable);
	
}
