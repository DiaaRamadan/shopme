package com.shopme.admin.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductRestController {

	@Autowired
	ProductService productService;

	@PostMapping("/products/check_unique")
	public String checkUnique(@Param("id") Integer id, @Param("name") String name) {
		if (!productService.isUnique(id, name))
			return "OK";

		return "Duplicate";
	}

}
