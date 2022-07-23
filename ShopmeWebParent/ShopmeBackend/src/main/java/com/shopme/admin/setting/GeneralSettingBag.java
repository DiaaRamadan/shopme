package com.shopme.admin.setting;

import java.util.List;

import com.shopme.common.entity.Setting;
import com.shopme.common.entity.SettingBag;

public class GeneralSettingBag extends SettingBag {

	public GeneralSettingBag(List<Setting> settings) {
		super(settings);
	}
	
	public void updateCurrencySymbol(String value) {
		update("currency_symbol", value);
	}
	
	public void updateSiteLogo(String value) {
		update("site_logo", value);
	}

}
