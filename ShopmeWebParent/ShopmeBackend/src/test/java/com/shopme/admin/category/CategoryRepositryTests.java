package com.shopme.admin.category;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Category;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class CategoryRepositryTests {

	@Autowired
	private CategoryRepository categoryRepository;

	@Test
	public void testCreateRootCategory() {
		Category category = new Category("Electronics");
		Category savedCategory = categoryRepository.save(category);
		assertThat(savedCategory.getId()).isGreaterThan(0);

	}

	@Test
	public void testCreateSubCategory() {
		Category parent = new Category(5);
		Category category = new Category("Memory", parent);
		Category savedCategory = categoryRepository.save(category);
		assertThat(savedCategory.getId()).isGreaterThan(0);
	}

	@Test
	public void testGetCategory() {

		Category category = categoryRepository.findById(1).get();
		System.out.println(category.getName());

		var children = category.getChildren();

		for (Category child : children) {
			System.out.println(child.getName());
		}

		assertThat(children.size()).isGreaterThan(0);
	}

	@Test
	public void testPrintHierarchicalCategories() {

		var categories = categoryRepository.findAll();

		for (Category category : categories) {
			System.out.println(category.getName());
			if (category.getParent() != null) {

				for (Category child : category.getChildren()) {
					System.out.println(child.getName());
				}
			}
		}

	}
	
	@Test
	public void testListRootCategories() {
		List<Category> categories = categoryRepository.findRootCategories(Sort.by("name").ascending() );
		categories.forEach(category -> System.out.println(category.getName()));
	}
	
	@Test
	public void testFindCategoryByName() {
		String name = "computer";
		var category = categoryRepository.findByName(name);
		assertThat(category).isNotNull();
		assertThat(category.getName().toLowerCase()).isEqualTo(name.toLowerCase());
	}
	
	@Test
	public void testFindCategoryByAlias() {
		String alias = "computer1";
		var category = categoryRepository.findByAlias(alias);
		assertThat(category).isNotNull();
		assertThat(category.getAlias().toLowerCase()).isEqualTo(alias.toLowerCase());
	}
}
