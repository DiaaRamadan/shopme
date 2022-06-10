package com.shopme.admin;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.web.multipart.MultipartFile;

public class FileUploadUtil {

	public static void save(String uploadDir, String fileName, MultipartFile multipartFile) throws IOException {

		Path uploadedPath = getOrCreateUploadedDirectoryIfNotExists(uploadDir, fileName);

		try (InputStream inputStream = multipartFile.getInputStream()) {
			Path path = uploadedPath.resolve(fileName);
			Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			throw new IOException("Could not save file: " + fileName, e);
		}

	}

	public static void cleanDir(String dir) {
		Path uploadPath = Paths.get(dir);

		try {
			Files.list(uploadPath).forEach(file -> {
				deleteFile(file);
			});
		} catch (IOException e) {
			System.out.println("Could not list directory " + dir);
		}

	}

	private static void deleteFile(Path file) {
		if (!Files.isDirectory(file)) {
			try {

				Files.delete(file);
			} catch (IOException ex) {
				System.out.println("Could not delete " + file);
			}
		}
	}

	private static Path getOrCreateUploadedDirectoryIfNotExists(String uploadDir, String fileName) throws IOException {

		Path uploadPath = Paths.get(uploadDir);

		if (!Files.exists(uploadPath))
			Files.createDirectories(uploadPath);
		return uploadPath;

	}

}
