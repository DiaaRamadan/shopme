package com.shopme.category;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class CategoryRepositoryTest {
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	@Test
	public void testListEnabledCategories() {
		var categories = categoryRepository.findAllEnabled();
		categories.forEach(category -> {
			System.out.println(category.getName() + "(" + category.isEnabled() +")");
		});
	}
	
	@Test
	public void testFindCategoryByAliasEnabled() {
		
		var alias = "Computer";
		
		var category = categoryRepository.findByAliasEnabled(alias);
		
		assertThat(category).isNotNull();
		
	}
	
}
