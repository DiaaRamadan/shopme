package com.shopme.admin.category;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Category;
import com.shopme.common.exceptions.CategoryNotFoundExecption;

@Service
@Transactional
public class CategoryService {

	public static final int ROOT_CATEGORIES_PER_PAGE = 50;

	@Autowired
	private CategoryRepository categoryRepository;

	public List<Category> listByPage(CategoryPageInfo categoryPageInfo, int pageNum, String sortDir, String keyword) {

		Sort sort = Sort.by("name");
		if (sortDir == null || sort.isEmpty())
			sort = sort.ascending();
		else if (sortDir.equals("asc"))
			sort = sort.ascending();
		else if (sortDir.equals("desc"))
			sort = sort.descending();

		Pageable page = PageRequest.of(pageNum - 1, ROOT_CATEGORIES_PER_PAGE, sort);

		Page<Category> pageCategories = null;

		if (keyword != null && !keyword.isEmpty()) {
			pageCategories = categoryRepository.search(keyword, page);
		} else {
			pageCategories = categoryRepository.findRootCategories(page);
		}
		categoryPageInfo.setTotalPages(pageCategories.getTotalPages());
		categoryPageInfo.setTotalElements(pageCategories.getTotalElements());

		if (keyword != null && !keyword.isEmpty()) {

			return pageCategories.getContent();

		}
		return listHierachicalCategories(pageCategories.getContent(), sortDir);

	}

	private List<Category> listHierachicalCategories(List<Category> rootCategories, String sortDir) {
		List<Category> hierachicalCategories = new ArrayList<>();

		for (Category category : rootCategories) {
			hierachicalCategories.add(Category.copyFull(category));

			var children = sortSubCategories(category.getChildren(), sortDir);

			for (Category child : children) {
				String name = "--" + child.getName();
				hierachicalCategories.add(Category.copyFull(category, name));
				listSubHierachicalCategories(hierachicalCategories, child, 1, sortDir);
			}

		}

		return hierachicalCategories;
	}

	private void listSubHierachicalCategories(List<Category> subHierachicalCategories, Category parent, int subLevel,
			String sortDir) {
		int newSubLevel = subLevel + 1;
		Set<Category> children = sortSubCategories(parent.getChildren(), sortDir);

		for (Category category : children) {
			String name = "";
			for (int i = 0; i < newSubLevel; i++) {
				name += "--";
			}
			name += category.getName();

			subHierachicalCategories.add(Category.copyFull(category, name));
			listSubHierachicalCategories(subHierachicalCategories, category, newSubLevel, sortDir);
		}
	}

	public Category save(Category category) {
		return categoryRepository.save(category);
	}

	public List<Category> listCategoriesUsedInForm() {
		List<Category> listCategoriesUsedInForm = new ArrayList<>();
		Iterable<Category> categoriesInDB = categoryRepository.findRootCategories(Sort.by("name").ascending());

		for (Category category : categoriesInDB) {
			if (category.getParent() == null) {
				listCategoriesUsedInForm.add(Category.copyByNameAndId(category));

				var children = sortSubCategories(category.getChildren());

				for (Category child : children) {
					String name = "--" + child.getName();
					listCategoriesUsedInForm.add(Category.copyByNameAndId(child.getId(), name));
					listSubCategories(listCategoriesUsedInForm, category, 1);
				}
			}
		}

		return listCategoriesUsedInForm;

	}

	private void listSubCategories(List<Category> listCategoriesUsedInForm, Category parent, int subLevel) {
		int newSubLevel = subLevel + 1;
		Set<Category> children = parent.getChildren();

		for (Category category : children) {
			String name = "";
			for (int i = 0; i < newSubLevel; i++) {
				name += "--";
			}
			name += category.getName();

			listCategoriesUsedInForm.add(Category.copyByNameAndId(category.getId(), name));
			listSubCategories(listCategoriesUsedInForm, category, newSubLevel);
		}
	}

	public Category get(int id) throws CategoryNotFoundExecption {

		try {

			return categoryRepository.findById(id).get();

		} catch (NoSuchElementException e) {
			throw new CategoryNotFoundExecption("Category ID " + id + " not found");
		}
	}

	public String checkUnique(Integer id, String name, String alias) {
		boolean isCreateNew = (id == null || id == 0);

		Category categoryByName = categoryRepository.findByName(name);

		if (isCreateNew) {
			if (categoryByName != null) {
				return "DuplicateName";
			} else {
				Category categoryByAlias = categoryRepository.findByAlias(alias);
				if (categoryByAlias != null) {
					return "DuplicateAlias";
				}
			}
		} else {
			if (categoryByName != null && categoryByName.getId() != id) {
				return "DuplicateName";
			}

			Category categoryByAlias = categoryRepository.findByAlias(alias);
			if (categoryByAlias != null && categoryByName.getId() != id) {
				return "DuplicateAlias";
			}
		}

		return "OK";
	}

	private SortedSet<Category> sortSubCategories(Set<Category> children) {
		return sortSubCategories(children, "asc");
	}

	private SortedSet<Category> sortSubCategories(Set<Category> children, String sortDir) {

		var sortedTree = new TreeSet<>(new Comparator<Category>() {

			@Override
			public int compare(Category category1, Category category2) {
				if (sortDir.equals("asc")) {
					return category1.getName().compareTo(category2.getName());
				} else {

					return category2.getName().compareTo(category1.getName());
				}
			}
		});

		sortedTree.addAll(children);

		return sortedTree;
	}

	public void updateEnabledStatus(Integer id, Boolean status) throws CategoryNotFoundExecption {
		var category = categoryRepository.findById(id);

		if (category.isEmpty())
			throw new CategoryNotFoundExecption("Category ID " + id + " not found");

		categoryRepository.updateEnabledStatus(id, status);
	}

	public void delete(Integer id) throws CategoryNotFoundExecption {
		var category = categoryRepository.findById(id);
		if (category.isEmpty())
			throw new CategoryNotFoundExecption("Category ID " + id + " not found");
		categoryRepository.deleteById(id);
	}

}
