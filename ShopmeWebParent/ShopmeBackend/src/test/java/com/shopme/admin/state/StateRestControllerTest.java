package com.shopme.admin.state;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.State;

@SpringBootTest
@AutoConfigureMockMvc
public class StateRestControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private StateRepository repository;

	@Test
	@WithMockUser(username = "user@mail.com", password = "12345678", authorities = "ADMIN")
	public void testListByCountry() throws Exception {
		Integer countryId = 1;
		String url = "/states/list-by-country/" + countryId;
		MvcResult mvcResult = mockMvc.perform(get(url)).andExpect(status().isOk()).andDo(print()).andReturn();
		String result = mvcResult.getResponse().getContentAsString();
		State[] states = mapper.readValue(result, State[].class);
		assertThat(states).hasSizeGreaterThan(0);
	}

	@Test
	@WithMockUser(username = "user@mail.com", password = "12345678", authorities = "ADMIN")
	public void testCreateState() throws JsonProcessingException, Exception {
		var url = "/states/save";
		var country = new Country(1);
		var stateName = "Qina";
		var state = new State(stateName, country);
		MvcResult mvcResult = mockMvc.perform(
				post(url).contentType("application/json").content(mapper.writeValueAsString(state)).with(csrf()))
				.andDo(print()).andExpect(status().isOk()).andReturn();
		
		var stateId = Integer.parseInt(mvcResult.getResponse().getContentAsString());
		var savedState = repository.findById(stateId);
		assertThat(savedState).isPresent();
		assertThat(savedState.get().getName()).isEqualTo(stateName);
	}
	
	@Test
	@WithMockUser(username = "user@mail.com", password = "12345678", authorities = "ADMIN")
	public void testUpdateState() throws JsonProcessingException, Exception {
		var url = "/states/save";
		var country = new Country(1);
		var stateName = "Qina2";
		var stateId = 5;
		var state = new State(stateId, stateName, country);
		mockMvc.perform(
				post(url).contentType("application/json").content(mapper.writeValueAsString(state)).with(csrf()))
				.andDo(print()).andExpect(status().isOk());
		
		var savedState = repository.findById(stateId);
		assertThat(savedState).isPresent();
		assertThat(savedState.get().getName()).isEqualTo(stateName);
	}
	
	
	@Test
	@WithMockUser(username = "user@mail.com", password = "12345678", authorities = "ADMIN")
	public void testDeleteCountry() throws Exception {
		var stateId = 5;
		var url = "/states/delete/" + stateId;
		mockMvc.perform(get(url)).andExpect(status().isOk());
		var state = repository.findById(stateId); 
		assertThat(state).isNotPresent();
	}

}
