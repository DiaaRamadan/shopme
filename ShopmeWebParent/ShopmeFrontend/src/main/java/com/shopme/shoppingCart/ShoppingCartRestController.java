package com.shopme.shoppingCart;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopme.Utility;
import com.shopme.common.entity.Customer;
import com.shopme.common.exceptions.CustomerNotFoundException;
import com.shopme.common.exceptions.ShoppingCartExecption;
import com.shopme.customer.CustomerService;

@RestController
public class ShoppingCartRestController {

	@Autowired
	private ShoppingCartService cartService;

	@Autowired
	private CustomerService customerService;

	@PostMapping("/cart/add/{productId}/{quantity}")
	public String addProductToCart(@PathVariable("productId") Integer productId,
			@PathVariable("quantity") Integer quantity, HttpServletRequest request) {

		try {

			Customer customer = getAuthenticatedCustomer(request);
			Integer updatedQuantity = cartService.addProduct(productId, quantity, customer);

			return updatedQuantity + " item(s) of this product were added to your shopping cart.";
		} catch (CustomerNotFoundException e) {

			return "You must login to add this product to cart";
		} catch (ShoppingCartExecption e) {
			return e.getMessage();
		}

	}
	
	@PostMapping("/cart/update/{productId}/{quantity}")
	public String updateQuantity(@PathVariable("productId") Integer productId, 
			@PathVariable("quantity") Integer quantity, HttpServletRequest request) {
		
		try {
			Customer customer = getAuthenticatedCustomer(request);
			float subTotal = cartService.updateQuantity(productId, quantity, customer);
			return String.valueOf(subTotal);
		} catch (CustomerNotFoundException e) {
			
			return "You must login to update product quantity";
		}
		
		
	}

	private Customer getAuthenticatedCustomer(HttpServletRequest request) throws CustomerNotFoundException {
		String email = Utility.getEmailOfAuthenticatedCustomer(request);

		if (email == null)
			throw new CustomerNotFoundException("No authenticated user");

		return customerService.getCustomerByEmail(email);
	}

}
