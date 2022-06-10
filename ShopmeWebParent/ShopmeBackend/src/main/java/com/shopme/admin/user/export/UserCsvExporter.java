package com.shopme.admin.user.export;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.shopme.common.entity.User;

public class UserCsvExporter extends Exporter {

	public void export(List<User> users, HttpServletResponse response) throws IOException {
				
		setResponseHeader(response, "text/csv", "csv", "Users_");
		
		ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);
		
		String[] header = {"User ID", "E-mail", "First Name", "Last Name", "Roles", "Enabled"};
		String[] mappedFields = {"id", "email", "firstName", "lastName", "roles", "enabled"};
		csvWriter.writeHeader(header);
		for(User user : users) {
			csvWriter.write(user, mappedFields);
		}
		
		
		csvWriter.close();
	}
}
