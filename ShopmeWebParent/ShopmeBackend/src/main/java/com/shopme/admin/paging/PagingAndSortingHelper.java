package com.shopme.admin.paging;

import org.springframework.data.domain.Page;
import org.springframework.web.method.support.ModelAndViewContainer;

public class PagingAndSortingHelper {

	private ModelAndViewContainer model;
	private String listName;
	private String moduleUrl;
	private String sortField;
	private String sortDir;
	private String keyword;



	public PagingAndSortingHelper(ModelAndViewContainer model, String listName, String moduleUrl, String sortField,
			String sortDir, String keyword) {
		
		this.model = model;
		this.listName = listName;
		this.moduleUrl = moduleUrl;
		this.sortField = sortField;
		this.sortDir = sortDir;
		this.keyword = keyword;
	}


	public void updateModelAttributes(int pageNum, Page<?> page) {
		var items = page.getContent();
		var pageSize = page.getSize();

		long startCount = (pageNum - 1) * pageSize + 1;
		long endCount = startCount + pageSize - 1;

		if (endCount > page.getTotalElements()) {
			endCount = page.getTotalElements();
		}

		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute(listName, items);
		model.addAttribute("moduleUrl", moduleUrl);
	}


	public String getSortField() {
		return sortField;
	}


	public String getSortDir() {
		return sortDir;
	}


	public String getKeyword() {
		return keyword;
	}

}
