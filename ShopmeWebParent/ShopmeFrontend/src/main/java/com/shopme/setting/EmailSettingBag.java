package com.shopme.setting;

import java.util.List;

import com.shopme.common.entity.Setting;
import com.shopme.common.entity.SettingBag;

public class EmailSettingBag extends SettingBag {

	public EmailSettingBag(List<Setting> settings) {
		super(settings);
	}

	public String getHost() {
		return getValue("mail_host");
	}

	public int getPort() {
		return Integer.parseInt(getValue("mail_port"));
	}

	public String getUsername() {
		return getValue("mail_username");
	}

	public String getPassword() {
		return getValue("mail_password");
	}

	public String getStmpAuth() {
		return getValue("mail_auth");
	}

	public String getStmpSecured() {
		return getValue("mail_secured");
	}

	public String getFromAddress() {
		return getValue("mail_from_address");
	}

	public String getSenderName() {
		return getValue("mail_sender_name");
	}
	
	public String getCustomerVerifySubject() {
		return getValue("customer_verify_subject");
	}
	
	public String getCustomerVerifyContent() {
		return getValue("customer_verify_content");
	}

}
