package com.mgup.mediascraper.scraper.service.impl;

import java.io.IOException;
import java.util.HashSet;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import com.mgup.mediascraper.scraper.service.ScraperService;

@Service
public class ScraperServiceImpl implements ScraperService {

	private final String GOOGLE_NEWS_PREFIX = "https://news.google.com";

	public String scrapeWebsite() {

		Document document = new Document(null);
		
		//TODO: Basic plan is to go to world news home page of google news and get all the images from that and all the links as well,
		//then go to the links and get all possible images, going to next links from those links might be difficult 
		//as the links from google news land us to pages of news websites, like CNN, Al-Jazeera, The Times of India, etc.
		//to extract links from then and to make that work will take work as I'll have to analyze all of them and figure out the website structures
		//but images hopefully should be in <img> tags and we can extract images from the individual articles :)
		
		//TODO: Add Downloading logic
		//TODO: Add getting metadata logic

		try {
			// downloading the target website with an HTTP GET request
			
			//link for world news in google news
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
			
			System.out.println("Test 5");
			
			System.out.println("images: " + imageLinkSet.size());

			//TODO: Download these images
			
			
			
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
			//TODO: go to the links and try downloading more images atleast to depth 1.
			
		} catch (IOException ex) {
			System.out.println(ex);
		}

		return "success";
	}

}
