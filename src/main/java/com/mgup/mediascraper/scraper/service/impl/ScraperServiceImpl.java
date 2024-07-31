package com.mgup.mediascraper.scraper.service.impl;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.webp.WebpDirectory;
import com.mgup.mediascraper.scraper.service.ScraperService;

@Service
public class ScraperServiceImpl implements ScraperService {

	private final String GOOGLE_NEWS_PREFIX = "https://news.google.com";
	// TODO: Move to application properties to change easily post deployment on
	// server
	private final String IMAGE_DOWNLOAD_DIRECTORY = "C:\\Users\\MONIT\\Documents\\Springing\\mediascraper\\images\\";

	public String scrapeWebsite() {

		Document document = new Document(null);

		// TODO: Basic plan is to go to world news home page of google news and get all
		// the images from that and all the links as well,
		// then go to the links and get all possible images, going to next links from
		// those links might be difficult
		// as the links from google news land us to pages of news websites, like CNN,
		// Al-Jazeera, The Times of India, etc.
		// to extract links from then and to make that work will take work as I'll have
		// to analyze all of them and figure out the website structures
		// but images hopefully should be in <img> tags and we can extract images from
		// the individual articles :)

		// Added basic metadata getting logic and image downloading logic, now will work
		// on the actual requirements of the problem.

		try {
			// downloading the target website with an HTTP GET request

			// link for world news in google news
			Connection connection = Jsoup.connect(
					"https://news.google.com/topics/CAAqKggKIiRDQkFTRlFvSUwyMHZNRGx1YlY4U0JXVnVMVWRDR2dKSlRpZ0FQAQ?ceid=IN:en&oc=3");
			document = connection.userAgent(
					"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36")
					.get();
			Elements imageElements = document.getElementsByTag("img");
			Elements anchorElements = document.getElementsByTag("a");

			HashSet<String> imageLinkSet = new HashSet<>();
			for (Element element : imageElements) {
				String src = element.attr("src");
				if (src.startsWith("/")) {
					String imageUrl = GOOGLE_NEWS_PREFIX + src;
					imageLinkSet.add(imageUrl);
				} else {
					continue;
				}

			}

			System.out.println("images: " + imageLinkSet.size());

			// TODO: Download these images
			for (String imageUrl : imageLinkSet) {
				String downloadedPath = downloadImage(imageUrl);
				System.out.println("image url: " + imageUrl + " downloaded at : " + downloadedPath);
			}

			HashSet<String> linkSet = new HashSet<>();
			for (Element linkElement : anchorElements) {
				String link = linkElement.attr("href");
				if (link.startsWith("./articles")) {
					String articleUrl = GOOGLE_NEWS_PREFIX + link.substring(1);
					linkSet.add(articleUrl);
				} else {
					continue;
				}
			}

			System.out.println("articles: " + linkSet.size());
			// TODO: go to the links and try downloading more images atleast to depth 1.

		} catch (IOException ex) {
			System.out.println(ex);
		}

		return "success";
	}

	private String downloadImage(String imageUrl) {
		try {
			URL url = new URL(imageUrl);
			String fileName = extractFileNameFromUrl(imageUrl);
			File file = new File(IMAGE_DOWNLOAD_DIRECTORY + fileName);

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

	private String extractFileNameFromUrl(String url) {
		String regex = "attachments/([A-Za-z0-9_-]+)=-";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(url);

		if (matcher.find()) {
			return matcher.group(1) + ".jpg";
		} else {
			return UUID.randomUUID().toString() + ".jpg";
		}
	}

	private String extractMetaDataFromImage(File imageFile) {
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

}
