package com.shopme.common.entity;

import java.util.List;

public class SettingBag {

	private List<Setting> settings;

	public SettingBag(List<Setting> settings) {
		this.settings = settings;
	}

	public Setting get(String key) {
		int index = settings.indexOf(new Setting(key));
		return index >= 0 ? settings.get(index) : null;
	}

	public String getValue(String key) {
		var setting = get(key);
		return setting != null ? setting.getValue() : null;
	}

	public void update(String key, String value) {
		var setting = get(key);
		if (setting != null && value != null) {
			setting.setValue(value);
		}
	}

	public List<Setting> list() {
		return settings;
	}

}
