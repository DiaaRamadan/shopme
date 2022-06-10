package com.shopme.admin.brand;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class BrandRepositoryTests {

	@Autowired
	BrandRepository brandRepository;

	@Test
	public void testCreateBrand() {
		var brand = new Brand("apple", "brand-default.png");
		var category = new Category(1);
		var category2 = new Category(2);
		brand.addCategory(category);
		brand.addCategory(category2);
		var savedBrand = brandRepository.save(brand);
		assertThat(savedBrand.getId()).isGreaterThan(0);
	}

	@Test
	public void testFindAll() {
		var brands = brandRepository.findAll();
		brands.forEach(brand -> System.out.println(brand.getName()));
		assertThat(brands).isNotEmpty();
	}

	@Test
	public void testFindById() {

		var brand = brandRepository.findById(1).get();
		assertThat(brand.getId()).isGreaterThan(0);
	}

	@Test
	public void testUpdate() {
		int id = 1;
		var brand = brandRepository.findById(id).get();
		brand.setName("Test name");
		var savedBrand = brandRepository.save(brand);

		assertThat(savedBrand.getId()).isEqualTo(id);
		assertThat(savedBrand.getName()).isEqualTo("Test name");
	}

	@Test
	public void testDelete() {
		int id = 2;
		brandRepository.deleteById(2);
		var brand = brandRepository.findById(id);
		assertThat(brand).isEmpty();
	}
}
