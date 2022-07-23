package com.shopme.admin.setting;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Setting;
import com.shopme.common.entity.SettingCategory;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class SettingRepositoryTests {

	@Autowired
	private SettingRepository settingRepository;

	@Test
	public void testCreateGeneralSettings() {

		var siteLogo = new Setting("site_logo", "shopme.png", SettingCategory.GENERAL);
		var copyright = new Setting("site_copyright", "Copyright (C) 2021 Shopme Ltd.", SettingCategory.GENERAL);

		settingRepository.saveAll(List.of(siteLogo, copyright));

		Iterable<Setting> iterable = settingRepository.findAll();
		assertThat(iterable).size().isGreaterThan(0);
	}

	@Test
	public void testCreateCurrencySettings() {

		var currencyId = new Setting("currency_id", "1", SettingCategory.CURRENCY);
		var symbol = new Setting("currency_symbol", "$", SettingCategory.CURRENCY);
		var symbolPosition = new Setting("currency_symbol_position", "before", SettingCategory.CURRENCY);
		var decimalPointType = new Setting("decimal_point_type", "point", SettingCategory.CURRENCY);
		var decimalDigits = new Setting("decimal_digits", "2", SettingCategory.CURRENCY);
		var thousandsPointType = new Setting("thousands_point_type", "comma", SettingCategory.CURRENCY);

		settingRepository.saveAll(
				List.of(currencyId, symbol, symbolPosition, decimalPointType, decimalDigits, thousandsPointType));

	}
	
	@Test
	public void testFindSettingByCategory() {
		var settings = settingRepository.findByCategory(SettingCategory.GENERAL);
		
		settings.forEach(System.out::println);
	}

}
