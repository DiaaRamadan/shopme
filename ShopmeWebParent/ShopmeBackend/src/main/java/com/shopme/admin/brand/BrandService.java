package com.shopme.admin.brand;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.common.entity.Brand;

@Service
@Transactional
public class BrandService {

	public static final int BRANDS_PER_PAGE = 5;

	@Autowired
	BrandRepository brandRepository;

	public void listByPage(int pageNum, PagingAndSortingHelper helper) {

		Sort sort = Sort.by(helper.getSortField());
		sort = helper.getSortDir().equals("asc") ? sort.ascending() : sort.descending();
		Pageable pageable = PageRequest.of(pageNum - 1, BRANDS_PER_PAGE, sort);

		Page<Brand> page = null;
		if (helper.getKeyword() != null)
			page = brandRepository.findAll(helper.getKeyword(), pageable);
		else
			page = brandRepository.findAll(pageable);
		
		helper.updateModelAttributes(pageNum, page);
	}

	public List<Brand> listAll() {

		return (List<Brand>) brandRepository.findAll();
	}

	public Brand save(Brand brand) {
		return brandRepository.save(brand);
	}

	public Brand findById(int id) throws BrandNotFoundExecption {

		var brand = brandRepository.findById(id);
		if (brand.isEmpty())
			throw new BrandNotFoundExecption("Brand with ID " + id + " not found");
		return brand.get();
	}

	public void deleteById(int id) throws BrandNotFoundExecption {

		var brand = brandRepository.findById(id);
		if (brand.isEmpty())
			throw new BrandNotFoundExecption("Brand with ID " + id + " not found");

		brandRepository.deleteById(id);

	}

	public boolean isUniqueBrand(Integer id, String name) {

		boolean isCreateNew = (id == null) || (id == 0);

		var brand = brandRepository.findByName(name);

		if (isCreateNew && brand == null)
			return true;

		if (!isCreateNew && brand.getId() != id)
			return true;

		return false;
	}

}
