package com.shopme.shoppingCart;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.CartItem;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.Product;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class CartItemRepositoryTest {

	@Autowired
	private ShoppinCartRepository shoppinCartRepository;

	@Autowired
	private TestEntityManager entityManager;

	@Test
	public void testSaveItem() {
		var customerId = 3;
		var productId = 10;
		Customer customer = entityManager.find(Customer.class, customerId);
		Product product = entityManager.find(Product.class, productId);

		CartItem item = new CartItem();
		item.setCustomer(customer);
		item.setProduct(product);
		item.setQuantity(1);

		CartItem savedItem = shoppinCartRepository.save(item);
		assertThat(savedItem).isNotNull();

	}

	@Test
	public void save2Items() {
		
		var customerId = 3;
		var productId = 6;
		Customer customer = entityManager.find(Customer.class, customerId);
		Product product = entityManager.find(Product.class, productId);

		CartItem item = new CartItem();
		item.setCustomer(customer);
		item.setProduct(product);
		item.setQuantity(1);
		
		CartItem item2 = new CartItem();
		item2.setCustomer(new Customer(2));
		item2.setProduct(new Product(8));
		item2.setQuantity(5);

		Iterable<CartItem> iterable = shoppinCartRepository.saveAll(List.of(item, item2));
		assertThat(iterable).size().isGreaterThan(0);		
	}
	
	@Test
	public void testFindByCustomer() {
		var customerId = 3;
		Customer customer = entityManager.find(Customer.class, customerId);
		List<CartItem> cartItems = shoppinCartRepository.findByCustomer(customer);
		assertThat(cartItems.size()).isGreaterThan(0);

	}
	
	@Test
	public void testFindByCustomerAndProduct() {
		var customerId = 3;
		var productId = 6;

		CartItem cartItems = shoppinCartRepository.findByCustomerAndProduct(customerId, productId);
		assertThat(cartItems).isNotNull();

	}
	
	@Test
	public void testUpdateQuantity() {
		var customerId = 3;
		var productId = 6;
		var quantity = 10;
		shoppinCartRepository.updateQuantity(quantity, customerId, productId);
		CartItem cartItem = shoppinCartRepository.findByCustomerAndProduct(customerId, productId);
		assertThat(cartItem.getQuantity()).isEqualTo(quantity);
	}
	
	@Test
	public void testDeleteByCustomerAndProduct() {
		var customerId = 3;
		var productId = 6;
		
		shoppinCartRepository.deleteByCustomerAndProduct(customerId, productId);

		CartItem cartItems = shoppinCartRepository.findByCustomerAndProduct(customerId, productId);
		assertThat(cartItems).isNull();

	}
}
