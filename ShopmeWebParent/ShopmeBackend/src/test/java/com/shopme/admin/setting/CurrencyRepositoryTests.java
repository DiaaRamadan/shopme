package com.shopme.admin.setting;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Currency;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class CurrencyRepositoryTests {

	@Autowired
	private CurrencyRepository currencyRepository;

	@Test
	public void testCreateCurrencies() {

		var listOfCurrencies = Arrays.asList(new Currency("Unit States Dollar", "$", "USD"),
				new Currency("British Pound", "£", "GPB"), new Currency("Japanese Yen", "¥", "JPY"),
				new Currency("Euro", "€", "EUR"), new Currency("Russian Ruble", "₽", "RUB"),
				new Currency("South Korean Won", "¥", "CNY"));

		currencyRepository.saveAll(listOfCurrencies);
		var iterable = currencyRepository.findAll();
		assertThat(iterable).size().isEqualTo(6);
	}
	
	@Test
	public void testFindAllOrderByNameAsc() {
		List<Currency> currencies = currencyRepository.findAllByOrderByNameAsc();
		currencies.forEach(System.out::println);
		assertThat(currencies.size()).isGreaterThan(0);
	}

}
