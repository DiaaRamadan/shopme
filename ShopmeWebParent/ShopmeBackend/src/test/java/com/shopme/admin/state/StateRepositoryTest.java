package com.shopme.admin.state;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Country;
import com.shopme.common.entity.State;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class StateRepositoryTest {

	@Autowired
	private StateRepository stateRepository;
	
	@Autowired
	private TestEntityManager entityManager;
	
	@Test
	public void testCreateState() {
		var country = entityManager.find(Country.class, 1);
		var savedState = stateRepository.save(new State("Alex", country));
		assertThat(savedState).isNotNull();
		assertThat(savedState.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testListStatesByCountry() {
		var country = entityManager.find(Country.class, 1);
		var states = stateRepository.findByCountryOrderByNameAsc(country);
		states.forEach(System.out::println);
		assertThat(states).size().isGreaterThan(0);
	}
	
	@Test
	public void testUpdateState() {
		Integer id = 2;
		String name = "Alexandria";
		var state = stateRepository.findById(id).get();
		state.setName(name);
		var updatedState = stateRepository.save(state);
		assertThat(updatedState.getName()).isEqualTo(name);
	}
	
	@Test
	public void testDeleteState() {
		Integer id = 2;
		stateRepository.deleteById(id);
		var deletedState = stateRepository.findById(id);
		assertThat(deletedState.isEmpty());
		
	}
	
}
