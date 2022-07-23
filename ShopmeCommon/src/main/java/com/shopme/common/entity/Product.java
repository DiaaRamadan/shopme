package com.shopme.common.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "products")
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(length = 256, unique = true, nullable = false)
	private String name;

	@Column(length = 256, unique = true, nullable = false)
	private String alias;

	@Column(name = "short_description", length = 512, nullable = false)
	private String shortDescription;

	@Column(length = 4096, name = "full_description", unique = true, nullable = false)
	private String fullDescription;

	@Column(name = "main_image", nullable = false)
	private String mainImage;

	@Column(name = "created_time", nullable = false)
	private Date createdTime;

	@Column(name = "updated_time")
	private Date updatedTime;

	private Boolean enabled;

	@Column(name = "in_stock")
	private Boolean inStock;

	@Column(name = "price")
	private Float price;

	@Column(name = "discount_percent")
	private Float discountPercent;

	@ManyToOne
	@JoinColumn(name = "brand_id")
	private Brand brand;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category_id")
	private Category category;

	private Float length;
	private Float width;
	private Float height;
	private Float weight;

	@Column(name = "average_rating")
	private Float averageRating;

	@Column(name = "review_count")
	private Float reviewCount;

	private Float cost;

	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<ProductImage> images = new HashSet<>();

	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ProductDetail> details = new ArrayList<>();

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getShortDescription() {
		return shortDescription;
	}

	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}

	public String getFullDescription() {
		return fullDescription;
	}

	public void setFullDescription(String fullDescription) {
		this.fullDescription = fullDescription;
	}

	public String getMainImage() {
		return mainImage;
	}

	public void setMainImage(String mainImage) {
		this.mainImage = mainImage;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public Date getUpdatedTime() {
		return updatedTime;
	}

	public void setUpdatedTime(Date updatedTime) {
		this.updatedTime = updatedTime;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public Boolean getInStock() {
		return inStock;
	}

	public void setInStock(Boolean inStock) {
		this.inStock = inStock;
	}

	public Float getPrice() {
		return price;
	}

	public void setPrice(Float price) {
		this.price = price;
	}

	public Float getDiscountPercent() {
		return discountPercent;
	}

	public void setDiscountPercent(Float discountPercent) {
		this.discountPercent = discountPercent;
	}

	public Brand getBrand() {
		return brand;
	}

	public void setBrand(Brand brand) {
		this.brand = brand;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public Float getLength() {
		return length;
	}

	public void setLength(Float length) {
		this.length = length;
	}

	public Float getWidth() {
		return width;
	}

	public void setWidth(Float width) {
		this.width = width;
	}

	public Float getHeight() {
		return height;
	}

	public void setHeight(Float height) {
		this.height = height;
	}

	public Float getWeight() {
		return weight;
	}

	public void setWeight(Float weight) {
		this.weight = weight;
	}

	public Float getAverageRating() {
		return averageRating;
	}

	public void setAverageRating(Float averageRating) {
		this.averageRating = averageRating;
	}

	public Float getReviewCount() {
		return reviewCount;
	}

	public void setReviewCount(Float reviewCount) {
		this.reviewCount = reviewCount;
	}

	public Float getCost() {
		return cost;
	}

	public void setCost(Float cost) {
		this.cost = cost;
	}

	public Set<ProductImage> getImages() {
		return images;
	}

	public void setImages(Set<ProductImage> images) {
		this.images = images;
	}

	public List<ProductDetail> getDetails() {
		return details;
	}

	public void setDetails(List<ProductDetail> details) {
		this.details = details;
	}

	@Transient
	public void addDetail(String name, String value) {
		details.add(new ProductDetail(name, value, this));
	}
	
	@Transient
	public void addDetail(Integer id, String name, String value) {
		details.add(new ProductDetail(id, name, value, this));
	}


	@Transient
	public void addExtraImages(String imageName) {
		images.add(new ProductImage(imageName, this));
	}

	@Transient
	public String getMainImagePath() {
		if (id == null || mainImage == null)
			return "/images/image-thumbnail.png";
		return "/product-images/" + this.id + "/" + this.mainImage;
	}

	@Transient
	public boolean containsImageName(String fileName) {

		Iterator<ProductImage> iterator = images.iterator();

		while (iterator.hasNext()) {
			ProductImage image = iterator.next();
			if (image.getName().equals(fileName))
				return true;
		}
		return false;
	}
	
	@Transient
	public String getShortName() {
		if (name.length() > 70) {
			return name.substring(0, 70).concat("...");
		}
		return name;
	}

	@Transient
	public float getDiscountPrice() {
		if(discountPercent > 0) {
			return price * ((100  - discountPercent) / 100);
		}
		return price;
	}
	
}
