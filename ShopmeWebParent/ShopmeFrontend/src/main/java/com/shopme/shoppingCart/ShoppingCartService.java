package com.shopme.shoppingCart;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.CartItem;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.Product;
import com.shopme.common.exceptions.ShoppingCartExecption;

@Service
public class ShoppingCartService {

	@Autowired
	private ShoppinCartRepository cartRepository;

	public Integer addProduct(Integer productId, Integer quantity, Customer customer) throws ShoppingCartExecption {

		Integer updatedQuantity = quantity;
		Product product = new Product(productId);
		CartItem cartItem = cartRepository.findByCustomerAndProduct(customer.getId(), productId);

		if (cartItem != null) {
			updatedQuantity = quantity + cartItem.getQuantity();

			if (updatedQuantity > 5) {
				throw new ShoppingCartExecption("Could not add more " + quantity + " item(s)"
						+ " because there's already " + cartItem.getQuantity() + ". Maxinum allowed quantity is 5.");
			}

		} else {
			cartItem = new CartItem();
			cartItem.setCustomer(customer);
			cartItem.setProduct(product);
		}
		cartItem.setQuantity(updatedQuantity);
		cartRepository.save(cartItem);
		return updatedQuantity;

	}
	
	
	public List<CartItem> listCartItems(Customer customer){
		return cartRepository.findByCustomer(customer);
	}
}
