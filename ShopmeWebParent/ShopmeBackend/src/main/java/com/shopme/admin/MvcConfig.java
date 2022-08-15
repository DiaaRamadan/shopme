package com.shopme.admin;

import java.nio.file.Paths;
import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.shopme.admin.paging.PagingAndSortingArgumentResolver;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {

		registerResourceHandler("user-photos", registry);
		registerResourceHandler("../brands-images", registry);
		registerResourceHandler("../category-images", registry);
		registerResourceHandler("../product-images", registry);
		registerResourceHandler("../site-logo", registry);
		
	}

	
	private void registerResourceHandler(String dirName, ResourceHandlerRegistry registry) {
		
		var dirPath = Paths.get(dirName);
		var photoPath = dirPath.toFile().getAbsolutePath();
		var logicalPath = dirName.replace("..", "") + "/**";
		registry.addResourceHandler(logicalPath).addResourceLocations("file:/" + photoPath + "/");
		
	}


	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		
		resolvers.add(new PagingAndSortingArgumentResolver());
	}
	
	
	
}
