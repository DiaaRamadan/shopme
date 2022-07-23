package com.shopme.admin.country;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.shopme.admin.Country.CountryRepository;
import com.shopme.common.entity.Country;

@AutoConfigureTestDatabase(replace = Replace.NONE)
@DataJpaTest
@Rollback(false)
public class CountryRepositoryTest {

	@Autowired
	private CountryRepository countryRepository;
	
	@Test
	public void testCreateNewCountry() {
		var country = new Country("China", "CN");
		var savedCountry = countryRepository.save(country);
		assertThat(savedCountry).isNotNull();
	}
	
	@Test
	public void testListCountries() {
		var countries = countryRepository.findAll();
		countries.forEach(System.out::println);
		assertThat(countries).size().isGreaterThan(0);
	}
	
	@Test
	public void testUpdateCountry() {
		String name = "China2";
		var country = countryRepository.findById(2).get();
		country.setName("China2");
		var updatedCountry = countryRepository.save(country);
		assertThat(updatedCountry.getName()).isEqualTo(name);
	}
	
	@Test
	public void testGetCountry() {
		var country = countryRepository.findById(2).get();
		assertThat(country).isNotNull();
	}
	
	@Test
	public void testDeleteCountry() {
		Integer id = 2;
		countryRepository.deleteById(id);
		
		var country = countryRepository.findById(id);
		assertThat(country.isEmpty());
	}
}
