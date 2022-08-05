package com.shopme.admin.product;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Product;
import com.shopme.common.exceptions.ProductNotFoundExecption;

@Service
@Transactional
public class ProductService {

	public static final int PRODUCT_PER_PAGE = 5;
	
	@Autowired
	private ProductRepositry productRepositry;

	public List<Product> listAll() {
		return (List<Product>) productRepositry.findAll();
	}
	
	public Page<Product> listByPage(int pageNum, String sortField , String sortDir, String keyword) {
		
		Sort sort = Sort.by(sortField);
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
		Pageable page = PageRequest.of(pageNum - 1, PRODUCT_PER_PAGE, sort);
		
		if(keyword != null) return productRepositry.findAll(keyword, page);
		
		return productRepositry.findAll(page);
	}

	public Product save(Product product) {

		if (product.getId() == null) {
			product.setCreatedTime(new Date());
		}

		if (product.getAlias() == null || product.getAlias().isEmpty()) {
			product.setAlias(product.getName().replaceAll(" ", "-"));
		} else {
			product.setAlias(product.getAlias().replaceAll(" ", "-"));
		}
		
		product.setUpdatedTime(new Date());
		return productRepositry.save(product);

	}
	
	public Boolean isUnique(Integer id, String name) {
		
		Product product = productRepositry.findByName(name);
		
		if(id == null || id == 0) {
			
			return product == null;
		}
		
		return product != null && product.getId() != id;
	}
	
	public void updateEnabledStatus(Integer id, Boolean status) throws ProductNotFoundExecption {
		var product = productRepositry.findById(id);
		if(product.isEmpty()) 
			throw new ProductNotFoundExecption("Product with ID " + id + " not found");
		productRepositry.updateEnabledStatus(id, status);
	}
	
	public void delete(Integer id) throws ProductNotFoundExecption {
		var product = productRepositry.findById(id);
		if (product.isEmpty())
			throw new ProductNotFoundExecption("Product With ID " + id + " not found");
		productRepositry.deleteById(id);
	}
	
	public Product get(Integer id) throws ProductNotFoundExecption {
		try {
			
			return productRepositry.findById(id).get();
			
		}catch (NoSuchElementException e) {
			throw new ProductNotFoundExecption("Product with id " + id + " not found");
		}
	}
}
