package com.shopme.admin.category;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.shopme.admin.user.export.Exporter;
import com.shopme.common.entity.Category;

public class CategoryCsvExporter extends Exporter{

	public void export(List<Category> categories, HttpServletResponse response) throws IOException {
		
		setResponseHeader(response, "text/csv", "csv", "Categories_");
		
		ICsvBeanWriter csvBeanWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);
		
		String[] header = {"Category ID", "Category Name"};
		String[] mapedFields = {"id", "name"};
		
		csvBeanWriter.writeHeader(header);
		
		for(Category category : categories) {
			
			category.setName(category.getName().replace("--", " "));
			csvBeanWriter.write(category, mapedFields);
		}
		
		csvBeanWriter.close();
	}
	
}
