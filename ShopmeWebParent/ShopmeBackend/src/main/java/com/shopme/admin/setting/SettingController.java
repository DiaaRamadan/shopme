package com.shopme.admin.setting;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.admin.FileUploadUtil;
import com.shopme.common.entity.Setting;
import com.shopme.common.entity.SettingBag;

@Controller
public class SettingController {

	@Autowired
	private SettingService settingService;

	@Autowired
	private CurrencyRepository currencyRepository;

	@GetMapping("/settings")
	public String listAll(Model model) {

		var settings = settingService.listAllSettings();
		model.addAttribute("currencies", currencyRepository.findAllByOrderByNameAsc());
		for (var setting : settings) {
			model.addAttribute(setting.getKey(), setting.getValue());
		}
		return "settings/settings";
	}

	@PostMapping("/settings/save-general")
	public String saveGeneralSettings(@RequestParam("fileImage") MultipartFile multipartFile,
			HttpServletRequest httpServletRequest, RedirectAttributes redirectAttributes) throws IOException {

		GeneralSettingBag settingBag = settingService.getGeneralSettingBag();
		saveSiteLogo(multipartFile, settingBag);
		saveCurrencySymbol(httpServletRequest, settingBag);
		updateSettingValuesFromForm(httpServletRequest, settingBag.list());
		redirectAttributes.addFlashAttribute("message", "General Settings has been saved.");
		return "redirect:/settings";

	}

	private void saveSiteLogo(MultipartFile multipartFile, GeneralSettingBag settingBag) throws IOException {
		if (!multipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			String value = "/site-logo/" + fileName;
			settingBag.updateSiteLogo(value);
			String uploadedDir = "../site-logo/";
			FileUploadUtil.cleanDir(uploadedDir);
			FileUploadUtil.save(uploadedDir, fileName, multipartFile);
		}
	}

	private void saveCurrencySymbol(HttpServletRequest request, GeneralSettingBag bag) {
		Integer currencyId = Integer.parseInt(request.getParameter("currency_id"));
		var currency = currencyRepository.findById(currencyId);
		if (currency.isPresent()) {
			var currencyData = currency.get();
			bag.updateCurrencySymbol(currencyData.getSymbol());
		}
	}
	
	public void updateSettingValuesFromForm(HttpServletRequest request, List<Setting> settings) {
		for(Setting setting : settings) {
			String value = request.getParameter(setting.getKey());
			if(value != null) {
				setting.setValue(value);
				
			}
		}
		
		settingService.saveAll(settings);
	}
}
