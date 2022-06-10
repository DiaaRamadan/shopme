package com.shopme.admin.user.export;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;


public abstract class Exporter {
	
	public void setResponseHeader(HttpServletResponse response, String contentType, String extension, String prefix) throws IOException {
			
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
			String timestamp = dateFormat.format(new Date());
			String fileName = prefix + timestamp + "." + extension;
			
			response.setContentType(contentType);
			response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
	}
}
