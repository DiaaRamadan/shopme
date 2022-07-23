package com.shopme.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.shopme.category.CategoryService;
import com.shopme.common.entity.Category;

@Controller
public class ProductController {

	@Autowired
	private ProductService productService;

	@Autowired
	private CategoryService categoryService;

	@GetMapping("/c/{category_alias}")
	public String viewCategoryFirstPage(@PathVariable("category_alias") String alias, Model model) {
		return viewCategoryByPage(alias, model, 1);
	}

	@GetMapping("/c/{category_alias}/page/{pageNum}")
	public String viewCategoryByPage(@PathVariable("category_alias") String alias, Model model,
			@PathVariable("pageNum") int pageNum) {

		try {
		Category category = categoryService.getCategoryByAlias(alias);
		if (category == null) {
			return "error/404";
		}

		var categoryParents = categoryService.getCategoryParents(category);
		var page = productService.listByCategory(pageNum, category.getId());

		long startCount = (pageNum - 1) * ProductService.PRODUCT_PER_PAGE + 1;
		long endCount = startCount + ProductService.PRODUCT_PER_PAGE - 1;

		if (endCount > page.getTotalElements()) {
			endCount = page.getTotalElements();
		}
		System.out.println(page.getContent().size());
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("pageTitle", category.getName());
		model.addAttribute("categoryParents", categoryParents);
		model.addAttribute("products", page.getContent());
		model.addAttribute("category", category);

		return "product/products_by_category";
		}catch (Exception e) {
			return "error/404";
		}

	}
	
	
	@GetMapping("/p/{product_alias}")
	public String viewProductDetails(@PathVariable("product_alias") String alias, Model model) {

		try {
		
		var product = productService.geProductByAlias(alias);
		var categoryParents = categoryService.getCategoryParents(product.getCategory());
		

		model.addAttribute("product", product);
		model.addAttribute("categoryParents", categoryParents);
		model.addAttribute("pageTitle", product.getShortName());

		return "product/product_details";
		}catch (Exception e) {
			e.printStackTrace();
			return "errors/404";
		}

	}

}
