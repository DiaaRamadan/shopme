package com.shopme.setting;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.shopme.common.entity.Country;
import com.shopme.common.entity.State;
import com.shopme.common.entity.StateDTO;

@RestController
public class StateRestController {

	@Autowired
	private StateRepository stateRepository;

	@GetMapping("/settings/states/list-states-by-country/{id}")
	public List<StateDTO> listByCountry(@PathVariable("id") Integer id) {

		List<State> states = stateRepository.findByCountryOrderByNameAsc(new Country(id));
		List<StateDTO> statesDTO = new ArrayList<>();

		for (var state : states)
			statesDTO.add(new StateDTO(state.getId(), state.getName()));

		return statesDTO;

	}

}
