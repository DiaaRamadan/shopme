package com.shopme.admin.brand;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.admin.FileUploadUtil;
import com.shopme.admin.category.CategoryService;
import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.admin.paging.PagingAndSortingParam;
import com.shopme.common.entity.Brand;

@Controller
public class BrandController {

	@Autowired
	BrandService brandService;

	@Autowired
	CategoryService categoryService;

	@GetMapping("/brands")
	public String listFirstPage() {
		return "redirect:/brands/page/1?sortFielةd=name&sortDir=asc";
	}

	@GetMapping("/brands/page/{pageNum}")
	public String listByPage(@PathVariable("pageNum") int pageNum, Model model,
			@PagingAndSortingParam(listName = "brands", moduleURL = "/brands") PagingAndSortingHelper helper) {

		brandService.listByPage(pageNum, helper);

		return "/brands/brands";
	}

	@GetMapping("/brands/new")
	public String newBrand(Model model) {
		model.addAttribute("pageTitle", "Add new brand");
		model.addAttribute("categories", categoryService.listCategoriesUsedInForm());
		model.addAttribute("brand", new Brand());

		return "brands/brand_form";
	}

	@GetMapping("/brands/edit/{id}")
	public String editBrand(Model model, @PathVariable("id") int id, RedirectAttributes redirectAttributes) {

		try {
			model.addAttribute("pageTitle", "Add new brand");
			model.addAttribute("categories", categoryService.listCategoriesUsedInForm());
			model.addAttribute("brand", brandService.findById(id));

			return "brands/brand_form";
		} catch (BrandNotFoundExecption e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			return "redirect:/brands";
		}
	}

	@PostMapping("/brands/save")
	public String save(Brand brand, @RequestParam("fileImage") MultipartFile multipartFile,
			RedirectAttributes redirectAttributes) throws IOException {

		if (!multipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			brand.setLogo(fileName);
			Brand savedBrand = brandService.save(brand);
			String dirName = "../brands-images/" + savedBrand.getId();
			FileUploadUtil.cleanDir(dirName);
			FileUploadUtil.save(dirName, fileName, multipartFile);
		} else {
			brandService.save(brand);
		}

		redirectAttributes.addFlashAttribute("message", "The brand has been saved successfully");
		return "redirect:/brands";
	}

	@GetMapping("/brands/delete/{id}")
	public String deleteBrand(@PathVariable("id") int id, RedirectAttributes redirectAttributes) {

		try {
			brandService.deleteById(id);
			redirectAttributes.addFlashAttribute("message", "Brand with ID " + id + " deleted successfully");

		} catch (BrandNotFoundExecption e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());

		}
		return "redirect:/brands";
	}

}
