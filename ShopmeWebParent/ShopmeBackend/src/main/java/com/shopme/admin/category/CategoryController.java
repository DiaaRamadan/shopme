package com.shopme.admin.category;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
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
import com.shopme.common.entity.Category;
import com.shopme.common.exceptions.CategoryNotFoundExecption;

@Controller
public class CategoryController {

	@Autowired
	CategoryService categoryService;

	@GetMapping("/categories")
	public String listFirstPage(@Param("sortDir") String sortDir, Model model) {
		return listByPage(1, sortDir, null, model);
	}

	@GetMapping("/categories/page/{pageNum}")
	public String listByPage(@PathVariable("pageNum") int pageNum, @Param("sortDir") String sortDir,
			@Param("keyword") String keyword, Model model) {

		if (sortDir == null || sortDir.isEmpty())
			sortDir = "asc";

		CategoryPageInfo categoryPageInfo = new CategoryPageInfo();
		model.addAttribute("categories", categoryService.listByPage(categoryPageInfo, pageNum, sortDir, keyword));
		model.addAttribute("totalPages", categoryPageInfo.getTotalPages());
		model.addAttribute("totalItems", categoryPageInfo.getTotalElements());
		model.addAttribute("currentPage", pageNum);

		long startCount = (pageNum - 1) * CategoryService.ROOT_CATEGORIES_PER_PAGE + 1;
		long endCount = startCount + CategoryService.ROOT_CATEGORIES_PER_PAGE - 1;

		if (endCount > categoryPageInfo.getTotalElements()) {
			endCount = categoryPageInfo.getTotalElements();
		}

		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);

		String reverseDir = sortDir.equals("asc") ? "desc" : "asc";
		model.addAttribute("reverseDir", reverseDir);
		model.addAttribute("sortField", "name");
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("keyword", keyword);

		return "categories/categories";
	}

	@GetMapping("/categories/new")
	public String newCategory(Model model) {
		Category category = new Category();
		List<Category> categories = categoryService.listCategoriesUsedInForm();
		model.addAttribute("pageTitle", "Add new category");
		model.addAttribute("category", category);
		model.addAttribute("categories", categories);
		return "categories/category_form";
	}

	@PostMapping("/categories/save")
	public String categorySave(Category category, @RequestParam("fileImage") MultipartFile multipartFile,
			RedirectAttributes redirectAttributes) throws IOException {

		if (!multipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			category.setImage(fileName);
			Category savedCategory = categoryService.save(category);
			String dirName = "../category-images/" + savedCategory.getId();
			FileUploadUtil.cleanDir(dirName);
			FileUploadUtil.save(dirName, fileName, multipartFile);
		} else {
			categoryService.save(category);
		}

		redirectAttributes.addFlashAttribute("message", "The category has been saved successfully");

		return "redirect:/categories";
	}

	@GetMapping("/categories/edit/{id}")
	public String edit(@PathVariable("id") int id, Model model, RedirectAttributes redirectAttributes) {
		try {
			model.addAttribute("category", categoryService.get(id));
			model.addAttribute("pageTitle", "Edit Category (ID: " + id + ") ");
			model.addAttribute("categories", categoryService.listCategoriesUsedInForm());
			return "categories/category_form";
		} catch (CategoryNotFoundExecption e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			return "redirect:/categories";
		}
	}

	@GetMapping("/categories/{id}/enabled/{status}")
	public String updateEnableStatus(@PathVariable("id") Integer id, @PathVariable("status") Boolean status,
			RedirectAttributes redirectAttributes) {
		try {

			categoryService.updateEnabledStatus(id, status);
			String statusString = (status) ? "enabled" : "disabled";
			redirectAttributes.addFlashAttribute("message", "Category with ID " + id + statusString + " successfully");

		} catch (CategoryNotFoundExecption e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());

		}
		return "redirect:/categories";
	}

	@GetMapping("/categories/delete/{id}")
	public String delete(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
		try {

			categoryService.delete(id);
			redirectAttributes.addFlashAttribute("message", "Category with ID " + id + " successfully");

		} catch (CategoryNotFoundExecption e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());

		}
		return "redirect:/categories";
	}

	@GetMapping("/categories/export/csv")
	public void exportToCsv(HttpServletResponse response) throws IOException {
		var categories = categoryService.listCategoriesUsedInForm();
		CategoryCsvExporter categoryCsvExporter = new CategoryCsvExporter();
		categoryCsvExporter.export(categories, response);
	}

}
