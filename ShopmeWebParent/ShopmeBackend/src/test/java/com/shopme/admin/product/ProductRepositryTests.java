package com.shopme.admin.product;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.Product;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class ProductRepositryTests {

	@Autowired
	private ProductRepositry productRepositry;

	@Autowired
	private TestEntityManager entityManager;

	@Test
	public void testCreateProduct() {
		Brand brand = entityManager.find(Brand.class, 6);
		Category category = entityManager.find(Category.class, 1);
		Product product = new Product();
		product.setName("sumsung mobile");
		product.setAlias("sumsung_mobile_A73");
		product.setShortDescription("A Good smart phone from sumsung");
		product.setFullDescription("This is very good mobile phone from Opsumsungp full description");
		product.setMainImage("default.png");
		product.setBrand(brand);
		product.setCategory(category);
		product.setPrice(456f);
		product.setCreatedTime(new Date());
		product.setUpdatedTime(new Date());

		Product savedCategory = productRepositry.save(product);
		assertThat(savedCategory).isNotNull();
		assertThat(savedCategory.getId()).isGreaterThan(0);
	}

	@Test
	public void listAll() {
		List<Product> products = (List<Product>) productRepositry.findAll();
		products.forEach(product -> System.out.println(product.getName()));
		assertThat(products.size()).isGreaterThan(0);
	}
	
	@Test
	public void getProduct() {
		int id = 1;
		Product product = productRepositry.findById(id).get();
		assertThat(product).isNotNull();
		assertThat(product.getId()).isEqualTo(id);
		
	}
	
	@Test
	public void update() {
		
		var id = 1;
		var product = productRepositry.findById(id).get();
		product.setPrice(1200f);
		productRepositry.save(product);
		var savedProduct = entityManager.find(Product.class, id);
		assertThat(savedProduct.getPrice()).isEqualTo(1200);
	}
	
	@Test
	public void testDelete() {
		
		var id = 2;
		productRepositry.deleteById(id);
		var product = productRepositry.findById(id);
		assertThat(!product.isPresent());
	}
	
	@Test
	public void testSaveProductWithImages() {
		Integer id = 1;
		Product product = productRepositry.findById(id).get();
		product.setMainImage("main_image.png");
		product.addExtraImages("extra_image1.png");
		product.addExtraImages("extra_image2.png");
		product.addExtraImages("extra_image3.png");
		Product savedProduct = productRepositry.save(product);
		assertThat(savedProduct.getImages().size()).isEqualTo(3);
	}
	
	@Test
	public void testSaveProductWithDetails() {
		Integer id = 1;
		Product product = productRepositry.findById(id).get();
		
		product.addDetail("Memory size", "8GB");
		product.addDetail("Internal size", "128GB");
		
		Product savedProduct = productRepositry.save(product);
		assertThat(savedProduct.getDetails()).isNotEmpty();
	}
	
}
