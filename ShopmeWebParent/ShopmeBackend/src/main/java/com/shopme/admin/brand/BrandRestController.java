package com.shopme.admin.brand;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopme.common.entity.Brand;

@RestController
public class BrandRestController {

	@Autowired
	BrandService brandService;

	@PostMapping("/brands/check_unique")
	public String checkUnique(@Param("id") Integer id, @Param("name") String name) {

		if (brandService.isUniqueBrand(id, name))
			return "DuplicateName";
		return "OK";
	}

	@GetMapping("/brands/{id}/categories")
	public List<CategoryDTO> listCategoriesByBrand(@PathVariable("id") Integer brandId) throws BrandNotFoundExecption {

		List<CategoryDTO> listCategories = new ArrayList<>();

		try {
			Brand brand = brandService.findById(brandId);
			var categories = brand.getCategories();
			for (var category : categories) {
				
				listCategories.add(new CategoryDTO(category.getId(), category.getName()));
			}
			return listCategories;

		} catch (BrandNotFoundExecption e) {
			throw new BrandNotFoundExecption("Brand with ID " + brandId + " not found");
		}

	}

}
