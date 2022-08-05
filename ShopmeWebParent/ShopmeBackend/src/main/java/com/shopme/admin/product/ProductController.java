package com.shopme.admin.product;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
import com.shopme.admin.brand.BrandService;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Product;
import com.shopme.common.entity.ProductImage;
import com.shopme.common.exceptions.ProductNotFoundExecption;

@Controller
public class ProductController {

	@Autowired
	private ProductService productService;

	@Autowired
	private BrandService brandService;

	@GetMapping("/products")
	public String listFirstPage(Model model) {
		return listByPage(1, "asc", null, "name", model);
	}

	@GetMapping("/products/page/{pageNum}")
	public String listByPage(@PathVariable("pageNum") int pageNum, @Param("sortDir") String sortDir,
			@Param("keyword") String keyword, @Param("sortField") String sortField, Model model) {

		if (sortDir == null || sortDir.isEmpty())
			sortDir = "asc";
		
		Page<Product> page = productService.listByPage(pageNum, sortField, sortDir, keyword);

		List<Product> products = page.getContent();
		model.addAttribute("products", products);

		System.out.println(page.getSize());

		long startCount = (pageNum - 1) * ProductService.PRODUCT_PER_PAGE + 1;
		long endCount = startCount + ProductService.PRODUCT_PER_PAGE - 1;

		if (endCount > page.getTotalElements()) {
			endCount = page.getTotalElements();
		}

		String reverseDir = sortDir.equals("asc") ? "desc" : "asc";

		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("reverseSortDir", reverseDir);
		model.addAttribute("sortField", "name");
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("keyword", keyword);

		return "/products/products";
	}

	@GetMapping("/products/new")
	public String newProduct(Model model) {

		List<Brand> brands = brandService.listAll();
		Product product = new Product();
		product.setEnabled(true);
		product.setInStock(true);
		product.setCost(0.0f);
		product.setPrice(0.0f);
		product.setDiscountPercent(0.0f);
		model.addAttribute("product", product);
		model.addAttribute("brands", brands);
		model.addAttribute("pageTitle", "Create new Product");
		model.addAttribute("numberOfExistingExtraImages", product.getImages().size());
		return "/products/product_form";
	}

	@PostMapping("/products/save")
	public String save(Product product, RedirectAttributes redirectAttributes,
			@RequestParam("fileImage") MultipartFile multipartFile,
			@RequestParam("extraImage") MultipartFile[] extraImages,
			@RequestParam(name = "detailsIDs", required = false) String[] detailIDs,
			@RequestParam(name = "detailNames", required = false) String[] detailNames,
			@RequestParam(name = "detailValues", required = false) String[] detailValues,
			@RequestParam(name = "imageIDs", required = false) String[] imageIDs,
			@RequestParam(name = "imageNames", required = false) String[] imageNames,
			@RequestParam("create_time") String createdTime) throws IOException, ParseException {

		
		setMainImage(multipartFile, product);
		setExistingExtraImageName(imageIDs, imageNames, product);
		setNewExtraImages(extraImages, product);
		setProductDetails(detailIDs, detailNames, detailValues, product);
		if(createdTime != null) {
			DateFormat formatter = new SimpleDateFormat("yyyy-mm-dd");
			Date date = formatter.parse(createdTime);
			product.setCreatedTime(date);
		}
		Product savedProduct = productService.save(product);
		saveUplodedImages(multipartFile, extraImages, savedProduct);

		deleteExtraImagesWeredRemovedOnForm(product);

		redirectAttributes.addFlashAttribute("message", "The product has been saved successfully");
		return "redirect:/products";
	}

	private void deleteExtraImagesWeredRemovedOnForm(Product product) {

		String extraImageDir = "/product-images/" + product.getId() + "/extras";
		Path dirPath = Paths.get(extraImageDir);

		try {
			Files.list(dirPath).forEach(file -> {
				String fileName = file.toFile().getName();
				if (!product.containsImageName(fileName)) {
					try {
						Files.delete(file);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void setExistingExtraImageName(String[] imageIDs, String[] imageNames, Product product) {

		if (imageIDs == null || imageNames.length == 0)
			return;

		Set<ProductImage> images = new HashSet<>();

		for (int i = 0; i < imageIDs.length; i++) {
			Integer id = Integer.parseInt(imageIDs[i]);
			String name = imageNames[i];
			images.add(new ProductImage(id, name, product));
		}

		product.setImages(images);

	}

	private void setProductDetails(String[] detailIDs, String[] detailNames, String[] detailValues, Product product) {

		if (detailNames == null || detailNames.length == 0)
			return;

		for (int i = 0; i < detailNames.length; i++) {
			String value = detailValues[i];
			String name = detailNames[i];
			Integer id = Integer.parseInt(detailIDs[i]);
			if (id != 0) {
				product.addDetail(id, name, value);
			} else {
				if (!value.isEmpty() || !name.isEmpty()) {
					product.addDetail(name, value);
				}
			}
		}

	}

	private void saveUplodedImages(MultipartFile multipartFile, MultipartFile[] extraImages, Product savedProduct)
			throws IOException {

		if (!multipartFile.isEmpty()) {
			var fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			String uploadDir = "../product-images/" + savedProduct.getId();
			FileUploadUtil.cleanDir(uploadDir);
			FileUploadUtil.save(uploadDir, fileName, multipartFile);
		}

		if (extraImages.length > 0) {
			String uploadDir = "../product-images/" + savedProduct.getId() + "/extras";
			for (MultipartFile extraImage : extraImages) {
				if (!extraImage.isEmpty()) {
					var fileName = StringUtils.cleanPath(extraImage.getOriginalFilename());
					FileUploadUtil.save(uploadDir, fileName, extraImage);
				}
			}
		}
	}

	private void setNewExtraImages(MultipartFile[] extraImages, Product product) {
		if (extraImages.length > 0) {
			for (MultipartFile multipartFile : extraImages) {
				if (!multipartFile.isEmpty()) {
					var fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
					product.addExtraImages(fileName);
				}
			}
		}

	}

	private void setMainImage(MultipartFile multipartFile, Product product) {

		if (!multipartFile.isEmpty()) {

			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			product.setMainImage(fileName);
		}
	}

	@GetMapping("/products/{id}/enabled/{status}")
	public String updateEnabledStatus(@PathVariable("id") Integer id, @PathVariable("status") Boolean status,
			RedirectAttributes redirectAttributes) {
		try {

			productService.updateEnabledStatus(id, status);
			redirectAttributes.addFlashAttribute("message", "Product status updated successfully");

		} catch (ProductNotFoundExecption ex) {
			redirectAttributes.addFlashAttribute("message", ex.getMessage());
		}

		return "redirect:/products";
	}

	@GetMapping("/products/delete/{id}")
	public String delete(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
		try {

			productService.delete(id);
			String extraImages = "../product-images/" + id + "/extras";
			String mainImage = "../product-images/" + id;
			FileUploadUtil.cleanDir(extraImages);
			FileUploadUtil.cleanDir(mainImage);
			redirectAttributes.addFlashAttribute("message", "Product deleted successfully");

		} catch (ProductNotFoundExecption ex) {
			redirectAttributes.addFlashAttribute("message", ex.getMessage());
		}

		return "redirect:/products";
	}

	@GetMapping("/products/edit/{id}")
	public String edit(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {

		try {

			var product = productService.get(id);
			model.addAttribute("product", product);
			model.addAttribute("brands", brandService.listAll());
			model.addAttribute("numberOfExistingExtraImages", product.getImages().size());
			model.addAttribute("pageTitle", "Edit project (ID: " + id + ")");
			return "/products/product_form";

		} catch (ProductNotFoundExecption ex) {
			redirectAttributes.addFlashAttribute("message", ex.getMessage());
		}

		return "redirect:/products";

	}

	@GetMapping("/products/details/{id}")
	public String details(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {

		try {

			model.addAttribute("product", productService.get(id));
			return "products/product_detail_modal";

		} catch (ProductNotFoundExecption ex) {
			redirectAttributes.addFlashAttribute("message", ex.getMessage());
		}

		return "redirect:/products";

	}

}
