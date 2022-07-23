package com.shopme.admin.country;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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
import com.shopme.admin.Country.CountryRepository;
import com.shopme.common.entity.Country;

@SpringBootTest
@AutoConfigureMockMvc
public class CountryRestControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private CountryRepository repository;

	@Test
	@WithMockUser(username = "name@java.com", password = "java123", roles = "ADMIN")
	public void testListCountries() throws Exception {
		var url = "/countries/list";
		MvcResult mvcResult = mockMvc.perform(get(url)).andExpect(status().isOk()).andDo(print()).andReturn();
		String jsonResponse = mvcResult.getResponse().getContentAsString();

		Country[] countries = mapper.readValue(jsonResponse, Country[].class);

		assertThat(countries).hasSizeGreaterThan(0);
	}

	@Test
	@WithMockUser(username = "name@java.com", password = "java123", roles = "ADMIN")
	public void testSaveCountry() throws JsonProcessingException, Exception {
		var url = "/countries/save";
		var name = "japanes";
		var code = "JPN";
		var country = new Country(name, code);

		MvcResult result = mockMvc.perform(
				post(url).contentType("application/json").content(mapper.writeValueAsString(country)).with(csrf()))
				.andDo(print()).andExpect(status().isOk()).andReturn();
		String response = result.getResponse().getContentAsString();
		Integer id = Integer.parseInt(response);

		var savedCountry = repository.findById(id).get();
		assertThat(savedCountry.getName()).isEqualTo(name);
	}

	@Test
	@WithMockUser(username = "name@java.com", password = "java123", roles = "ADMIN")
	public void testUpdateCountry() throws JsonProcessingException, Exception {
		var url = "/countries/save";
		var id = 3;
		var name = "japanes";
		var code = "JPN";
		var country = new Country(id, name, code);

		mockMvc.perform(
				post(url).contentType("application/json").content(mapper.writeValueAsString(country)).with(csrf()))
				.andDo(print()).andExpect(status().isOk()).andExpect(content().string(String.valueOf(id)));

		var savedCountry = repository.findById(id);
		assertThat(savedCountry.isPresent());
		assertThat(savedCountry.get().getName()).isEqualTo(name);
	}

	@Test
	@WithMockUser(username = "name@java.com", password = "java123", roles = "ADMIN")
	public void testDeleteCountry() throws JsonProcessingException, Exception {
		var id = 4;
		var url = "/countries/delete/" + id;
		mockMvc.perform(get(url)).andExpect(status().isOk());
		
		var savedCountry = repository.findById(id);
		assertThat(savedCountry.isEmpty());
	}
}
