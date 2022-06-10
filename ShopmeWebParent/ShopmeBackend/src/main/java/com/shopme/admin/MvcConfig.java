package com.shopme.admin;

import java.nio.file.Paths;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {

		var dirName = "user-photos";
		var userDirPath = Paths.get(dirName);
		var userPhotoPath = userDirPath.toFile().getAbsolutePath();
		registry.addResourceHandler("/" + dirName + "/**").addResourceLocations("file:/" + userPhotoPath + "/");
		
		var categoryDir = "../category-images";
		var categoryDirPath = Paths.get(categoryDir);
		var categoryImagePath = categoryDirPath.toFile().getAbsoluteFile();
		
		registry.addResourceHandler("/category-images/**").addResourceLocations("file:/" + categoryImagePath + "/");
	}

}
