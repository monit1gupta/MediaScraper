package com.mgup.mediascraper.utils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.webp.WebpDirectory;
import com.mgup.mediascraper.constants.ScraperConstants;

public class ScraperUtils {
	
	public static String downloadImage(String imageUrl) {
		try {
			URL url = new URL(imageUrl);
			String fileName = extractFileNameFromUrl(imageUrl);
			File file = new File(ScraperConstants.IMAGE_DOWNLOAD_DIRECTORY + fileName);

			FileUtils.copyURLToFile(url, file);
			System.out.println("Downloaded image: " + fileName);
			String metaDataFromImage = extractMetaDataFromImage(file);
			System.out.println("metadata from image: " + metaDataFromImage);

			return file.getAbsolutePath();

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return "";

	}
	
	private static String extractMetaDataFromImage(File imageFile) {
		Metadata imageMetadata = null;
		StringBuilder metaDataInfo = new StringBuilder();
		try {
			imageMetadata = ImageMetadataReader.readMetadata(imageFile);
		} catch (ImageProcessingException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (imageMetadata != null) {
			// Image Format and Dimensions
			WebpDirectory webpDirectory = imageMetadata.getFirstDirectoryOfType(WebpDirectory.class);
			ExifIFD0Directory exifIFD0Directory = imageMetadata.getFirstDirectoryOfType(ExifIFD0Directory.class);

			if (webpDirectory != null) {
				Iterable<Tag> tags = webpDirectory.getTags();

				for (Iterator<Tag> iterator = tags.iterator(); iterator.hasNext();) {
					Tag tag = (Tag) iterator.next();
					metaDataInfo.append(tag.toString()).append("\n");

				}

			}

			if (exifIFD0Directory != null) {
				metaDataInfo.append("Camera Make: ").append(exifIFD0Directory.getString(ExifIFD0Directory.TAG_MAKE))
						.append("\n");
				metaDataInfo.append("Camera Model: ").append(exifIFD0Directory.getString(ExifIFD0Directory.TAG_MODEL))
						.append("\n");
			}
		}

		return metaDataInfo.toString();
	}
	
	private static String extractFileNameFromUrl(String url) {
		String regex = "attachments/([A-Za-z0-9_-]+)=-";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(url);

		if (matcher.find()) {
			return matcher.group(1) + ".jpg";
		} else {
			return UUID.randomUUID().toString() + ".jpg";
		}
	}

	

}
