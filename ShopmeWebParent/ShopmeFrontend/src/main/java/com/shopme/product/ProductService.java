package com.shopme.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Product;
import com.shopme.common.exceptions.ProductNotFoundExecption;

@Service
public class ProductService {

	public static final int PRODUCT_PER_PAGE = 10;

	@Autowired
	private ProductRespository productRespository;

	public Page<Product> listByCategory(int pageNum, Integer categoryID) {
		Pageable pageable = PageRequest.of(pageNum - 1, PRODUCT_PER_PAGE);
		return productRespository.listByCategory(categoryID, pageable);
	}

	public Product geProductByAlias(String alias) throws ProductNotFoundExecption {
		Product product = productRespository.findByAlias(alias);
		if (product == null)
			throw new ProductNotFoundExecption("Product with alias " + alias + " not found");
		return product;
	}

}
